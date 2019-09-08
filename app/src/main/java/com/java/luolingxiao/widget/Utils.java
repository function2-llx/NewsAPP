package com.java.luolingxiao.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Utils {
    public static int widthPixels, heightPixels;
    public static float density;
    static public int dp2px(int dp) {
        return (int) (dp * density + 0.5f);
    }
    static public int getWidthPixels(int percentage) {
         return (int)(percentage / 100.0 * widthPixels);
    }

    static int px2dp(int px) {
        return (int)(px / density +0.5f);
    }
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

        return ret_bitmap;
    }
}
