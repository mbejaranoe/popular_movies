package com.example.android.popularmovies.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }

    }

    public static class DetailFragment extends Fragment {

        //private String title;
        //private String releaseDate;
        //private String posterPath;
        //private String voteAverage;
        //private String synopsis;
        //private Movie movie;

        public DetailFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra("movie")) {
                Movie movie = intent.getParcelableExtra("movie");
                ((TextView) rootView.findViewById(R.id.title_textView)).setText("Title: " + movie.title);
                ((TextView) rootView.findViewById(R.id.date_textView)).setText("Release date: " + movie.date);
                ImageView img = (ImageView) rootView.findViewById(R.id.poster_ImageView);
                Picasso.with(getContext()).load(movie.posterPath).into(img);
                ((TextView) rootView.findViewById(R.id.vote_Average_textView)).setText("Vote average: " + movie.voteAverage);
                ((TextView) rootView.findViewById(R.id.synopsis_textView)).setText(movie.synopsis);
            }
            return rootView;
        }
    }
}