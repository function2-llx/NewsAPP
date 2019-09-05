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

    /**
     * 处于中心时候原始X轴
     */
    private int mOriginalHeaderX = 0;
    /**
     * 处于中心时候原始Y轴
     */
    private int mOriginalHeaderY = 0;


    public TransferTitleBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, TextView child, View dependency) {
        return dependency instanceof Toolbar;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, TextView child, View dependency) {
//        // 计算X轴坐标
//        if (mOriginalHeaderX == 0) {
//            this.mOriginalHeaderX = dependency.getWidth() / 2 - child.getWidth() / 2;
//        }
//        // 计算Y轴坐标
//        if (mOriginalHeaderY == 0) {
//            mOriginalHeaderY = dependency.getHeight() - child.getHeight();
//        }
//        //X轴百分比
//        float mPercentX = dependency.getY() / mOriginalHeaderX;
//        if (mPercentX >= 1) {
//            mPercentX = 1;
//        }
//        //Y轴百分比
//        float mPercentY = dependency.getY() / mOriginalHeaderY;
//        if (mPercentY >= 1) {
//            mPercentY = 1;
//        }
//
//        float x = mOriginalHeaderX - mOriginalHeaderX * mPercentX;
//        if (x <= child.getWidth()) {
//            x = child.getWidth();
//        }
//        // TODO 头像的放大和缩小没做
//
//        child.setX(x);
//        child.setY(mOriginalHeaderY - mOriginalHeaderY * mPercentY);
//        return true;

        int yDependency = Utils.px2dp((int) dependency.getY());

        int xChild = px2dp((int) child.getX());
        int yChild = px2dp((int) child.getY());

        int heightDependency = px2dp(dependency.getHeight());
//        child.getHeight()
        int heightChild = px2dp(child.getHeight());
//        System.out.println(yDependency + " " + dependency.getX() + " " + px2dp(dependency.getHeight()));

//        System.out.println("text_height " + heightChild);


//        System.out.println("xChild " + xChild);
//        System.out.println("yChild " + yChild);

        int bottom = heightDependency - yDependency;


//        System.out.println("bottom " + dependency.getRawX);
//        System.out.println("bottom " + bottom);
        child.setX(dp2px(24));
        child.setY(dp2px(Math.max(bottom - heightChild, 36)));

        int margin = 48;

        if (bottom < 56 + 65) {
            child.getLayoutParams().width = getWidthPixels(50);
            child.setMaxLines(1);
            child.setX(dp2px((int) (24 + ((float) 56 + 65 - bottom) / 65 * (70 - 24))));

            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) dependency.getParent();

//            System.out.println("title " + collapsingToolbarLayout.getTitle());
//            child.bringToFront(); //uesless
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
//            child.getLayoutParams().width = dp2px(360);
            child.setMaxLines(3);
            child.setX(dp2px(24));
        }

        if (yDependency > heightDependency - 56 * 2) {
//            child.setX(dp2px(24 + (int)((56 * 2 - yDependency) / 56.0 * margin)));
        }

        return true;
    }
}