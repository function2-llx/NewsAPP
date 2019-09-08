package com.java.luolingxiao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import static com.java.luolingxiao.widget.Utils.dp2px;
import static com.java.luolingxiao.widget.Utils.getWidthPixels;
import static com.java.luolingxiao.widget.Utils.px2dp;

public class TransferTitleBehavior extends CoordinatorLayout.Behavior<TextView> {

    public TransferTitleBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, TextView child, View dependency) {
        return dependency instanceof Toolbar;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, TextView child, View dependency) {
        int yDependency = Utils.px2dp((int) dependency.getY());

        int heightDependency = px2dp(dependency.getHeight());
        int heightChild = px2dp(child.getHeight());
        int bottom = heightDependency - yDependency;
        child.setX(dp2px(24));
        child.setY(dp2px(Math.max(bottom - heightChild, 36)));

        if (bottom < 56 + 65) {
            child.getLayoutParams().width = getWidthPixels(50);
            child.setMaxLines(1);
            child.setX(dp2px((int) (24 + ((float) 56 + 65 - bottom) / 65 * (70 - 24))));

            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) dependency.getParent();
            if (bottom == 56) {
                child.setVisibility(View.INVISIBLE);
                collapsingToolbarLayout.setTitleEnabled(true);
            } else {
                child.setVisibility(View.VISIBLE);
                collapsingToolbarLayout.setTitleEnabled(false);
            }
        } else if (bottom < 56 + 95) {
            child.getLayoutParams().width = Utils.getWidthPixels(90);
            child.setMaxLines(2);
            child.setX(dp2px(24));
        } else {
            child.getLayoutParams().width = Utils.getWidthPixels(90);
            child.setMaxLines(3);
            child.setX(dp2px(24));
        }

        return true;
    }
}