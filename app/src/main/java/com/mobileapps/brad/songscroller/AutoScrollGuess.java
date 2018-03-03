package com.mobileapps.brad.songscroller;


import java.io.File;

/**
 * Created by brad on 3/2/18.
 */

public class AutoScrollGuess extends AutoScroll {
    public AutoScrollGuess(ScrollActivity scrollActivity) {
        super(scrollActivity);
    }

    public AutoScrollGuess (ScrollActivity scrollActivity, AutoScroll autoScroll) {
        super(scrollActivity);
        this.text = autoScroll.getText();
        //this.scoreData = autoScroll.scoreData;
    }

    public void onScrollChanged(ScrollViewExt scrollView, int x, int y, int oldx, int oldy) {
        if ( ScrollActivity.mediaPlayer == null || !ScrollActivity.mediaPlayer.isPlaying()) {
            scrollActivity.setPosOffset(y);
        }
        else {
            int mediaPos_new = ScrollActivity.mediaPlayer.getCurrentPosition();

            if (scrollActivity.getPosOffset() == scrollActivity.getPosOffset() || scrollActivity.getIvMute().isActivated()) {
                ///////// how it is calulated for moving scroller
                //scrollY = calculatedPos + posOffet
                //posOffest = scrollY - calculatedPos;
                ///////// this code allows user to shift position of vertical scroll while mp3 is playing
                scrollActivity.setPosOffset(y - ((int) (((double) (mediaPos_new - scrollActivity.getPause()) / scrollActivity.getSong().getDuration()) * scrollActivity.getTextVeiwHeight())));
            } else {
                ///////// how it is calulated for new song seek position
                //scrollY = calculatedPos + posOffet (from that equation)
                ///////// this code sets the mp3 current position to match the vertical scroll position
                scrollActivity.setNewSeek((int) ((scrollActivity.getSong().getDuration() * (y - (scrollActivity.getPosOffset())) / (double) scrollActivity.getTextVeiwHeight()) + scrollActivity.getPause()));
            }
        }
    }

    public boolean isChordLine (int position) {return true;}
}
