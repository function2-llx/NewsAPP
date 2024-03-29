package com.java.luolingxiao;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.githang.statusbar.StatusBarCompat;
import com.java.luolingxiao.bean.ChannelBean;
import com.java.luolingxiao.database.AppDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressLint("Registered")
public class DefaultActivity extends AppCompatActivity {
    private static Set<DefaultActivity> activitySet = new HashSet<>();

    public static boolean offline = false;
    //    public static Context getContext() { return context; }
    public static DefaultActivity getAnyActivity() {
        return activitySet.iterator().next();
    }

    protected boolean isNightMode() {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.preferences_name), 0);
        return preferences.getBoolean(getString(R.string.preference_night_mode), false);
    }

    public boolean isSaveTrafficMode() {
        SharedPreferences preferences = getSharedPreferences(getString(R.string.preferences_name), 0);
        return preferences.getBoolean(getString(R.string.preference_save_traffic), false);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        activitySet.add(this);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.colorPrimary));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activitySet.remove(this);
    }

    public List<ChannelBean> getAllChannels() {
        List<ChannelBean> ret = new ArrayList<>();
        String[] fixedChannels = getResources().getStringArray(R.array.fixed_channels), unfixedChannels = getResources().getStringArray(R.array.unfixed_channels);
        for (String name: fixedChannels) {
            ret.add(new ChannelBean(name, true));
        }
        for (String name: unfixedChannels) {
            ret.add(new ChannelBean(name, false));
        }
        return ret;
    }

    public DataRepository getDataRepository() {
        return DataRepository.getInstance(AppDatabase.getInstance(getApplicationContext()));
    }
}
