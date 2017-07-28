package com.example.android.popularmovies.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import com.example.android.popularmovies.app.data.MovieContract;

/**
 * Created by Manolo on 17/07/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = value.toString();

        // Since we only have a list preference, look up the correct display value in
        // the preference's 'entries' list (since they have separate labels/values).
        ListPreference listPreference = (ListPreference) preference;
        int prefIndex = listPreference.findIndexOfValue(stringValue);
        if (prefIndex >= 0) {
            preference.setSummary(listPreference.getEntries()[prefIndex]);
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_general);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            Preference p = prefScreen.getPreference(i);
            String value = sharedPreferences.getString(p.getKey(), "");
            setPreferenceSummary(p, value);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // unregister the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // register the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_order_key))){
            updateMovieSortOrder(sharedPreferences, key);
        } else {
            return;
        }
        Preference preference = findPreference(key);
        if (null != preference) {
                setPreferenceSummary(preference, sharedPreferences.getString(key, ""));
        }
    }

    public void updateMovieSortOrder(SharedPreferences sharedPreferences, String key){
        Activity activity = getActivity();

        String pref_order = sharedPreferences.getString(key, getString(R.string.pref_order_default));

        String[] projection = new String[]{MovieContract.MovieEntry.COLUMN_MOVIE_POSTER};

        String selection;
        String[] selectionArgs;
        if (pref_order.equals(getString(R.string.pref_order_favorite))){
            selection = MovieContract.MovieEntry.COLUMN_FAVORITE + "=?";
            selectionArgs = new String[]{"1"};
        } else {
            selection = null;
            selectionArgs = null;
        }

        String sortOrder;
        if (pref_order.equals(getString(R.string.pref_order_rated))){
            sortOrder = MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
        } else {
            sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
        }
    }

}
