package com.example.android.popularmovies.app;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Movie[]> {

    private static final String LOG_TAG = MovieFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private MovieAdapter mMovieAdapter;
    private ArrayList<Movie> mMovieList;

    private static final int MOVIE_LOADER_ID = 0;

    public MovieFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        updateMovie();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_movie);
        mGridLayoutManager = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mMovieList = new ArrayList<>();

        mMovieAdapter = new MovieAdapter(mMovieList);
        mRecyclerView.setAdapter(mMovieAdapter);

        return rootView;
    }

    private void updateMovie() {
        int loaderId = MOVIE_LOADER_ID;

        LoaderManager loaderManager = this.getLoaderManager();
        LoaderManager.LoaderCallbacks<Movie[]> callback = this;
        loaderManager.initLoader(loaderId, null, callback);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovie();
    }

    @Override
    public Loader<Movie[]> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<Movie[]>(getContext()) {

            Movie[] mMovieData = null;

            @Override
            protected void onStartLoading(){
                if (mMovieData != null) {
                    deliverResult(mMovieData);
                } else {
                    forceLoad();
                }
            }

            private Movie[] getMovieInfoFromJson(String movieInfoJsonStr) throws JSONException {

                final String TMDB_RESULTS = "results";
                final String TMDB_TITLE = "title";
                final String TMDB_RELEASE_DATE = "release_date";
                final String baseUrl = "http://image.tmdb.org/t/p/w185/";
                final String TMDB_POSTER = "poster_path";
                final String TMDB_VOTE_AVERAGE = "vote_average";
                final String TMDB_SYNOPSIS = "overview";
                JSONObject movieJson = new JSONObject(movieInfoJsonStr);
                JSONArray resultsArray = movieJson.getJSONArray(TMDB_RESULTS);
                Movie[] resultStr = new Movie[resultsArray.length()];

                for (int i = 0; i < resultsArray.length(); i++) {
                    resultStr[i] = new Movie();
                    resultStr[i].title = resultsArray.getJSONObject(i).getString(TMDB_TITLE);
                    resultStr[i].date = resultsArray.getJSONObject(i).getString(TMDB_RELEASE_DATE);
                    resultStr[i].posterPath = baseUrl.concat(resultsArray.getJSONObject(i).getString(TMDB_POSTER));
                    resultStr[i].voteAverage = resultsArray.getJSONObject(i).getString(TMDB_VOTE_AVERAGE);
                    resultStr[i].synopsis = resultsArray.getJSONObject(i).getString(TMDB_SYNOPSIS);
                }

                return resultStr;
            }

            @Override
            public Movie[] loadInBackground() {

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                String movieInfoJsonStr;

                try {
                    final String SCHEME_URL = "https";
                    final String PATH_URL = "//api.themoviedb.org/3/movie/";
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    String sortOrder = prefs.getString(getString(R.string.pref_order_key),getString(R.string.pref_order_default));
                    final String apiKey = "api_key";

                    Uri.Builder uriBuilder;
                    uriBuilder = new Uri.Builder();
                    uriBuilder.scheme(SCHEME_URL);
                    uriBuilder.path(PATH_URL+sortOrder);
                    uriBuilder.appendQueryParameter(apiKey, BuildConfig.TMDB_API_KEY);

                    URL url = new URL(uriBuilder.toString());

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        return null;
                    }

                    movieInfoJsonStr = buffer.toString();

                } catch (IOException e) {
                    Log.e("MovieFragment", "Error ", e);
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e("MovieFragment", "Error closing stream", e);
                        }
                    }
                }
                Movie[] result = null;
                try {
                    result = getMovieInfoFromJson(movieInfoJsonStr);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
                return result;
            }

            /*
            @Override
            public void deliverResult(Movie[] data){
                mMovieData = data;
                super.deliverResult(data);
            }
            */
        };
    }

    @Override
    public void onLoadFinished(Loader<Movie[]> loader, Movie[] data) {
        if (data != null){
            ArrayList<Movie> arrayListMovie = new ArrayList<>();
            for (int i = 0; i < data.length; i++) {
                arrayListMovie.add(data[i]);
            }
            mMovieAdapter = new MovieAdapter(arrayListMovie);
            mRecyclerView.setAdapter(mMovieAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<Movie[]> loader) {

    }
}