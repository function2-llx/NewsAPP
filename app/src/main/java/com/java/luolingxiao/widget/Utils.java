package com.java.luolingxiao.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

public class Utils {


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
