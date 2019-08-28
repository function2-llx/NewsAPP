package com.java.luolingxiao.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.java.luolingxiao.R;
import com.java.luolingxiao.SettingsActivity;
import com.java.luolingxiao.events.NightModeChangeEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {
    public SettingsFragment() {}

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

                restart();
                EventBus.getDefault().post(new NightModeChangeEvent());

            }
            return super.onPreferenceTreeClick(preference);
        } catch (Exception e) {
            return super.onPreferenceTreeClick(preference);
        }
    }

    private void restart() {
        startActivity(new Intent(getContext(), SettingsActivity.class));
        Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.fade_in, R.anim.fade_out_bar);
        getActivity().finish();
        //  recreate 仿佛有坑，慎用
        //  getActivity().recreate();
    }
}
