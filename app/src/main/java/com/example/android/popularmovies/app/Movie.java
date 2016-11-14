package com.example.android.popularmovies.app;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Manolo on 12/11/2016.
 */

public class Movie implements Parcelable {
    String title;
    String date;
    String posterPath;
    String voteAverage;
    String synopsis;

    public Movie() {

    }

    public Movie (Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(date);
        dest.writeString(posterPath);
        dest.writeString(voteAverage);
        dest.writeString(synopsis);
    }

    private void readFromParcel(Parcel in) {
        title = in.readString();
        date = in.readString();
        posterPath = in.readString();
        voteAverage = in.readString();
        synopsis = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
