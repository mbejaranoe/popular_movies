package com.example.android.popularmovies.app.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.example.android.popularmovies.app.BuildConfig;
import com.example.android.popularmovies.app.R;
import com.example.android.popularmovies.app.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Manolo on 24/07/2017.
 */

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIE_INFO_BASE_URL = "https://api.themoviedb.org/3/movie/";

    private static final String API_KEY_PARAM = "api_key";

    /*
    This method will be only used when either "Most popular" or "Highest rated" options are
    selected in the settings menu. The case for the "Favorite" option must be implemented out
    of this method.
     */
    public static URL getURL(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String keyForSortOrder = context.getString(R.string.pref_order_key);
        String defaultOrder = context.getString(R.string.pref_order_default);
        String preferredOrder = sp.getString(keyForSortOrder, defaultOrder);

        return buildURL(preferredOrder);
    }

    private static URL buildURL(String order){
        Uri movieQueryUri = Uri.parse(MOVIE_INFO_BASE_URL + order + "?").buildUpon()
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY)
                .build();

        try {
            URL movieQueryUrl = new URL(movieQueryUri.toString());
            Log.v(TAG, "URL: " + movieQueryUrl);
            return movieQueryUrl;
        } catch (MalformedURLException e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }

    public static byte[] getImageFromURL(String url){
        Bitmap bitmap = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public static ContentValues[] fetchMovieData(Context context){
        String movieInfoJsonStr;

        try {
            movieInfoJsonStr = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.getURL(context));
        } catch (IOException e) {
            Log.e("MovieFragment", "Error ", e);
            return null;
        }
        ContentValues[] result = null;
        try {
            result = getMovieInfoFromJson(movieInfoJsonStr);
        } catch (JSONException e) {
            Log.e("NetworkUtils", e.getMessage(), e);
            e.printStackTrace();
        }
        return result;
    }

    private static ContentValues[] getMovieInfoFromJson(String movieInfoJsonStr) throws JSONException {
        final String TMDB_RESULTS = "results";
        final String TMDB_TITLE = "title";
        final String TMDB_ID = "id";
        final String baseUrl = "http://image.tmdb.org/t/p/w185/";
        final String TMDB_POSTER = "poster_path";
        final String TMDB_SYNOPSIS = "overview";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_POPULARITY = "popularity";
        final String TMDB_VOTE_AVERAGE = "vote_average";
        JSONObject movieJson = new JSONObject(movieInfoJsonStr);
        JSONArray resultsArray = movieJson.getJSONArray(TMDB_RESULTS);
        ContentValues[] resultStr = new ContentValues[resultsArray.length()];

        for (int i = 0; i < resultsArray.length(); i++) {
            resultStr[i].put(MovieContract.MovieEntry.COLUMN_TITLE, resultsArray.getJSONObject(i).getString(TMDB_TITLE));
            resultStr[i].put(MovieContract.MovieEntry.COLUMN_TMDB_ID, resultsArray.getJSONObject(i).getString(TMDB_ID));
            byte[] poster = NetworkUtils.getImageFromURL(baseUrl+resultsArray.getJSONObject(i).getString(TMDB_POSTER));
            resultStr[i].put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, poster);
            resultStr[i].put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, resultsArray.getJSONObject(i).getString(TMDB_SYNOPSIS));
            resultStr[i].put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, resultsArray.getJSONObject(i).getString(TMDB_RELEASE_DATE));
            resultStr[i].put(MovieContract.MovieEntry.COLUMN_POPULARITY, resultsArray.getJSONObject(i).getLong(TMDB_POPULARITY));
            resultStr[i].put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, resultsArray.getJSONObject(i).getLong(TMDB_VOTE_AVERAGE));
            resultStr[i].put(MovieContract.MovieEntry.COLUMN_FAVORITE, 0);
        }

        return resultStr;
    }

}
