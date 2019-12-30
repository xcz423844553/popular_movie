package az.android.popularmovie;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.databinding.DataBindingUtil;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import az.android.popularmovie.Data.FavoriteMovieContract;
import az.android.popularmovie.Entity.Movie;
import az.android.popularmovie.Entity.Review;
import az.android.popularmovie.Entity.Trailer;
import az.android.popularmovie.databinding.ActivityDetailBinding;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    public static final String TAG_LOG = DetailActivity.class.getSimpleName();
    public static final String TAG_ID = "ID Tag";
    public static final int LOADER_ID_TRAILER = 109;
    public static final int LOADER_ID_REVIEW = 110;

    private Movie movie;
    private RecyclerView mTrailerRecyclerView;
    private TrailerAdapter mTrailerAdapter;
    private RecyclerView mReviewRecyclerView;
    private ReviewAdapter mReviewAdapter;
    private List<Trailer> trailerList;
    private List<Review> reviewList;

    ActivityDetailBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Movie.TAG_MOVIE_ID)) {
            movie = new Movie.Builder(intent.getLongExtra(Movie.TAG_MOVIE_ID, 0L))
                    .setTitle(intent.getStringExtra(Movie.TAG_MOVIE_TITLE))
                    .setImgUrl(intent.getStringExtra(Movie.TAG_MOVIE_IMGURL))
                    .setPoster((Bitmap) intent.getParcelableExtra(Movie.TAG_MOVIE_POSTER))
                    .setOverview(intent.getStringExtra(Movie.TAG_MOVIE_OVERVIEW))
                    .setRating(intent.getDoubleExtra(Movie.TAG_MOVIE_RATING, 0.0d))
                    .setReleaseDate(intent.getStringExtra(Movie.TAG_MOVIE_RELEASE_DATE))
                    .build();
        }

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        mBinding.tvMovieTitle.setText(movie.getTitle());
        if (movie.getPoster() == null) {
            Picasso.get().load(NetworkUtils.BASE_URL_IMAGE + movie.getImgUrl()).into((ImageView) findViewById(R.id.iv_movie_poster));
        } else {
            mBinding.ivMoviePoster.setImageBitmap(movie.getPoster());
        }
        toggleFavoriteButton(false);
        mBinding.tvMovieYear.setText(movie.getReleaseDate().substring(0, 4));
        mBinding.tvMovieRating.setText(movie.getRating() + "/10");

        mBinding.tvMovieSynopsis.setText(movie.getOverview());
        mBinding.tvTrailerLabel.setText(getString(R.string.trailer_label));
        mBinding.tvReviewLabel.setText(getString(R.string.review_label));

        mTrailerRecyclerView = (RecyclerView) findViewById(R.id.rv_trailer);
        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(this);
        mTrailerRecyclerView.setLayoutManager(trailerLayoutManager);
        mTrailerRecyclerView.setHasFixedSize(true);
        mTrailerAdapter = new TrailerAdapter(this);
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);

        mReviewRecyclerView = (RecyclerView) findViewById(R.id.rv_review);
        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this);
        mReviewRecyclerView.setLayoutManager(reviewLayoutManager);
        mReviewRecyclerView.setHasFixedSize(true);
        mReviewAdapter = new ReviewAdapter(this);
        mReviewRecyclerView.setAdapter(mReviewAdapter);

        Bundle bundle = new Bundle();
        bundle.putString(TAG_ID, Long.toString(movie.getId()));

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> trailerLoader = loaderManager.getLoader(LOADER_ID_TRAILER);
        loaderManager.restartLoader(LOADER_ID_TRAILER, bundle, this);
        Loader<String> reviewLoader = loaderManager.getLoader(LOADER_ID_REVIEW);
        loaderManager.restartLoader(LOADER_ID_REVIEW, bundle, this);

        new FavoriteMovieAsyncTask(FavoriteMovieAsyncTask.TASK_ID_CHECK).execute(movie);
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(final int id, @Nullable final Bundle args) {
        return new AsyncTaskLoader<String>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (args == null) return;
                forceLoad();
            }

            @Nullable
            @Override
            public String loadInBackground() {
                String urlStr = null;
                URL url = null;
                switch (id) {
                    case LOADER_ID_TRAILER:
                        urlStr = NetworkUtils.BASE_URL_DETAIL_PREFIX + args.getString(TAG_ID) + NetworkUtils.BASE_URL_TRAILER_POSTFIX + Configs.API_KEY;
                        break;
                    case LOADER_ID_REVIEW:
                        urlStr = NetworkUtils.BASE_URL_DETAIL_PREFIX + args.getString(TAG_ID) + NetworkUtils.BASE_URL_REVIEW_POSTFIX + Configs.API_KEY;
                }
                try {
                    url = new URL(urlStr);
                    String res = NetworkUtils.getResponseFromHttpUrl(url);
                    return res;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        if (data != null && !data.equals("")) {
            switch (loader.getId()) {
                case LOADER_ID_TRAILER:
                    trailerList = NetworkUtils.parseResponseToTrailerList(data);
                    mTrailerAdapter.setTrailers(trailerList);
                    break;
                case LOADER_ID_REVIEW:
                    reviewList = NetworkUtils.parseResponseToReviewList(data);
                    mReviewAdapter.setReviews(reviewList);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    public boolean markFavoriteMovie(Movie movie) {
        byte[] posterData = new byte[0];
        try {
            Bitmap bitmap = Picasso.get().load(NetworkUtils.BASE_URL_IMAGE + movie.getImgUrl()).get();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
            posterData = outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ContentValues values = new ContentValues();
        values.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_ID, movie.getId());
        values.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_POSTER, posterData);
        values.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_TITLE, movie.getTitle());
        values.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_RATING, movie.getRating());
        values.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_SYNOPSIS, movie.getOverview());
        values.put(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        Uri uri = getContentResolver().insert(FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI, values);
        return uri != null;
    }

    public boolean unMarkFavoriteMovie(Movie movie) {
        String mSelection = FavoriteMovieContract.FavoriteMovieEntry.COLUMN_ID + "=?";
        String[] mSelectionArgs = {Long.toString(movie.getId())};
        int mRowsDeleted = 0;
        mRowsDeleted = getContentResolver().delete(
                FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI,
                mSelection,
                mSelectionArgs
        );
        return mRowsDeleted > 0;
    }

    public boolean isFavoriteMovie(Movie movie) {
        String[] mProjection = {FavoriteMovieContract.FavoriteMovieEntry.COLUMN_ID};
        String mSelection = FavoriteMovieContract.FavoriteMovieEntry.COLUMN_ID + "=?";
        String[] mSelectionArgs = {Long.toString(movie.getId())};
        Cursor cursor = getContentResolver().query(
                FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI,
                mProjection,
                mSelection,
                mSelectionArgs,
                null);
        if (cursor == null || cursor.getCount() < 1) {
            return false;
        } else {
            return true;
        }
    }

    public class FavoriteMovieAsyncTask extends AsyncTask<Movie, Void, Boolean> {

        public static final int TASK_ID_MARK = 1;
        public static final int TASK_ID_UNMARK = 2;
        public static final int TASK_ID_CHECK = 3;
        private int CURRENT_TASK_ID;

        public FavoriteMovieAsyncTask(int taskId) {
            this.CURRENT_TASK_ID = taskId;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            toggleFavoriteButton(aBoolean);
        }

        @Override
        protected Boolean doInBackground(Movie... movies) {
            Movie movie = movies[0];
            switch (CURRENT_TASK_ID) {
                case TASK_ID_MARK:
                    return markFavoriteMovie(movie);
                case TASK_ID_UNMARK:
                    return !unMarkFavoriteMovie(movie);
                case TASK_ID_CHECK:
                    return isFavoriteMovie(movie);
                default:
                    return null;
            }
        }
    }

    public void toggleFavoriteButton(boolean isFavorite) {
        if (isFavorite) {
            mBinding.btnMovieFavorite.setText(R.string.label_favorite);
            mBinding.btnMovieFavorite.setBackgroundColor(getColor(R.color.colorFavorite));
            mBinding.btnMovieFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new FavoriteMovieAsyncTask(FavoriteMovieAsyncTask.TASK_ID_UNMARK).execute(movie);
                }
            });
        } else {
            mBinding.btnMovieFavorite.setText(R.string.label_unfavorite);
            mBinding.btnMovieFavorite.setBackgroundColor(getColor(R.color.colorAccent));
            mBinding.btnMovieFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new FavoriteMovieAsyncTask(FavoriteMovieAsyncTask.TASK_ID_MARK).execute(movie);
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }
}
