package az.android.popularmovie;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import az.android.popularmovie.Data.FavoriteMovieContract;
import az.android.popularmovie.Data.FavoriteMovieDbHelper;

public class FavoriteMovieContentProvider extends ContentProvider {

    private FavoriteMovieDbHelper mDbHelper;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int MATCH_URI_FAVORITE_MOVIE = 1;
    private static final int MATCH_URI_FAVORITE_MOVIE_ID = 2;

    static {
        uriMatcher.addURI(FavoriteMovieContract.AUTHORITY, FavoriteMovieContract.PATH_MOVIE, MATCH_URI_FAVORITE_MOVIE);
        uriMatcher.addURI(FavoriteMovieContract.AUTHORITY, FavoriteMovieContract.PATH_MOVIE + "/#", MATCH_URI_FAVORITE_MOVIE_ID);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mDbHelper = new FavoriteMovieDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        int match = uriMatcher.match(uri);
        Cursor cursor;
        switch (match) {
            case MATCH_URI_FAVORITE_MOVIE:
                cursor = db.query(FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MATCH_URI_FAVORITE_MOVIE_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = FavoriteMovieContract.FavoriteMovieEntry.COLUMN_ID + "=?";
                String[] mSelectionArgs = new String[]{id};
                cursor = db.query(FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case MATCH_URI_FAVORITE_MOVIE:
                return "vnd.android.cursor.dir" + "/" + FavoriteMovieContract.AUTHORITY + "/" + FavoriteMovieContract.PATH_MOVIE;
            case MATCH_URI_FAVORITE_MOVIE_ID:
                return "vnd.android.cursor.item" + "/" + FavoriteMovieContract.AUTHORITY + "/" + FavoriteMovieContract.PATH_MOVIE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case MATCH_URI_FAVORITE_MOVIE:
                long id = db.insert(FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FavoriteMovieContract.FavoriteMovieEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);
        int numMovieDeleted;

        switch (match) {
            case MATCH_URI_FAVORITE_MOVIE:
                numMovieDeleted = db.delete(FavoriteMovieContract.FavoriteMovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numMovieDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numMovieDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
