package com.example.android.popularmovies.app;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class MovieFragment extends Fragment {

    private ImageListAdapter mMoviesAdapter;

    public MovieFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String[] url = {};

        mMoviesAdapter = new ImageListAdapter(getContext(), url);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(mMoviesAdapter);

        return rootView;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        FetchMovieTask movieTask = new FetchMovieTask();
        movieTask.execute();
    }

    public class FetchMovieTask extends AsyncTask<Void, Void, String[]> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private String[] getMoviePosterPathFromJson(String movieInfoJsonStr) throws JSONException {
            String LOG_TAG = "getMoviePosterPathFromJson";

            final String TMDB_RESULTS = "results";
            final String TMDB_POSTER = "poster_path";
            JSONObject movieJson = new JSONObject(movieInfoJsonStr);
            JSONArray resultsArray = movieJson.getJSONArray(TMDB_RESULTS);
            String[] resultStr = new String[resultsArray.length()];

            String path;
            for (int i = 0; i < resultsArray.length(); i++) {
                path = resultsArray.getJSONObject(i).getString(TMDB_POSTER);
                resultStr[i]=path;
                Log.v(LOG_TAG, path);
            }

            return resultStr;
        }

        @Override
        protected String[] doInBackground(Void... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieInfoJsonStr;

            try {
                // Construct the URL for the TMDB query
                final String SCHEME_URL = "https";
                final String PATH_URL = "//api.themoviedb.org/3/movie/";
                String sortOrder = "popular";
                final String apiKey = "api_key";

                Uri.Builder uriBuilder;
                uriBuilder = new Uri.Builder();
                uriBuilder.scheme(SCHEME_URL);
                uriBuilder.path(PATH_URL+sortOrder);
                uriBuilder.appendQueryParameter(apiKey, BuildConfig.TMDB_API_KEY);

                URL url = new URL(uriBuilder.toString());

                // Create the request to TMDB, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                movieInfoJsonStr = buffer.toString();
                Log.v("MovieFragment", movieInfoJsonStr);
            } catch (IOException e) {
                Log.e("MovieFragment", "Error ", e);
                // If the code didn't successfully get movies info, there's no point in attemping
                // to parse it.
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
                return getMoviePosterPathFromJson(movieInfoJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result){
            final String baseUrl = "http://image.tmdb.org/t/p/w185/";

            if (result != null){
                Log.v("onPostExecute", "dentro del if");
                mMoviesAdapter.clear();
                for (int i = 0; i < result.length; i++) {
                    Log.v("onPostExecute", "dentro del for");
                    Log.v("onPostExecute", "result[" + i + "]=" + result[i]);
                    Log.v("onPostExecute", baseUrl.concat(result[i]));
                    mMoviesAdapter.add(baseUrl.concat(result[i]));
                }
            }
            Log.v("onPostExecute", "fuera del if, saliendo de onPostExecute");
        }
    }

    public class ImageListAdapter extends ArrayAdapter<String> {

        private Context mContext;
        private LayoutInflater inflater;

        private String[] imageUrls;

        public ImageListAdapter(Context context,String[] imageUrls){
            super(context, R.layout.grid_item_movie, imageUrls);
            mContext=context;
            this.imageUrls=imageUrls;

            inflater = LayoutInflater.from(context);
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            Picasso.with(mContext).load(imageUrls[position]).into((ImageView) convertView);

            return convertView;
        }
    }
}