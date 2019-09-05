package com.java.luolingxiao.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatImageView;

public class Utils {
    public static int widthPixels, heightPixels;
    public static float density;
    static public int dp2px(int dp) {
        return (int) (dp * density + 0.5f);
    }
    static public int getWidthPixels(int percentage) {
         return (int)(percentage / 100.0 * widthPixels);
    }

    static public int getHeightPixels(int percentage) {
        return (int)(percentage / 100.0 * heightPixels);
    }

    static int px2dp(int px) {
        return (int)(px / density +0.5f);
    }
//    https://blog.csdn.net/qq_42022077/article/details/89351273
    static public Bitmap reshapeImage(Bitmap bitmap) {
        //获取原始宽高，并获取宽高中较大的
        int nWidth = bitmap.getWidth();
        int nHeight = bitmap.getHeight();
        int nMax = Math.max(nWidth,nHeight);
        //新建一个正方形的bitmap
        Bitmap  ret_bitmap = Bitmap.createBitmap( nMax, nMax, bitmap.getConfig());

        float left = 0;
        float top = 0;
        if ( nWidth >= nHeight)
        {
            int nLen= nWidth - nHeight ;

            top =  (float) (nLen / 2.0) ;
        }
        else
        {
            int nLen=  nHeight - nWidth  ;

            left =  (float) (nLen / 2.0) ;
        }

        Canvas canvas = new Canvas( ret_bitmap );
        //生成正方形
        canvas.drawBitmap( bitmap, left , top, null );

        canvas = null;
        return ret_bitmap;
    }
}
