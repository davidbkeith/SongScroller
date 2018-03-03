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
    private int scrollLinePos;
    private int beatpos;
    private int beatspan;
    int[] beatcolor = getResources().getIntArray(R.array.beatcolor);

    public int getBeatspan() {
        return beatspan;
    }

    public void setBeatspan(int beatspan) {
        this.beatspan = beatspan;
    }

    public int getBeatpos() {
        return beatpos;
    }

    public void setBeatpos(int beatpos) {
        this.beatpos = beatpos;
    }

    public float getLineHeight() {
        return lineHeight;
    }

    public void setLineHeight(float lineHeight) {
        this.lineHeight = lineHeight;
    }

    public int getScrollLinePos() {
        return scrollLinePos;
    }

    public void setScrollLinePos(int scrollLinePos) {
        this.scrollLinePos = scrollLinePos;
    }

    public long getAvgTapSpeed () {
      /*  Log.e("Media", String.format("7 to 8: %d", tapIntervals[7]));
        Log.e("Media", String.format("6 to 7: %d", tapIntervals[6]));
        Log.e("Media", String.format("5 to 6: %d", tapIntervals[5]));
        Log.e("Media", String.format("4 to 5: %d", tapIntervals[4]));
        Log.e("Media", String.format("3 to 4: %d", tapIntervals[3]));
        Log.e("Media", String.format("2 to 3: %d", tapIntervals[2]));
        Log.e("Media", String.format("1 to 2: %d", tapIntervals[1]));
        Log.e("Media", String.format("0 to 1: %d", tapIntervals[0]));
*/
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

    private ScrollViewListener scrollViewListener = null, scrollViewListener2;
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

    @Override
    protected void onDraw(Canvas canvas) {
        //int sideLength = 200;
        ScrollActivity scrollActivity = (ScrollActivity) scrollViewListener;

        int scrollto = scrollActivity.getAutoScroll().getScrollPosition ();
        scrollTo(0, scrollto);

        int x = 0;
        int y = scrollActivity.getElapsedTime() == 0 ? (int) ((scrollLinePos + 2) * lineHeight) : 0;

        if (beatspan > 0) {
            int position = beatpos % beatspan + 1;

            int width = scrollActivity.getTextViewWidth() / beatspan * position;

            // create a rectangle that we'll draw later
            Rect rectangle = new Rect(x, y, width, y + 4);

            // shrink beat positions to fit into color array available colors
            float shrinkFactor = beatcolor.length/(float) beatspan;
            int colorIndex = (int) ((position-1) * shrinkFactor);

            // create the Paint and set its color
            Paint paint = new Paint();
            paint.setColor(beatcolor[colorIndex]);
            canvas.drawRect(rectangle, paint);
        }
    }
}
