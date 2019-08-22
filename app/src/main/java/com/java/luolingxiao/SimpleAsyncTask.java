package com.java.luolingxiao;

import android.os.Handler;

public abstract class SimpleAsyncTask {
    protected abstract void doInBackGround();
    protected void onComplete() {}

    public void start() {
        Handler handler = new Handler();
        new Thread(() -> {
            doInBackGround();
            handler.post(this::onComplete);
        }).start();
    }
}
