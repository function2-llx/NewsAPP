package com.example.newsapp;

import android.view.View;

public  abstract class DoubleClickListener implements View.OnClickListener {
//    private static final long DOUBLE_TIME = 1000;
    private final long timeout ;
    private long previousTime;


    /**
     *
     * @param timeout 双击间隔（毫秒）
     */
    DoubleClickListener(long timeout) {
        this.timeout = timeout;
        this.previousTime = -1;
    }

    DoubleClickListener() {
        this(1000);
    }

    @Override
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        if (previousTime == -1 || currentTime - previousTime < timeout) {
            onDoubleClick(v);
        }
        previousTime = currentTime;
    }
    public abstract void onDoubleClick(View v);
}
