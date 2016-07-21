package com.accordhk.SnapNEat.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by jm on 17/3/16.
 */
public class RoundedNetworkImageView extends NetworkImageView {

    public RoundedNetworkImageView(Context context) {
        super(context);
    }

    public RoundedNetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundedNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStrokeWidth(10);
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.STROKE);

        float radius = 30.0f; // angle of round corners
        Path clipPath = new Path();
        RectF rect = new RectF(0, 0, this.getWidth()-1, this.getHeight()-1);
        clipPath.addRoundRect(rect, radius, radius, Path.Direction.CW);
        canvas.clipPath(clipPath);
        super.onDraw(canvas);
    }
}
