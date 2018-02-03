package com.mobileapps.brad.songscroller;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by brad on 2/2/18.
 */

public class ScrollViewExt extends ScrollView {
    private Boolean enableScrolling = true;

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

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e("Media", "intercept Action down: set autoscroll to false........");
                setEnableScrolling(false);
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
                Log.e("Media", "onTouchEvent Action up: set autoscroll to ture........");
                setEnableScrolling(true);
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
}
