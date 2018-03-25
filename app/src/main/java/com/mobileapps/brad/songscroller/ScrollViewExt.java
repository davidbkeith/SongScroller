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
import android.widget.Toast;

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
    private double scrollFactor = 3.0;   /// how fast to scroll - higher is slower, 1 no delay

    public void setScrollLine(int scrollLine) {
        //enableScrolling = true;
        isScrolling = true;
        this.scrollLine = scrollLine;
    }

    private int scrollLine;
    private double touchY, offset;

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
                //touchY = ev.getY() - offset;
                ///setEnableScrolling(false);
                currTap = System.currentTimeMillis();
                tapIntervals[tapIntervalIndex] = currTap - lastTapTime;
                lastTapTime = currTap;
                if(++tapIntervalIndex > 7) {
                    tapIntervalIndex = 0;
                }
          //  case MotionEvent.ACTION_MOVE:
          //      Log.e("Media", "intercept Action move: set autoscroll to false........");
          //      setEnableScrolling(false);
            case MotionEvent.ACTION_UP:
                isScrolling = false;
                //      Log.e("Media", "intercept Action up: set autoscroll to true........");
          //      setEnableScrolling(true);
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
                        // String bpmtext = String.format("%d", (60000 / BeatInterval));
                        // textEditBPM.setText(bpmtext);
                    } else {
                       // scrollFactor = 1.8;
                        int currentline = getScrollLine();
                     /*   offset = ev.getY();// - touchY;
                        scrollLine = scrollActivity.getScrollLine(offset);
                        if (scrollLine != currentline) {
                            //isScrolling = true;
                            ///// this check prevents spurious data from causing jittery screen
                            //if ((offset > 0 && scrollLine - currentline == 1) || (offset < 0 && scrollLine - currentline == -1) )
                                return super.onTouchEvent(ev);
                        }*/


                            int newPos;
                        //int numLines = scrollActivity.getLinesPerPage();
                        if (ev.getY() < scrollActivity.getScrollVeiwHeight() * 0.5) {
                            //Toast.makeText(scrollActivity, "scroll up", Toast.LENGTH_SHORT).show();
                            scrollActivity.getAutoScroll().pageUp();
                            //offset = (newPos + scrollLine + scrollActivity.getAutoScroll().getScoreData().getScrollStart()*3 - 2) * getLineHeight();
                            //offset = newPos * getLineHeight();
                        } else if (ev.getY() >= scrollActivity.getScrollVeiwHeight() * 0.5) {
                            //Toast.makeText(scrollActivity, "scroll down", Toast.LENGTH_SHORT).show();
                            //int beforeScroll = getTop();
                            scrollActivity.getAutoScroll().pageDown();
                            //int newScrollLine = scrollActivity.getAutoScroll().getScrollLine();
                            //offset = 4 * getLineHeight();
                            //scrollActivity.getAutoScroll().setSeekBarProgress();
                            //setScrollLine ((int) (scrollActivity.getAutoScroll().getScrollLine()));
                            //scrollTo(0, (int) (scrollLine * lineHeight));

                            //offset = (newPos + scrollLine) * getLineHeight();
                            //offset = (newPos + scrollLine + scrollActivity.getAutoScroll().getScoreData().getScrollStart()*3 - 2) * getLineHeight();
                            //offset = scrollLine * lineHeight - beforeScroll;
                            //offset = scrollActivity.getAutoScroll().getScrollLine() * lineHeight;
                        }
                        //else {
                        //Toast.makeText(ScrollActivity.this, "expand-contract", Toast.LENGTH_SHORT).show();
                        //expand();
                        //}*/
                    }
                }
                isScrolling = false;
                break;
            case MotionEvent.ACTION_DOWN:
                // if we can scroll pass the event to the superclass
          //      if (enableScrolling) return super.onTouchEvent(ev);
                // only continue to handle the touch event if scrolling enabled
         //       return false; // mScrollable is always false at this point        //       Log.e("Media", "onTouchEvent Action down: set autoscroll to false........");
         //       setEnableScrolling(false);
         //   case MotionEvent.ACTION_MOVE:
         //       Log.e("Media", "onTouchEvent Action move: set autoscroll to false........");
         //       setEnableScrolling(false);
                break;
            case MotionEvent.ACTION_MOVE:

                int currentline = getScrollLine();
            //    int pointerCount = ev.getPointerCount();
                int size = ev.getHistorySize();
                if (size > 1) {
                    scrollFactor = 6;
                    offset = (ev.getHistoricalY(size-1) - ev.getHistoricalY(size-2))*scrollSensitivity;

                    int newMeasure = scrollActivity.getAutoScroll().getProgress() + (int) (offset/scrollActivity.getAutoScroll().getScrollYmin());
                    //Toast.makeText(scrollActivity, Double.toString(offset), Toast.LENGTH_SHORT).show();

                    scrollActivity.getSong().setStartPosition(newMeasure * scrollActivity.getAutoScroll().getTimePerMeasure());
                    int newscrollLine = scrollActivity.getAutoScroll().getScrollLine(newMeasure);

                    if (newscrollLine != currentline) {
                        //setScrollLine(newscrollLine);
                        //isScrolling = true;
                        ///// this check prevents spurious data from causing jittery screen
                        if ((offset > 0 && scrollLine - currentline == 1) || (offset < 0 && scrollLine - currentline == -1)) {
                            setScrollLine(newscrollLine);
                            return super.onTouchEvent(ev);
                        }
                    }
                }
                return false;

        }
     //  if (scrollViewListener != null) {
     //      scrollViewListener.onTouchEvent(ev);
      // }
     //  return super.onTouchEvent(ev);
       return false;
    }

    /////// turn off scroll event handler when app calls scrollTo
    @Override
    public void scrollTo (int x, int y) {
        if (isEnableScrolling() && isScrolling ) {
            ScrollViewListener scrollViewListener = getScrollViewListener();
            setScrollViewListener(null);
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
            //super.scrollTo(x, y);
            setScrollViewListener(scrollViewListener);
            //enableScrolling = false;
            //isScrolling = false;
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
        setScrollLine ((int) (autoScroll.getScrollLine()));
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
        //if (enableScrolling) {
            scrollTo(0, (int) (scrollLine * lineHeight));
        //}
        //}
        //isScrolling = false;

    }
}
