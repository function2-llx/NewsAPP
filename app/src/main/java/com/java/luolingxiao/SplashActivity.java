package com.java.luolingxiao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.mob.moblink.MobLink;
import com.mob.moblink.RestoreSceneListener;
import com.mob.moblink.Scene;
import com.yanzhenjie.nohttp.InitializationConfig;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.OkHttpNetworkExecutor;

public class SplashActivity extends DeFaultActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //  必须放在 super.onCreate 前面，否则第一次打开程序时会产生两个 MainActivity
        if (isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        super.onCreate(savedInstanceState);
        NoHttp.initialize(
                InitializationConfig.newBuilder(this).
                networkExecutor(new OkHttpNetworkExecutor())
                .connectionTimeout(5 * 1000)
                .readTimeout(5 * 1000)
                .build()
            );
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        MobLink.setRestoreSceneListener(new RestoreSceneListener() {
            @Override
            public Class<? extends Activity> willRestoreScene(Scene scene) {
                return NewsActivity.class;
            }

            @Override
            public void completeRestore(Scene scene) {

            }

            @Override
            public void notFoundScene(Scene scene) {

            }
        });

        // init
        getDataRepository();

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 1500);
    }
}
