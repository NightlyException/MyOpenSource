package com.itemp.myproject.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.itemp.myproject.R;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by idea on 2016/10/7.
 */
public class LrcView extends View {

    private static final String TAG = "test";
    private Paint paint;
    private Paint hPaint;
    private int width;
    private int height;
    private Set<Long> longs;
    private int highLightTextY;
    private int currentPosition = 0;
    private int lastPosition = 0;
    private int scrollCount = 0;
    private Long firstMillis = -1L;
    private Long lastMillis = -1L;
    private boolean scrollingFinished;
    private int scountDownCount = 0;
    private String currentLine = "";
    private float highLightX;
    private float highLightY;
    private boolean scrollFinished = false;
    private int thisLineTotalScrollY = 0;

    public LrcView(Context context) {
        this(context, null);
    }

    public LrcView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LrcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(getResources().getColor(R.color.whiteTransparent));
        paint.setTextSize(50);

        hPaint = new Paint();
        hPaint.setAntiAlias(true);
        hPaint.setColor(getResources().getColor(R.color.toolbarGreen));
        hPaint.setTextSize(50);

        if (myTextProgressBar != null) {
            myTextProgressBar.setVisibility(showCurrentLineProgress ? VISIBLE : INVISIBLE);
        }
    }

    List<Map<String, Object>> data;
    long currentMillis = 0;

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
        firstMillis = ((Long) data.get(0).get("start"));
        lastMillis = ((Long) data.get(data.size() - 1).get("start"));
    }

    public void setCurrentMillis(long currentMillis) {
        this.currentMillis = currentMillis;

        //当前行号
        getCurrentPosition();

        //当前歌词
        currentLine = ((String) data.get(currentPosition).get("text"));

        //在每句前500毫秒进行滚动
        long start = ((Long) data.get(currentPosition).get("start"));
        int value = (currentMillis - start) > 500 ?
                currentPosition * 80 :
                lastPosition*80 + (int) (((currentPosition - lastPosition) * 80 - 0) * ((currentMillis - start) / 500f));
        setScrollY(value);

        //就位后，将当前行号赋值给lastPosition
        if (getScrollY() == currentPosition * 80) {
            lastPosition = currentPosition;
            myTextProgressBar.setVisibility(VISIBLE);
        }else {
            myTextProgressBar.setVisibility(INVISIBLE);
        }
    }

    MyTextProgressBar myTextProgressBar;

    public void setMyTextProgressBar(MyTextProgressBar myTextProgressBar) {
        this.myTextProgressBar = myTextProgressBar;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Log.d(TAG, "lrcview onDraw: ");

        if (width == 0 || height == 0) {
            width = getWidth();
            height = getHeight();
            highLightTextY = height / 2 + 10;
        }

        if (data == null) {
            String text = "暂无歌词";
            float textWidth = hPaint.measureText(text);
            canvas.drawText(text, (width - textWidth) / 2, highLightTextY, paint);
            return;
        }

//        drawTextDynamic(canvas);
        drawTextStatic(canvas);
        showTextProgress();
    }

    private void scrollIfNeeded() {
        int offset = 80 * (currentPosition - lastPosition);
        if (thisLineTotalScrollY < offset) {
            scrollBy(0, offset / 20);
            thisLineTotalScrollY += offset / 20;
        } else {
            scrollTo(0, 80 * (currentPosition - lastPosition));
            scrollFinished = true;
            lastPosition = currentPosition;
        }

//        if (scrollFinished) {
//            myTextProgressBar.setVisibility(VISIBLE);
//        } else {
//            myTextProgressBar.setVisibility(INVISIBLE);
//        }
    }

    private void getCurrentPosition() {
        if (currentMillis <= firstMillis) {
            currentPosition = 0;
        } else if (currentMillis >= lastMillis) {
            currentPosition = data == null ? 0 : data.size() - 1;
        } else {
            currentPosition = -1;
            for (int i = 0; i < data.size(); i++) {
                Map<String, Object> map = data.get(i);
                Long start = (Long) map.get("start");
                Long end = (Long) map.get("end");
                if (currentMillis > start && currentMillis < end) {
                    currentPosition = i;
                }
            }

            if (currentPosition == -1) {
                currentPosition = data.size() - 1;
            }
        }
    }

    private void showTextProgress() {
        Log.e(TAG, "showTextProgress:1");
        if (showCurrentLineProgress && myTextProgressBar != null) {
            Log.e(TAG, "showTextProgress:2");
//            myTextProgressBar.setVisibility(VISIBLE);
            try {
                Log.e(TAG, "showTextProgress:3");
                Map<String, Object> map = data.get(currentPosition);
                Long start = (Long) map.get("start");
                Long end = (Long) map.get("end");

                myTextProgressBar.setText(currentLine);
                myTextProgressBar.setProgress((int) ((float) (currentMillis - start) / (end - start) * 100));
                myTextProgressBar.invalidate();
            } catch (Exception e) {
                Log.e(TAG, "showTextProgress:4");
                e.printStackTrace();
            }
        }
    }

    boolean showCurrentLineProgress = true;

    private void drawTextDynamic(Canvas canvas) {

        //画高亮行
        float textWidth = hPaint.measureText(currentLine);
        if (showCurrentLineProgress && myTextProgressBar != null) {
            canvas.drawText(currentLine, (width - textWidth) / 2, highLightTextY, paint);
        } else {
            canvas.drawText(currentLine, (width - textWidth) / 2, highLightTextY, hPaint);
        }

        //下三行
        for (int i = 1; i < 10; i++) {

            int position = currentPosition + i;
            if (position > -1 && position < data.size()) {
                String textBottom = ((String) data.get(position).get("text"));
                float textBottomWidth = paint.measureText(textBottom);
                canvas.drawText(textBottom, (width - textBottomWidth) / 2, highLightTextY + 80 * i, paint);
            }
        }

        //上三行
        for (int i = 1; i < 10; i++) {
            int position = currentPosition - i;
            if (position > -1 && position < data.size()) {
                String textTop = ((String) data.get(position).get("text"));
                float textTopWidth = paint.measureText(textTop);
                canvas.drawText(textTop, (width - textTopWidth) / 2, highLightTextY - 80 * i, paint);
            }
        }
    }

    private void drawTextStatic(Canvas canvas) {
        for (int i = 0; i < data.size(); i++) {
            String text = ((String) data.get(i).get("text"));
            float textTopWidth = paint.measureText(text);
            canvas.drawText(text, (width - textTopWidth) / 2, highLightTextY + 80 * i, paint);
        }
    }

}
