package com.example.newsapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.githang.statusbar.StatusBarCompat;

public class DeFaultActivity extends AppCompatActivity {
    protected boolean isNightMode() {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.preferences_name), 0);
        return preferences.getBoolean(getString(R.string.preference_night_mode), false);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.colorPrimary));
    }
}
