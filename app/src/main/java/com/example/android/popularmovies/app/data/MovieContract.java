package com.example.android.popularmovies.app.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Manolo on 24/07/2017.
 */

/**
 * Defines table and column names for the movie database.
 */

public class MovieContract {

    /* This is the authority for the content provider */
    public static final String AUTHORITY = "com.example.android.popularmovies.app";

    /* The base for the uri */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    /* The path for all the movies */
    public static final String PATH_MOVIES = "movies";

    /* The path for all the trailers */
    public static final String PATH_TRAILERS = "trailers";

    /* The path for all the reviews */
    public static final String PATH_REVIEWS = "reviews";

    /* Inner class that defines the table contents of the movie table */
    public static final class MovieEntry implements BaseColumns {

        /* MovieEntry content URI = base content URI + path */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        /* Used internally as the name of the movie table. */
        public static final String TABLE_NAME = "movies";

        /* The movie title */
        public static final String COLUMN_TITLE = "title";

        /* The TMDB id, though it's an integer, we don't have to operate with it, so we will store it as TEXT */
        public static final String COLUMN_TMDB_ID = "tmdbId";

        /* The movie poster. It will be stored as a BLOB in the database*/
        public static final String COLUMN_MOVIE_POSTER = "moviePoster";

        /* The backdrop image. It will be stored as a BLOB in the database */
        public static final String COLUMN_BACKDROP_IMAGE = "backdropImage";

        /* The synopsis will be stored as TEXT */
        public static final String COLUMN_SYNOPSIS = "synopsis";

        /* The release date will be stored as TEXT, later on we will localize the app so it should change */
        public static final String COLUMN_RELEASE_DATE = "releaseDate";

        /* The popularity will be stored as a real so we can order the movie on this criteria */
        public static final String COLUMN_POPULARITY = "popularity";

        /* The vote average will be stored as a real so we can order the movie on this criteria */
        public static final String COLUMN_VOTE_AVERAGE = "voteAverage";

        /* The favorite indicates wether the movie has been marked as favorite (value integer 1) or not (value integer 0)*/
        public static final String COLUMN_FAVORITE = "favorite";
    }

    public static final class TrailersEntry implements BaseColumns {

        /* TrailersEntry content URI = base content URI + path */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILERS).build();

        /* Used internally as the name of the trailers table. */
        public static final String TABLE_NAME = "trailers";

        /* The TMDB id, though it's an integer, we don't have to operate with it, so we will store it as TEXT */
        public static final String COLUMN_TMDB_ID = "tmdbId";

        /* The URL of the trailer, stored as a String */
        public static final String COLUMN_URL = "url";

        /* The name of the trailer */
        public static final String COLUMN_NAME = "name";
    }

    public static final class ReviewsEntry implements BaseColumns {

        /* ReviewsEntry content URI = base content URI + path */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();

        /* Used internally as the name of the reviews table. */
        public static final String TABLE_NAME = "reviews";

        /* The TMDB id, though it's an integer, we don't have to operate with it, so we will store it as TEXT */
        public static final String COLUMN_TMDB_ID = "tmdbId";

        /* The name of the author */
        public static final String COLUMN_AUTHOR = "author";

        /* The text of the review itself */
        public static final String COLUMN_REVIEW = "review";

    }
}