package com.example.android.popularmovies.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

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

public class MovieFragment extends Fragment {

    private ImageListAdapter mMoviesAdapter;

    public MovieFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        updateMovie();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMoviesAdapter = new ImageListAdapter(getContext(), new ArrayList<Movie>());
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(mMoviesAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Movie movie = mMoviesAdapter.getItem(i);
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("movie", movie);
                startActivity(intent);
            }
        });

        return rootView;
    }



    private void updateMovie() {
        FetchMovieTask movieTask = new FetchMovieTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortCriteria = prefs.getString(getString(R.string.pref_order_key),getString(R.string.pref_order_default));
        movieTask.execute(sortCriteria);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovie();
    }

    public class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

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
        protected Movie[] doInBackground(String... params) {

            if (params.length==0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieInfoJsonStr;

            try {
                final String SCHEME_URL = "https";
                final String PATH_URL = "//api.themoviedb.org/3/movie/";
                String sortOrder = params[0];
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

            try {
                return getMovieInfoFromJson(movieInfoJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] result){

            if (result != null){
                mMoviesAdapter.clear();
                for (int i = 0; i < result.length; i++) {
                    mMoviesAdapter.add(result[i]);
                }
            }
        }
    }

    public class ImageListAdapter extends ArrayAdapter<Movie> {

        private Context mContext;
        private LayoutInflater inflater;

        private ArrayList<Movie> movies;

        public ImageListAdapter(Context context,ArrayList<Movie> movies){
            super(context, R.layout.grid_item_movie, movies);
            mContext=context;
            this.movies=movies;

            inflater = LayoutInflater.from(context);
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.grid_item_movie, parent, false);
            }

            Picasso.with(mContext).load(movies.get(position).posterPath).into((ImageView) convertView);

            return convertView;
        }
    }
}