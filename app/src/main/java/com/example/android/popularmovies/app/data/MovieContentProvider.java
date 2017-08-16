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

    public static final int TRAILERS = 200;
    public static final int TRAILERS_WITH_ID = 201;

    public static final int REVIEWS = 300;
    public static final int REVIEWS_WITH_ID = 301;

    /* The member variable for the UriMatcher */
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES + "/#", MOVIES_WITH_ID);

        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_TRAILERS, TRAILERS);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_TRAILERS + "/#", TRAILERS_WITH_ID);

        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_REVIEWS, REVIEWS);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_REVIEWS + "/#", REVIEWS_WITH_ID);

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
        long id;
        String tableName;
        Uri contentUri;

        switch (match) {
            case MOVIES:
                tableName = MovieContract.MovieEntry.TABLE_NAME;
                contentUri = MovieContract.MovieEntry.CONTENT_URI;
                break;
            case TRAILERS:
                tableName = MovieContract.TrailersEntry.TABLE_NAME;
                contentUri = MovieContract.TrailersEntry.CONTENT_URI;
                break;
            case REVIEWS:
                tableName = MovieContract.ReviewsEntry.TABLE_NAME;
                contentUri = MovieContract.ReviewsEntry.CONTENT_URI;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        id = db.insert(tableName, null, contentValues);
        if (id > 0) {
            returnUri = ContentUris.withAppendedId(contentUri,id);
        } else {
            throw new android.database.SQLException("Failed: " + id);
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
        String id;
        String mSelection;
        String[] mSelectionArgs;

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
                id = uri.getPathSegments().get(1);
                mSelection = "_id=?";
                mSelectionArgs = new String[]{id};
                retCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case TRAILERS:
                retCursor = db.query(MovieContract.TrailersEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case TRAILERS_WITH_ID:
                id = uri.getPathSegments().get(1);
                mSelection = "_id=?";
                mSelectionArgs = new String[]{id};
                retCursor = db.query(MovieContract.TrailersEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case REVIEWS:
                retCursor = db.query(MovieContract.ReviewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case REVIEWS_WITH_ID:
                id = uri.getPathSegments().get(1);
                mSelection = "_id=?";
                mSelectionArgs = new String[]{id};
                retCursor = db.query(MovieContract.ReviewsEntry.TABLE_NAME,
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
        int rowsUpdated;
        int match = sUriMatcher.match(uri);
        String tableName;

        switch (match){
            case MOVIES:
                tableName = MovieContract.MovieEntry.TABLE_NAME;
                break;
            case TRAILERS:
                tableName = MovieContract.TrailersEntry.TABLE_NAME;
                break;
            case REVIEWS:
                tableName = MovieContract.ReviewsEntry.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }

        rowsUpdated = db.update(tableName,
                contentValues,
                selection,
                selectionArgs);

        if (rowsUpdated > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
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
            case TRAILERS:
                return "vnd.android.cursor.dir" + "/" + MovieContract.AUTHORITY + "/" + MovieContract.PATH_TRAILERS;
            case TRAILERS_WITH_ID:
                return "vnd.android.cursor.item" + "/" + MovieContract.AUTHORITY + "/" + MovieContract.PATH_TRAILERS;
            case REVIEWS:
                return "vnd.android.cursor.dir" + "/" + MovieContract.AUTHORITY + "/" + MovieContract.PATH_REVIEWS;
            case REVIEWS_WITH_ID:
                return "vnd.android.cursor.item" + "/" + MovieContract.AUTHORITY + "/" + MovieContract.PATH_REVIEWS;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values){
        final SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rowsInserted;
        long _id;
        String tableName;

        switch (match){
            case MOVIES:
                tableName = MovieContract.MovieEntry.TABLE_NAME;
                break;
            case TRAILERS:
                tableName = MovieContract.TrailersEntry.TABLE_NAME;
                break;
            case REVIEWS:
                tableName = MovieContract.ReviewsEntry.TABLE_NAME;
                break;
            default:
                return super.bulkInsert(uri, values);
        }

        db.beginTransaction();
        rowsInserted = 0;

        try {
            for (ContentValues value : values) {
                _id = db.insert(tableName, null, value);
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
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
