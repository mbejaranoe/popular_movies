package com.example.android.popularmovies.app.utilities;

import android.content.ContentValues;
import android.content.Context;

import com.example.android.popularmovies.app.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Created by Manolo on 24/07/2017.
 */

public final class TMDBJsonUtils {

    private static final String TMDB_MESSAGE_CODE = "status_code";

    /* Movie information. Each movie info is an element of the "results" array */
    private static final String TMDB_RESULT = "results";

    private static final String TMDB_TITLE = "title";
    private static final String TMDB_ID = "id";
    private static final String TMDB_POSTER_PATH = "poster_path";
    private static final String TMDB_SYNOPSIS = "overview";
    private static final String TMDB_RELEASE_DATE = "release_date";
    private static final String TMDB_POPULARITY = "popularity";
    private static final String TMDB_VOTE_AVERAGE = "vote_average";

    public static ContentValues[] getMovieContentValuesFromJson(Context context, String movieInfoJsonStr) throws JSONException {

        JSONObject movieInfoJson = new JSONObject(movieInfoJsonStr);

        /* Is there an error? */
        if (movieInfoJson.has(TMDB_MESSAGE_CODE)) {
            int errorCode = movieInfoJson.getInt(TMDB_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        /* Get the JSON object representing info from one item (movie) */
        JSONArray movieArray = movieInfoJson.getJSONArray(TMDB_RESULT);

        ContentValues[] movieContentValues = new ContentValues[movieArray.length()];

        for (int i = 0; i < movieArray.length(); i++) {
            /* These are the values that will be collected */
            String title;
            String id;
            String posterPath;
            String synopsis;
            String releaseDate;
            String popularity;
            String voteAverage;

            JSONObject movieInfo = movieArray.getJSONObject(i);

            title = movieInfo.getString(TMDB_TITLE);
            id = movieInfo.getString(TMDB_ID);
            posterPath = movieInfo.getString(TMDB_POSTER_PATH);
            synopsis  = movieInfo.getString(TMDB_SYNOPSIS);
            releaseDate = movieInfo.getString(TMDB_RELEASE_DATE);
            popularity = movieInfo.getString(TMDB_POPULARITY);
            voteAverage = movieInfo.getString(TMDB_VOTE_AVERAGE);

            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
            movieValues.put(MovieContract.MovieEntry.COLUMN_TMDB_ID, id);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, posterPath);
            movieValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, synopsis);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, popularity);
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);

            movieContentValues[i] = movieValues;
        }

        return movieContentValues;
    }
}
