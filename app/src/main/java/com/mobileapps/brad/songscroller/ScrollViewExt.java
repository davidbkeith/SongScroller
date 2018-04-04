package com.mobileapps.brad.songscroller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by brad on 2/2/18.
 */

public class ScrollViewExt extends ScrollView {
    private Boolean enableScrolling = true;
    private boolean isScrolling;
    protected long lastTapTime;
    protected long[] tapIntervals = new long[8];
    protected int tapIntervalIndex;
    private float lineHeight;
    final private double scrollSensitivity = 2.0;  /// how much is scrolled per finger movement
    private double scrollFactor = 2.0;   /// how fast to scroll - higher is slower, 1 no delay
    private int scrollLine;

    public int getMaxMeasuresPerLine() {
        return maxMeasuresPerLine;
    }

    public void setMaxMeasuresPerLine(int maxMeasuresPerLine) {
        this.maxMeasuresPerLine = maxMeasuresPerLine;
    }

    private int maxMeasuresPerLine = 8;
    boolean startPlayerAfterMove;

    public void setScrollLine(int scrollLine) {
        isScrolling = true;
        if (scrollLine != this.scrollLine) {
            ScrollActivity scrollActivity = (ScrollActivity) scrollViewListener;

            this.scrollLine = scrollLine;
            scrollActivity.getSongLineSettings ().update();
        }
    }

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
       ScrollActivity scrollActivity = (ScrollActivity) scrollViewListener;

        long currTap;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
           //     enableScrolling = false;
                 startPlayerAfterMove = scrollActivity.getSong().isPlaying();
           //     Toast.makeText(scrollActivity, "disabled", Toast.LENGTH_SHORT).show();

                //Log.e("Media", "intercept Action down: set autoscroll to false........");
                currTap = System.currentTimeMillis();
                tapIntervals[tapIntervalIndex] = currTap - lastTapTime;
                lastTapTime = currTap;
                if(++tapIntervalIndex > 7) {
                    tapIntervalIndex = 0;
                }
          //  case MotionEvent.ACTION_MOVE:
          //      Log.e("Media", "intercept Action move: set autoscroll to false........");
            case MotionEvent.ACTION_UP:
                //Toast.makeText(scrollActivity, "disabled", Toast.LENGTH_SHORT).show();
              //  if (startPlayerAfterMove) { scrollActivity.getSong().start(); }
                isScrolling = false;
                //      Log.e("Media", "intercept Action up: set autoscroll to true........");
         }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
       ScrollActivity scrollActivity = (ScrollActivity) scrollViewListener;
       switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                //Toast.makeText(ScrollActivity.this, "up", Toast.LENGTH_SHORT).show();
                if (!isScrolling) {
                    int tapSpeed = (int) getAvgTapSpeed();
                    if (tapSpeed == 1000000000) {
                        scrollActivity.getAutoScroll().setBeatInterval(tapSpeed);
                    } else {
                        scrollActivity.getSong().pause();

                        if (ev.getY() < scrollActivity.getScrollVeiwHeight() * 0.4) {
                             scrollActivity.getAutoScroll().pageUp();
                        } else if (ev.getY() >= scrollActivity.getScrollVeiwHeight() * 0.6) {
                            scrollActivity.getAutoScroll().pageDown();
                        }
                        else {
                            startPlayerAfterMove = !startPlayerAfterMove;
                        }
                    }
                }
             //   Toast.makeText(scrollActivity, "enabled", Toast.LENGTH_SHORT).show();
             //   enableScrolling = true;
                if (startPlayerAfterMove) { scrollActivity.getSong().start(); }
                isScrolling = false;
                break;
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                int size = ev.getHistorySize();
                if (size > 1) {
                    scrollActivity.getSong().pause();
                    scrollActivity.getAutoScroll().setSongPosition(ev.getHistoricalY(size-1) - ev.getHistoricalY(size-2));
                }
                return false;

        }
     //  return super.onTouchEvent(ev);
       return false;
    }

    /////// turn off scroll event handler when app calls scrollTo
    @Override
    public void scrollTo (int x, int y) {
        if (isEnableScrolling() && isScrolling ) {
            ScrollViewListener scrollViewListener = getScrollViewListener();

            setScrollViewListener(null);
            if (!BuildConfig.DEBUG) {
                if (y > getScrollY()) {
                    int scrollto = getScrollY() + (int)((y - getScrollY())/scrollFactor);
                    if (scrollto > y) {
                        super.scrollTo(x, y);
                    }
                    else {
                        super.scrollTo(x, scrollto);
                    }
                }
                else {
                    int scrollto = getScrollY() + (int)((y - getScrollY())/scrollFactor);
                    if (scrollto < y) {
                        super.scrollTo(x, y);
                    }
                    else {
                        super.scrollTo(x, scrollto);
                    }
                }
            }
            else {
                super.scrollTo(x, y);
            }
            setScrollViewListener(scrollViewListener);
        }
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
            //int position = beatpos % beatspan + 1;
            int rectBottom = (int) (linePos * lineHeight);


            int width = scrollActivity.getScrollView().getWidth() / beatspan * beatpos;

            // create a rectangle that we'll draw later
            Rect rectangle = new Rect(0, rectBottom+8, width, rectBottom + 12);

            // shrink beat positions to fit into color array available colors
            float shrinkFactor = beatcolor.length/(float) beatspan;
            int colorIndex = (int) ((beatpos-1) * shrinkFactor);

            // create the Paint and set its color
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.beatcolorgray));
            canvas.drawRect(rectangle, paint);
        }
    }
    /*
    void drawBeatIndicator (int beatspan, int beatpos, int linePos, Canvas canvas) {
        if (beatspan > 0) {
            ScrollActivity scrollActivity = (ScrollActivity) scrollViewListener;
            //int position = beatpos % beatspan + 1;
            int rectBottom = (int) (linePos * lineHeight);


            int width = scrollActivity.getScrollView().getWidth() / beatspan * beatpos;

            // create a rectangle that we'll draw later
            Rect rectangle = new Rect(0, rectBottom+8, width, rectBottom + 12);

            // shrink beat positions to fit into color array available colors
            float shrinkFactor = beatcolor.length/(float) beatspan;
            int colorIndex = (int) ((beatpos-1) * shrinkFactor);

            // create the Paint and set its color
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.beatcolorgray));
            canvas.drawRect(rectangle, paint);
        }
    }*/

    @Override
    protected void onDraw(Canvas canvas) {
        int beatpos = 0;
        int beatspan = 0;

        ScrollActivity scrollActivity = (ScrollActivity) scrollViewListener;
        AutoScroll autoScroll = scrollActivity.getAutoScroll();
        setScrollLine ((int) (autoScroll.getScrollLine()));

        ////// tempo animation
        if (autoScroll.getBeatInterval() > 0) {
            beatspan = autoScroll.getLineMeasures();
            if (beatspan > autoScroll.getScoreData().getMeasuresPerLine()) {
                beatspan = autoScroll.getScoreData().getMeasuresPerLine();
            }

            beatpos = autoScroll.getProgress() - autoScroll.getStartLineMeasure() + 1;
            beatpos = beatpos % maxMeasuresPerLine == 0 ? maxMeasuresPerLine : beatpos % maxMeasuresPerLine;
            drawBeatIndicator(beatspan, beatpos, scrollLine + autoScroll.getScoreData().getScrollStart() + 2, canvas);
        }

        if (!scrollActivity.isPlaying() && autoScroll.getBeatInterval() > 0) {
            beatpos = (int) (scrollActivity.getElapsedTime() / autoScroll.getBeatInterval());
            beatpos = beatpos % autoScroll.getScoreData().getBeats() == 0 ? autoScroll.getScoreData().getBeats() : beatpos % autoScroll.getScoreData().getBeats();
            beatspan = autoScroll.getScoreData().getBeats();
            drawBeatIndicator(beatspan, beatpos, 0, canvas);
        }


       /* ////// tempo animation
        if (autoScroll.getBeatInterval() > 0) {
            beatspan = autoScroll.getLineMeasures();
            if (beatspan > autoScroll.getScoreData().getMeasuresPerLine()) {
                beatspan = autoScroll.getScoreData().getMeasuresPerLine();
            }

            beatpos = autoScroll.getProgress() - autoScroll.getStartLineMeasure() + 1;
            beatpos = beatpos % beatspan+1;
            drawBeatIndicator(beatspan, beatpos, scrollLine + autoScroll.getScoreData().getScrollStart() + 2, canvas);
        }

        if (!scrollActivity.isPlaying() && autoScroll.getBeatInterval() > 0) {
            beatpos = (int) (scrollActivity.getElapsedTime() / autoScroll.getBeatInterval());
            beatpos = beatpos % autoScroll.getScoreData().getBeats() + 1;
            beatspan = autoScroll.getScoreData().getBeats();
            drawBeatIndicator(beatspan, beatpos, 0, canvas);
        }*/

        int scrollTo = scrollLine;
        if (scrollActivity.getSong().getPosition() == 0) {
            scrollTo = 0;     /// show stuff above starting line of song
        }


        //if (enableScrolling) {
            scrollTo(0, (int) (scrollTo * lineHeight));
        //}
    }
}
