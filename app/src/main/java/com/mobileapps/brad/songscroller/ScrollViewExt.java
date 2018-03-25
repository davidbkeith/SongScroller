package com.mobileapps.brad.songscroller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.constraint.solver.widgets.Rectangle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TimeUtils;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by brad on 2/2/18.
 */

public class ScrollViewExt extends ScrollView {
    private Boolean enableScrolling = true;
    protected long lastTapTime;
    protected long[] tapIntervals = new long[8];
    protected int tapIntervalIndex;
    private float lineHeight;
    private int scrollLine;

    public int getScrollLine() {
        return scrollLine;
    }

    int[] beatcolor = getResources().getIntArray(R.array.beatcolor);

    public void setLineHeight(float lineHeight) {
        this.lineHeight = lineHeight;
    }

    public float getLineHeight () { return lineHeight; }

    public long getAvgTapSpeed () {

        int Sum = 0;
        int Count = 0;
        for (int i=0; i<8; i++) {
            //long nextVal = Math.abs(tapTimes[i] - tapTimes[i-1]);
            if (tapIntervals[i] < 3000 ) { //// ignore time intervals longer than 3 seconds
                Sum += tapIntervals[i];
                Count++;
            }
        }
        ////// require 4 taps min
        return Count == 8 ? Sum/Count : 0;
    }

    public Boolean isEnableScrolling() {
        return enableScrolling;
    }

    public void setEnableScrolling(Boolean enableScrolling) {
        this.enableScrolling = enableScrolling;
    }

    private ScrollViewListener scrollViewListener = null;
    public ScrollViewExt(Context context) {
        super(context);
    }

    public ScrollViewExt(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ScrollViewExt(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    public ScrollViewListener getScrollViewListener() {
        return scrollViewListener;
    }

   @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        long currTap;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //Log.e("Media", "intercept Action down: set autoscroll to false........");
                setEnableScrolling(false);
                currTap = System.currentTimeMillis();
                tapIntervals[tapIntervalIndex] = currTap - lastTapTime;
                lastTapTime = currTap;
                if(++tapIntervalIndex > 7) {
                    tapIntervalIndex = 0;
                }
          //  case MotionEvent.ACTION_MOVE:
          //      Log.e("Media", "intercept Action move: set autoscroll to false........");
          //      setEnableScrolling(false);
          //  case MotionEvent.ACTION_UP:
          //      Log.e("Media", "intercept Action up: set autoscroll to true........");
          //      setEnableScrolling(true);
         }
        return super.onTouchEvent(ev);
    }

   @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
         //   case MotionEvent.ACTION_DOWN:
         //       Log.e("Media", "onTouchEvent Action down: set autoscroll to false........");
         //       setEnableScrolling(false);
         //   case MotionEvent.ACTION_MOVE:
         //       Log.e("Media", "onTouchEvent Action move: set autoscroll to false........");
         //       setEnableScrolling(false);
            case MotionEvent.ACTION_UP:
         //       Log.e("Media", "onTouchEvent Action up: set autoscroll to ture........");
                setEnableScrolling(true);
         }
       if (scrollViewListener != null) {
           scrollViewListener.onTouchEvent(ev);
       }
       return super.onTouchEvent(ev);
    }

    /////// turn off scroll event handler when app calls scrollTo
    public void scrollTo (int x, int y) {
        //if (isEnableScrolling()) {
            ScrollViewListener scrollViewListener = getScrollViewListener();
            setScrollViewListener(null);
            super.scrollTo(x, y);
            setScrollViewListener(scrollViewListener);
        //}
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }

    void drawBeatIndicator (int beatspan, int beatpos, int linePos, Canvas canvas) {
        if (beatspan > 0) {
            ScrollActivity scrollActivity = (ScrollActivity) scrollViewListener;
            int position = beatpos % beatspan + 1;
            int rectBottom = (int) (linePos * lineHeight);


            int width = scrollActivity.getTextViewWidth() / beatspan * position;

            // create a rectangle that we'll draw later
            Rect rectangle = new Rect(0, rectBottom, width, rectBottom + 4);

            // shrink beat positions to fit into color array available colors
            float shrinkFactor = beatcolor.length/(float) beatspan;
            int colorIndex = (int) ((position-1) * shrinkFactor);

            // create the Paint and set its color
            Paint paint = new Paint();
            paint.setColor(beatcolor[colorIndex]);
            canvas.drawRect(rectangle, paint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //int sideLength = 200;
        int beatpos = 0;
        int beatspan = 0;
        //int position = 0;

        ScrollActivity scrollActivity = (ScrollActivity) scrollViewListener;
        AutoScroll autoScroll = scrollActivity.getAutoScroll();
        scrollLine = (int) (autoScroll.getScrollLine());
       // int rectBottom = (int) ((scrollLine + autoScroll.getScoreData().getScrollStart()*3 + 2) * lineHeight);

        //int y = scrollActivity.getElapsedTime() == 0 ? (int) ((scrollLine + 2) * lineHeight) : 0;

        ////// tempo animation
        if (autoScroll.getBeatInterval() > 0) {
            //f (scrollActivity.isPlaying()) {
           // beatpos = autoScroll.getProgressMeasures();
            beatspan = autoScroll.getLineMeasures();
            if (beatspan > autoScroll.getScoreData().getMeasuresPerLine()) {
                beatspan = autoScroll.getScoreData().getMeasuresPerLine();
            }
            //beatspan = span > autoScroll.getScoreData().getMeasuresPerLine() ? autoScroll.getScoreData().getMeasuresPerLine() : span;
            beatpos = autoScroll.getProgress();
            drawBeatIndicator(beatspan, beatpos, scrollLine + autoScroll.getScoreData().getScrollStart()*3 + 2, canvas);
        }

        if (!scrollActivity.isPlaying() && autoScroll.getBeatInterval() > 0) {
            beatpos = (int) (scrollActivity.getElapsedTime() / autoScroll.getBeatInterval());
            beatspan = autoScroll.getScoreData().getBeats();
            drawBeatIndicator(beatspan, beatpos, 0, canvas);
        }
        //}

        if (scrollActivity.getSong().getPosition() == 0) {
            scrollLine = 0;     /// show stuff above starting line of song
        }

        //if (scrollActivity.getSong().getStartPosition () > 0) {
        scrollTo(0, (int) (scrollLine * lineHeight));
        //}
    }
}
