package com.java.luolingxiao.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.alibaba.fastjson.parser.deserializer.CollectionResolveFieldDeserializer;
import com.java.luolingxiao.DefaultActivity;
import com.java.luolingxiao.R;

public class RoundImageView extends AppCompatImageView {


    float width, height;

    public RoundImageView(Context context) {
        this(context, null);
        init(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context, attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (Build.VERSION.SDK_INT < 18) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(DefaultActivity.getAnyActivity().getColor(R.color.item_background));
        int dp = 25;
        if (width >= dp && height > dp) {
            Path path = new Path();
            //四个圆角
            path.moveTo(dp, 0);
            path.lineTo(width - dp, 0);
            path.quadTo(width, 0, width, dp);
            path.lineTo(width, height - dp);
            path.quadTo(width, height, width - dp, height);
            path.lineTo(dp, height);
            path.quadTo(0, height, 0, height - dp);
            path.lineTo(0, dp);
            path.quadTo(0, 0, dp, 0);

            canvas.clipPath(path);
        }
        super.onDraw(canvas);
    }

}
