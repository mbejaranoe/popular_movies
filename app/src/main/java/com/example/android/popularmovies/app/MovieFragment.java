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
import java.util.Arrays;

public class MovieFragment extends Fragment {

    private ImageAdapter mMoviesAdapter;

    public MovieFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ImageView img = new ImageView(getContext());
        img.setImageResource(R.drawable.image);

        ImageView[] imageArray = {img, img, img, img, img, img, img, img, img};

        ArrayList<ImageView> gridMovies = new ArrayList<ImageView>(Arrays.asList(imageArray));

        mMoviesAdapter = new ImageAdapter(getActivity(), R.id.grid_item_movie_imageview, gridMovies);

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

        private String[] getMoviePosterFromJson(String movieInfoJsonStr) throws JSONException {
            final String TMDB_RESULTS = "results";
            final String TMDB_POSTER = "poster_path";
            JSONObject movieJson = new JSONObject(movieInfoJsonStr);
            JSONArray resultsArray = movieJson.getJSONArray(TMDB_RESULTS);

            String[] resultStrs = new String[resultsArray.length()];
            for (int i = 0; i < resultsArray.length(); i++) {
                resultStrs[i] = resultsArray.getJSONObject(i).getString(TMDB_POSTER);
            }
            for (String s : resultStrs){
                Log.v(LOG_TAG, "Movie poster path" + s);
            }
            return resultStrs;
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
                return getMoviePosterFromJson(movieInfoJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }
    }
    public class ImageAdapter extends ArrayAdapter<ImageView> {

        private ArrayList<ImageView> imgs;
        private Context mContext;

        public ImageAdapter(Context context, int ImageViewResourceId, ArrayList<ImageView> imgs){
            super(context, ImageViewResourceId, imgs);
            mContext=context;
            this.imgs=imgs;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;

            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            //Picasso.with(context).load("[url]").into(imageView);
            imageView.setImageResource(R.drawable.image);

            return imageView;
        }
    }
}