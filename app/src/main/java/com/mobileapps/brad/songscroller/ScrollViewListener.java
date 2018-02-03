package com.mobileapps.brad.songscroller;

/**
 * Created by brad on 2/2/18.
 */

public interface ScrollViewListener {
    void onScrollChanged(ScrollViewExt scrollView,
                         int x, int y, int oldx, int oldy);
}
