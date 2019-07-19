package com.example.newsapp;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.githang.statusbar.StatusBarCompat;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class DefaultSwipeBackActivity extends SwipeBackActivity {
    protected boolean isNightMode() {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.preferences_name), 0);
        return preferences.getBoolean(getString(R.string.preference_night_mode), false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(true);
        SwipeBackLayout swipeBackLayout = getSwipeBackLayout();
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.colorPrimary));
    }
}
