package com.nightly.qqmusic.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.nightly.qqmusic.R;

/**
 * Created by Nightly on 2016/10/15.
 */

public class CircleProgress extends View {
    private final String TAG = "Test";
    private Paint circlePaint;
    private Paint simplePaint;

    private float cx;
    private float cy;
    private float radius;
    private long duration;
    private long currentMills;

    public void setAngle(long angle) {
        this.angle = angle;
    }

    private long angle;

    public void setCurrentMills(long currentMills) {
        this.currentMills = currentMills;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public CircleProgress(Context context) {
        this(context, null);
    }

    public CircleProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        circlePaint = new Paint();
        circlePaint.setColor(getResources().getColor(R.color.grayDarkTransparent));//颜色
        circlePaint.setAntiAlias(true);//抗锯齿
        circlePaint.setStyle(Paint.Style.STROKE);//空心画笔
        circlePaint.setStrokeWidth(3);

        simplePaint = new Paint();
        simplePaint.setColor(getResources().getColor(R.color.greenLight));//颜色
        simplePaint.setAntiAlias(true);//抗锯齿
        simplePaint.setStyle(Paint.Style.STROKE);//
        simplePaint.setStrokeWidth(3);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        cx = getWidth() / 2f;
        cy = getHeight() / 2f;
        radius = Math.min(cx, cy);
//        Log.e(TAG, "onDraw: cx:"+cx+"/,cy:"+cy+"/,currentMills:"+currentMills+",duration:/"+duration+"/,radius:"+radius+",asd:"+(currentMills/(float)duration)*360f);

//        canvas.drawLine(cx, cy, ((float) (cx + radius * Math.sin(Math.toRadians((currentMills / (float) duration) * 360f)) * 0.9)), ((float) (cy - radius * Math.cos(Math.toRadians((currentMills / (float) duration) * 360f)) * 0.9)), circlePaint);
        RectF rectF = new RectF(((float) (getWidth() * 0.1)), ((float) (getHeight() * 0.1)), ((float) (getWidth() * 0.9)), ((float) (getHeight() * 0.9)));

//        canvas.drawArc(rectF, 0, 360, false, circlePaint);
        canvas.drawArc(rectF, -90, (currentMills / (float) duration) * 360f, false, simplePaint);
        postInvalidateDelayed(100);
    }

}
