package com.example.android.popularmovies.app.data;

import android.provider.BaseColumns;

/**
 * Created by Manolo on 24/07/2017.
 */

/**
 * Defines table and column names for the movie database.
 */

public class MovieContract {

    /* Inner class that defines the table contents of the movie table */
    public static final class MovieEntry implements BaseColumns {

        /* Used internally as the name of the movie table. */
        public static final String TABLE_NAME = "movie";

        /* The movie title */
        public static final String COLUMN_TITLE = "title";

        /* The TMDB id, though it's an integer, we don't have to operate with it, so we will store it as TEXT */
        public static final String COLUMN_TMDB_ID = "tmdbId";

        /* The movie poster. It will be stored as a BLOB in the database*/
        public static final String COLUMN_MOVIE_POSTER = "moviePoster";

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
}