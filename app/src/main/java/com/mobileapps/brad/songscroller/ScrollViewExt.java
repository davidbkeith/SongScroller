package com.mobileapps.brad.songscroller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.Log;
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
    final private double scrollSensitivity = 2.0;  /// how much is scrolled per finger movement
    private double scrollFactor = 2.0;   /// how fast to scroll - higher is slower, 1 no delay
    private int scrollLine;
    private boolean showSoftKeyboard;

    boolean startPlayerAfterMove;

   /* @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int proposedheight = MeasureSpec.getSize(heightMeasureSpec);
        final int actualHeight = getHeight();

        ScrollActivity scrollActivity = (ScrollActivity) scrollViewListener;
        if (actualHeight > proposedheight){
            // Keyboard is shown
            //scrollActivity.getTextView().setFocusableInTouchMode(true);
            showSoftKeyboard = true;
       //     Toast.makeText(scrollActivity, "show keyboard", Toast.LENGTH_SHORT).show();

        } else {
            // Keyboard is hidden
            if (showSoftKeyboard) {
                //scrollActivity.getTextView().setFocusable(false);
                showSoftKeyboard = false;
                Toast.makeText(scrollActivity, "hide keyboard", Toast.LENGTH_SHORT).show();
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }*/

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

    public int getCursorLine() {
        ScrollActivity scrollActivity = (ScrollActivity) scrollViewListener;
        if (scrollActivity.isEditText()) {
            if (scrollActivity.getAutoScroll().getProgress() < scrollActivity.getAutoScroll().getMax()) {
                return scrollActivity.getAutoScroll().getProgress() + 1;
            }
            return scrollActivity.getAutoScroll().getProgress();
            //return scrollLine + 1;

        } else {
            return scrollLine + scrollActivity.getAutoScroll().getScoreData().getScrollOffset() + 1;
        }
    }

    public int getLyricsPos () {
        ScrollActivity scrollActivity = (ScrollActivity) scrollViewListener;
        int cursorLine = getCursorLine();
        return scrollActivity.getTextView().getLayout().getLineStart(cursorLine);
    }

    public int getChordsPos () {
        ScrollActivity scrollActivity = (ScrollActivity) scrollViewListener;
        int cursorLine = getCursorLine() - 1;
        if (cursorLine >=0 && cursorLine < scrollActivity.getTextView().getLineCount()) {
            return scrollActivity.getTextView().getLayout().getLineStart(cursorLine);
        }
        return -1;
    }

    int[] beatcolor = getResources().getIntArray(R.array.beatcolor);

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
        if (scrollActivity.isEditText()) {
            return super.onInterceptTouchEvent(ev);
        }

        long currTap;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            //    Toast.makeText(scrollActivity, "touche.action_down", Toast.LENGTH_SHORT).show();
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

     /*  if (scrollActivity.isEditText()) {
           return super.onTouchEvent(ev);
       }*/

       switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                //Toast.makeText(ScrollActivity.this, "up", Toast.LENGTH_SHORT).show();
                if (!isScrolling) {
                    int tapSpeed = (int) getAvgTapSpeed();
                    if (tapSpeed == 1000000000) {
                        //scrollActivity.getAutoScroll().setBeatInterval(tapSpeed);
                    } else {
                        scrollActivity.getSong().pause();

                        Layout layout = scrollActivity.getTextView().getLayout();
                        int y = (int) ev.getY() + scrollActivity.getTextView().getTotalPaddingTop() + scrollActivity.getScrollView().getScrollY();
                        int line = layout.getLineForVertical(y);

                        if (!scrollActivity.isEditText()) {
                           // int measures = scrollActivity.getAutoScroll().getGroupArray().getMeasuresToStartOfLine(line);
                           // scrollActivity.getSong().setStartPosition(measures * AutoScroll.scoreData.getBeatsPerMeasure() * AutoScroll.scoreData.getBeatInterval());
                            if (ev.getY() < scrollActivity.getScrollVeiwHeight() * 0.4) {
                                scrollActivity.getAutoScroll().pageUp();
                            } else {
                                scrollActivity.getAutoScroll().pageDown();
                            //     int measures = scrollActivity.getAutoScroll().getGroupArray().getMeasuresToStartOfLine(line);
                            //     scrollActivity.getSong().setStartPosition(measures * AutoScroll.scoreData.getBeatsPerMeasure() * AutoScroll.scoreData.getBeatInterval());
                            }
                        }
                        else {
                           // scrollActivity.getAutoScroll().setProgress( scrollActivity.getAutoScroll().getProgress() + deltaY/2);
                            //if (ev.getY() < scrollActivity.getScrollVeiwHeight() * 0.5) {
                            //    scrollActivity.getAutoScroll().pageUp();
                           // } else {
                                scrollActivity.getAutoScroll().setProgress(line);
                                int group = scrollActivity.getAutoScroll().getGroupArray().getGroupFromLine(line);
                                scrollActivity.getSong().setStartPosition(scrollActivity.getAutoScroll().getGroupArray().getMeasuresToStartOfLine(group) * scrollActivity.getAutoScroll().getScoreData().getBeatsPerMeasure() * scrollActivity.getAutoScroll().getScoreData().getBeatInterval());

                          //  }
                        }
                    }
                }
             //   Toast.makeText(scrollActivity, "enabled", Toast.LENGTH_SHORT).show();
             //   enableScrolling = true;
                if (startPlayerAfterMove) { scrollActivity.getSong().start(); }
                isScrolling = false;
                break;
            case MotionEvent.ACTION_DOWN:
              //  Toast.makeText(scrollActivity, "motionevent.action_down", Toast.LENGTH_SHORT).show();
                break;
            case MotionEvent.ACTION_MOVE:
                int size = ev.getHistorySize();
                if (size > 1) {
                   // scrollActivity.disableEditMode();
                    scrollActivity.getSong().pause();
                    int deltaY = (int) ((ev.getHistoricalY(size - 2) - ev.getHistoricalY(size - 1)));

                   if (!scrollActivity.isEditText() && deltaY != 0) {
                  //      scrollActivity.setSongPosition(deltaY);
                        int currentGroup = scrollActivity.getAutoScroll().getGroupArray().getCurrentGroup();
                        currentGroup += deltaY/2;
                        scrollActivity.getSong().setStartPosition( scrollActivity.getAutoScroll().getGroupArray().getMeasuresToStartOfLine(currentGroup) * scrollActivity.getAutoScroll().getScoreData().getBeatsPerMeasure() * scrollActivity.getAutoScroll().getScoreData().getBeatInterval());

                    }
                    else {

                        scrollActivity.getAutoScroll().setProgress( scrollActivity.getAutoScroll().getProgress() + deltaY/2);
                    }
                        //scrollActivity.updateSongAndSeekProgress(deltaY);
                    //}
                    scrollToProgress();
                }
                return false;

        }
       return super.onTouchEvent(ev);
     //   return false;
    }

    /////// turn off scroll event handler when app calls scrollTo
    @Override
    public void scrollTo (int x, int y) {
        if (isEnableScrolling() && isScrolling ) {
            ScrollViewListener scrollViewListener = getScrollViewListener();

            setScrollViewListener(null);
            if (!BuildConfig.DEBUG || false) {
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

    void drawBeatIndicator (float beatspan, int beatpos, int linePos, Canvas canvas) {
        //try {
            if (beatspan > 0) {
                ScrollActivity scrollActivity = (ScrollActivity) scrollViewListener;
                //int position = beatpos % beatspan + 1;
                //int rectBottom = (int) (linePos * lineHeight);
                int rectBottom = scrollActivity.getTextView().getLayout().getLineTop(linePos);


                int width = (int) (scrollActivity.getScrollView().getWidth() / beatspan * beatpos);

                // create a rectangle that we'll draw later
                Rect rectangle = new Rect(0, rectBottom + 8, width, rectBottom + 12);

                // shrink beat positions to fit into color array available colors
                float shrinkFactor = beatcolor.length / (float) beatspan;
                int colorIndex = (int) ((beatpos - 1) * shrinkFactor);

                // create the Paint and set its color
                Paint paint = new Paint();
                paint.setColor(getResources().getColor(R.color.beatcolorgray));
                canvas.drawRect(rectangle, paint);
            }
      //  }
        //catch (Exception e) {
       //     Log.e ("drawBeatIndicator", e.getMessage());
        //}
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

    public void scrollToProgress() {
        int scrollTo = scrollLine;
        ScrollActivity scrollActivity = (ScrollActivity) scrollViewListener;

        if (!scrollActivity.isEditText() && scrollActivity.getSong().getPosition() == 0) {
            scrollTo = 0;     /// show stuff above starting line of song
        }

        try {
            scrollTo(0, scrollActivity.getTextView().getLayout().getLineTop(scrollTo));

        }
        catch (Exception e) {
            Log.d("Scroll Error", e.toString());
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        int beatpos = 0;
        float beatspan = 0;


        ScrollActivity scrollActivity = (ScrollActivity) scrollViewListener;

       // if (!scrollActivity.isEditText()) {
            if (scrollActivity.getTextView().getLayout() != null) {

                AutoScroll autoScroll = scrollActivity.getAutoScroll();
                setScrollLine((int) (autoScroll.getScrollLine()));

                ////// line measure position cursor
                //if (!scrollActivity.isEditText()) {
                    if (autoScroll.getBeatInterval() > 0) {
                        beatspan = !scrollActivity.isPlaying() ? 1 : autoScroll.getGroupArray().getMeasures(-1);
                        // beatspan = autoScroll.getLineMeasures();
                        // if (beatspan > autoScroll.getScoreData().getMeasures()) {
                        //     beatspan = autoScroll.getScoreData().getMeasures();
                        // }

                        //beatpos = scrollActivity.getSong().getBeat();
                        beatpos = autoScroll.getProgress() - autoScroll.getGroupArray().getStartLineMeasuresFromTotalMeasures(autoScroll.getProgress()) + 1;

                        // int numbeatsmax = (int) beatspan;
                        // beatpos = beatpos % (int) beatspan == 0 ? (int) beatspan : beatpos % (int) beatspan;
                        //beatpos = beatpos % (int) beatspan;
                        drawBeatIndicator(beatspan, beatpos, getCursorLine(), canvas);
                    }
                //}

                ////// tempo animation
                  /*  if (!scrollActivity.isPlaying() && autoScroll.getBeatInterval() > 0) {
                        beatpos = (int) (scrollActivity.getElapsedTime() / autoScroll.getBeatInterval());
                        beatpos = beatpos % autoScroll.getScoreData().getMeasures() == 0 ? autoScroll.getScoreData().getMeasures() : beatpos % autoScroll.getScoreData().getMeasures();
                        beatspan = autoScroll.getScoreData().getMeasures();
                        drawBeatIndicator(beatspan, beatpos, 0, canvas);
                    }*/


               /* ////// tempo animation
                if (autoScroll.getBeatInterval() > 0) {
                    beatspan = autoScroll.getLineMeasures();
                    if (beatspan > autoScroll.getScoreData().getMeasuresPerLine()) {
                        beatspan = autoScroll.getScoreData().getMeasuresPerLine();
                    }

                    beatpos = autoScroll.getProgress() - autoScroll.getStartLineMeasures() + 1;
                    beatpos = beatpos % beatspan+1;
                    drawBeatIndicator(beatspan, beatpos, scrollLine + autoScroll.getScoreData().getScrollStart() + 2, canvas);
                }

                if (!scrollActivity.isPlaying() && autoScroll.getBeatInterval() > 0) {
                    beatpos = (int) (scrollActivity.getElapsedTime() / autoScroll.getBeatInterval());
                    beatpos = beatpos % autoScroll.getScoreData().getMeasures() + 1;
                    beatspan = autoScroll.getScoreData().getMeasures();
                    drawBeatIndicator(beatspan, beatpos, 0, canvas);
                }*/

      //      }

            if (scrollActivity.isPlaying()) {
                scrollToProgress();
            }
        }
    }
}
