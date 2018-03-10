package com.mobileapps.brad.songscroller;


import android.content.Context;
import android.util.AttributeSet;

import java.io.File;

/**
 * Created by brad on 3/2/18.
 */

public class AutoScrollGuess extends AutoScroll implements android.widget.SeekBar.OnSeekBarChangeListener{

    public AutoScrollGuess(Context context, AutoScroll autoScroll) {
        super(context);
        this.setScoreData(autoScroll.scoreData);
        this.text = autoScroll.text;
    }

    public AutoScrollGuess(Context context) {
        super(context);
        this.scrollActivity = (ScrollActivity) context;
    }

    public AutoScrollGuess(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoScrollGuess(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void initialize (AutoScroll autoScroll) {
        this.text = autoScroll.getText();
        this.scoreData = autoScroll.getScoreData();
    }

    @Override
    public void onScrollChanged(ScrollViewExt scrollView, int x, int y, int oldx, int oldy) {
        if ( ScrollActivity.mediaPlayer == null || !ScrollActivity.mediaPlayer.isPlaying()) {
            posOffset = y;
        }
        else {
            int mediaPos_new = ScrollActivity.mediaPlayer.getCurrentPosition();

            if (posOffset == posOffset || scrollActivity.getIvMute().isActivated()) {
                ///////// how it is calulated for moving scroller
                //scrollY = calculatedPos + posOffet
                //posOffest = scrollY - calculatedPos;
                ///////// this code allows user to shift position of vertical scroll while mp3 is playing
                //posOffset = (y - ((int) (((double) (mediaPos_new - scrollActivity.getPause()) / scrollActivity.getSong().getDuration()) * scrollActivity.getTextVeiwHeight())));
                posOffset = (y - ((int) (((double) (getProgress() - scrollActivity.getPause()) / scrollActivity.getSong().getDuration()) * scrollActivity.getTextVeiwHeight())));
            } else {
                ///////// how it is calulated for new song seek position
                //scrollY = calculatedPos + posOffet (from that equation)
                ///////// this code sets the mp3 current position to match the vertical scroll position
                scrollActivity.setNewSeek((int) ((scrollActivity.getSong().getDuration() * (y - (posOffset)) / (double) scrollActivity.getTextVeiwHeight()) + scrollActivity.getPause()));
            }
        }
    }

    @Override
    public int getSongDuration () {
        if (ScrollActivity.mediaPlayer != null) {
            return (int) (ScrollActivity.mediaPlayer.getDuration() / scrollActivity.getActualLineHeight());
        }
        return 0;
    }

    @Override
    public boolean isChordLine (int position) {return true;}

    @Override
    public int getScrollLine() {
        return getProgress();
    }
}
