package com.example.newsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        getPreferenceManager().setSharedPreferencesName(getString(R.string.preferences_name));
        setPreferencesFromResource(R.xml.preferences, s);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        try {
            String key = preference.getKey();
            if (key.equals(getString(R.string.preference_night_mode))) {
                boolean mode = Objects.requireNonNull(getActivity()).getSharedPreferences(getString(R.string.preferences_name), 0).getBoolean(getString(R.string.preference_night_mode), false);
                if (mode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                        .beginTransaction()
                        .remove(this)
                        .commit();
                getActivity().getWindow().setWindowAnimations(R.style.Widget_AppCompat_ActionBar_Solid);
                getActivity().recreate();
            }
            return super.onPreferenceTreeClick(preference);
        } catch (Exception e) {
            return super.onPreferenceTreeClick(preference);
        }
    }
}
