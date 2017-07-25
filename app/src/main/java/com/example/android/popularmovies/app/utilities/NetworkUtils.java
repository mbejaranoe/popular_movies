package com.example.android.popularmovies.app.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.example.android.popularmovies.app.BuildConfig;
import com.example.android.popularmovies.app.R;

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
}
