package com.example.android.popularmovies.app.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Manolo on 25/07/2017.
 */

public class MovieContentProvider extends ContentProvider {

    private MovieDBHelper mMovieDBHelper;

    /* Integer constants for the UriMatcher */
    public static final int MOVIES = 100;
    public static final int MOVIES_WITH_ID = 101;

    /* The member variable for the UriMatcher */
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES + "/#", MOVIES_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mMovieDBHelper = new MovieDBHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIES:
                long id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI,id);
                } else {
                    throw new android.database.SQLException("Failed: " + id);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return  returnUri;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mMovieDBHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case MOVIES:
                retCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};
                retCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
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
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
        int moviesUpdated;
        int match = sUriMatcher.match(uri);

        switch (match){
            case MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};

                moviesUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME,
                        contentValues,
                        mSelection,
                        mSelectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }

        if (moviesUpdated > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return moviesUpdated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);

        switch (match){
            case MOVIES:
                return "vnd.android.cursor.dir" + "/" + MovieContract.AUTHORITY + "/" + MovieContract.PATH_MOVIES;
            case MOVIES_WITH_ID:
                return "vnd.android.cursor.item" + "/" + MovieContract.AUTHORITY + "/" + MovieContract.PATH_MOVIES;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values){
        final SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        switch (match){
            case MOVIES:
                db.beginTransaction();
                int rowsInserted = 0;

                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (rowsInserted > 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsInserted;
            default:
                return super.bulkInsert(uri, values);
        }

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
