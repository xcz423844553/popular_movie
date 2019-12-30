package az.android.popularmovie;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import az.android.popularmovie.Data.FavoriteMovieContract;
import az.android.popularmovie.Entity.Movie;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>, GridViewHolder.GridItemClickListner {

    private static final String TAG_LOG = MainActivity.class.getSimpleName();
    private RecyclerView mList;
    private GridAdapter mAdapter;
    private List<Movie> movieList;
    public static final int ID_LOADER = 108;
    public static final String TAG_SORT = "Sort Tag";
    public static final int TAG_SORT_POPULAR = 1;
    public static final int TAG_SORT_HIGH_RATED = 2;
    public static final int TAG_SORT_FAVORITE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieList = new ArrayList<>();
        mList = (RecyclerView) findViewById(R.id.rv_main);
        int numOfCol = 2;
        GridLayoutManager layoutManager = new GridLayoutManager(this, numOfCol);
        mList.setLayoutManager(layoutManager);
        mAdapter = new GridAdapter(this, this);
        mList.setAdapter(mAdapter);

        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            sortMovie(TAG_SORT_POPULAR);
        } else {
            sortMovie(TAG_SORT_FAVORITE);
        }
    }

    private void sortMovie(int tagSortId) {
        if (tagSortId == TAG_SORT_FAVORITE) {
            new FavoriteMovieQueryAsyncTask().execute();
            return;
        }
        Bundle bundle = new Bundle();
        switch (tagSortId) {
            case TAG_SORT_POPULAR:
                bundle.putInt(TAG_SORT, TAG_SORT_POPULAR);
                break;
            case TAG_SORT_HIGH_RATED:
                bundle.putInt(TAG_SORT, TAG_SORT_HIGH_RATED);
                break;
        }
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> loader = loaderManager.getLoader(ID_LOADER);
        if (loader == null) {
            loaderManager.initLoader(ID_LOADER, bundle, this);
        } else {
            loaderManager.restartLoader(ID_LOADER, bundle, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable final Bundle args) {
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
                int sortTagId = args.getInt(TAG_SORT, TAG_SORT_POPULAR);
                String urlStr = null;
                switch (sortTagId) {
                    case TAG_SORT_POPULAR:
                        urlStr = NetworkUtils.BASE_URL_POPULAR + Configs.API_KEY;
                        break;
                    case TAG_SORT_HIGH_RATED:
                        urlStr = NetworkUtils.BASE_URL_HIGHRATED + Configs.API_KEY;
                        break;
                }
                try {
                    URL url = new URL(urlStr);
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
            movieList = NetworkUtils.parseResponseToMovieList(data);
            mAdapter.swapArray(movieList);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemSelected = item.getItemId();
        switch (itemSelected) {
            case R.id.action_sort_popular:
                sortMovie(TAG_SORT_POPULAR);
                break;
            case R.id.action_sort_highest_rated:
                sortMovie(TAG_SORT_HIGH_RATED);
                break;
            case R.id.action_sort_favorite:
                sortMovie(TAG_SORT_FAVORITE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGridItemClick(Movie movie) {
        Class destinationActivity = DetailActivity.class;
        Intent intent = new Intent(this, destinationActivity);
        intent.putExtra(Movie.TAG_MOVIE_ID, movie.getId());
        intent.putExtra(Movie.TAG_MOVIE_TITLE, movie.getTitle());
        intent.putExtra(Movie.TAG_MOVIE_IMGURL, movie.getImgUrl());
        intent.putExtra(Movie.TAG_MOVIE_OVERVIEW, movie.getOverview());
        intent.putExtra(Movie.TAG_MOVIE_RATING, movie.getRating());
        intent.putExtra(Movie.TAG_MOVIE_RELEASE_DATE, movie.getReleaseDate());
        intent.putExtra(Movie.TAG_MOVIE_POSTER, movie.getPoster());
        startActivity(intent);
    }

    public class FavoriteMovieQueryAsyncTask extends AsyncTask<Long, Void, List<Movie>> {

        @Override
        protected void onPostExecute(List<Movie> list) {
            super.onPostExecute(list);
            if (list != null && list.size() > 0) {
                mAdapter.swapArray(list);
            }
        }

        @Override
        protected List<Movie> doInBackground(Long... movieIds) {
            String mSelection = null;
            String[] mSelectionArgs = null;
            if (movieIds.length > 0) {
                mSelection = FavoriteMovieContract.FavoriteMovieEntry.COLUMN_ID + "=?";
                mSelectionArgs = new String[]{Long.toString(movieIds[0])};
            }
            Cursor cursor = getContentResolver().query(
                    FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI,
                    null,
                    mSelection,
                    mSelectionArgs,
                    null);
            if (cursor == null || cursor.getCount() < 1) {
                return null;
            }
            List<Movie> movieList = new ArrayList<>();
            while (cursor.moveToNext()) {
                byte[] bitmapData = cursor.getBlob(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_POSTER));
                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
                Movie movie = new Movie.Builder(cursor.getLong(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_ID)))
                        .setTitle(cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_TITLE)))
                        .setOverview(cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_SYNOPSIS)))
                        .setRating(cursor.getDouble(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_RATING)))
                        .setReleaseDate(cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE)))
                        .setPoster(bitmap)
                        .build();
                movieList.add(movie);
            }
            cursor.close();
            return movieList;
        }
    }
}