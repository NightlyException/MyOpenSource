package com.nightly.qqmusic.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.nightly.qqmusic.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Nightly on 2016/10/13.
 */

public class LrcView extends View {

    private List<Map<String, Object>> data;
    private int width;
    private int height;
    private long currentMillis;
    private int currentLinePosition;
    private Paint paint;
    private Paint hPaint;

    public void setCurrentMillis(long currentMillis) {
        this.currentMillis = currentMillis;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

    public LrcView(Context context) {
        this(context, null);
    }

    public LrcView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LrcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    private void initPaint() {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setTextSize(30);

        hPaint = new Paint();
        hPaint.setColor(getResources().getColor(R.color.toolbarGreen));
        hPaint.setAntiAlias(true);
        hPaint.setTextSize(30);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * 获取控件的宽和高
         */
        if (width == 0 || height == 0) {
            width = getWidth();
            height = getHeight();
        }

        if (data == null) {
            drawTextCenter(canvas, "暂无歌词", paint);
            return;
        }

        /**
         * 找出当前位置
         */
        for (int i = 0; i < data.size(); i++) {
            Map<String, Object> map = data.get(i);
            Long start = (Long) map.get("start");
            Long end = (Long) map.get("end");
            if (currentMillis >= start && currentMillis <= end) {
                currentLinePosition = i;
                break;
            }
        }

        //当前行高亮显示
        drawCurrentLine(canvas, null);
        //画上下各10行
        drawOtherLines(canvas);

    }

    /**
     * 画当前行的上下10行
     * @param canvas
     */
    private void drawOtherLines(Canvas canvas) {
        for (int i = 0; i < currentLinePosition + 11; i++) {
            if (i > -1 && i < data.size()) {
                String text = (String) data.get(i).get("text");
                Rect rect = new Rect();
                hPaint.getTextBounds(text, 0, text.length(), rect);
                canvas.drawText(text, width / 2 - rect.width() / 2, height / 2 + rect.height() / 2 + 30 * (i - currentLinePosition), paint);
            }
        }
        for (int i = currentLinePosition - 1; i > currentLinePosition - 11; i--) {
            if (i > -1 && i < data.size()) {
                String text = (String) data.get(i).get("text");
                Rect rect = new Rect();
                hPaint.getTextBounds(text, 0, text.length(), rect);
                canvas.drawText(text, width / 2 - rect.width() / 2, height / 2 + rect.height() / 2 - 30 * (currentLinePosition - i), paint);
            }
        }
    }

    /**
     * 画当前行
     * @param canvas
     * @param text
     */
    private void drawCurrentLine(Canvas canvas, String text) {
        long start = (long) data.get(currentLinePosition).get("start");
        long end = (long) data.get(currentLinePosition).get("end");
        String line = (String) data.get(currentLinePosition).get("text");
        if(listener!=null){
            listener.onLyricChanged(start,end,currentMillis,line);
        }else {
            drawTextCenter(canvas, line, hPaint);
        }
    }

    /**
     * 画中间行
     * @param canvas
     * @param text
     * @param paint
     */
    private void drawTextCenter(Canvas canvas, String text, Paint paint) {
        Rect rect = new Rect();
        hPaint.getTextBounds(text, 0, text.length(), rect);
        canvas.drawText(text, width / 2 - rect.width() / 2, height / 2 - rect.height() / 2+2, paint);
    }

    OnLyricChangedListener listener;

    public void setListener(OnLyricChangedListener listener) {
        this.listener = listener;
    }

    public interface OnLyricChangedListener{
        void onLyricChanged(long start,long end,long currentMillis,String text);
    }

}
