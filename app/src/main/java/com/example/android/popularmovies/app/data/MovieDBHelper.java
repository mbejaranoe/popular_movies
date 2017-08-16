package com.example.android.popularmovies.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Manolo on 24/07/2017.
 */

public class MovieDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 3;

    public MovieDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase){
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_TMDB_ID + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_POSTER + " BLOB NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_BACKDROP_IMAGE + " BLOB NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_FAVORITE + " INTEGER NOT NULL, " +
                "UNIQUE (" + MovieContract.MovieEntry.COLUMN_TMDB_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " +
                MovieContract.TrailersEntry.TABLE_NAME + " (" +
                MovieContract.TrailersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.TrailersEntry.COLUMN_TMDB_ID + " TEXT NOT NULL, " +
                MovieContract.TrailersEntry.COLUMN_URL + " TEXT NOT NULL, " +
                MovieContract.TrailersEntry.COLUMN_NAME + " TEXT NOT NULL);";

        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " +
                MovieContract.ReviewsEntry.TABLE_NAME + " (" +
                MovieContract.ReviewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.ReviewsEntry.COLUMN_TMDB_ID + " TEXT NOT NULL, " +
                MovieContract.ReviewsEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                MovieContract.ReviewsEntry.COLUMN_REVIEW + " TEXT NOT NULL);";

        sqliteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqliteDatabase.execSQL(SQL_CREATE_TRAILERS_TABLE);
        sqliteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqliteDatabase, int i, int i1){
        sqliteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        sqliteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.TrailersEntry.TABLE_NAME);
        sqliteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewsEntry.TABLE_NAME);
        onCreate(sqliteDatabase);
    }
}