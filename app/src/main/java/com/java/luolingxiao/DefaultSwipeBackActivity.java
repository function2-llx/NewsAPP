package com.java.luolingxiao;

import android.annotation.SuppressLint;
import android.os.Bundle;

import me.imid.swipebacklayout.lib.SwipeBackLayout;

@SuppressLint("Registered")
public class DefaultSwipeBackActivity extends SwipeBackActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(true);
        SwipeBackLayout swipeBackLayout = getSwipeBackLayout();
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
    }
}
