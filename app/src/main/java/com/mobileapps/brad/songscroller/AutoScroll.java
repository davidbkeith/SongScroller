package com.mobileapps.brad.songscroller;

import android.util.Log;
import android.widget.SeekBar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by brad on 2/26/18.
 */

public abstract class AutoScroll {

    protected ScoreData scoreData;
    protected int BeatInterval;
    protected int startLine;
    protected String text;
    protected ScrollActivity scrollActivity;

    public int getPosOffset() {
        return posOffset;
    }

    public void setPosOffset(int posOffset) {
        this.posOffset = posOffset;
    }

    protected int posOffset;

    public AutoScroll (ScrollActivity scrollActivity) {
        this.scrollActivity = scrollActivity;
    }

    public AutoScroll (ScrollActivity scrollActivity, File file) {}

    public boolean isValid () {return true;}

    public int getBeatInterval() {
        return BeatInterval;
    }

    public void setBeatInterval(int beatInterval) {
        BeatInterval = beatInterval;
    }

    public ScoreData getScoreData() {
        return scoreData;
    }

    public void setScoreData (ScoreData scoreData) { this.scoreData = scoreData; }

    public String getText() {
        return text;
    }

    public int getStartLine() {
        return startLine;
    }

    public int getNumLines () {
        return text.trim().split("\n").length;
    }

    public void setPriorWrappedLines () {}

    public void onScrollChanged(ScrollViewExt scrollView, int x, int y, int oldx, int oldy) {}

    public int getLineMeasures (int measure) {return 0;}

    public int getRepeat () {return 1;}

    public int getScrollLine(int measure) {return 0;}

    public boolean isChordLine (int position) {return false;}

    public void showBeat () {};

    public GroupArray getGroupArray() { return null; }

    public int getSongDuration () { return 0; }

    public void setOnSeekBarProgressChanged (SeekBar seekBar, int i, boolean b) {}

    //public void setProgress (int progress) {}
    public int getScrollPosition () { return 0; }

}
