package com.mobileapps.brad.songscroller;

import android.content.Context;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
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

public abstract class AutoScroll extends AppCompatSeekBar implements android.widget.SeekBar.OnSeekBarChangeListener {

    protected ScoreData scoreData;
    protected int BeatInterval;
    protected int startLine;
    protected String text;
    protected ScrollActivity scrollActivity;
    protected boolean updateProgress;

    public int getPosOffset() {
        return posOffset;
    }

    public void setPosOffset(int posOffset) {
        this.posOffset = posOffset;
        updateProgress = false;
    }

    protected int posOffset;

    public AutoScroll (Context context) {
        super (context);
    };

    public AutoScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoScroll(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        //mediaPlayer.seekTo(seekBar.getProgress());
        //int newPos = seekBar.getProgress();
        //int timeLeft = seekBar.getMax() - newPos;
        //long minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeft) % TimeUnit.HOURS.toMinutes(1);
        //long seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeft) % TimeUnit.MINUTES.toSeconds(1);
        // textCountdown.setText(String.format("%d:%02d", minutes, seconds));

        //beatPos = newPos / autoScroll.getBeatInterval();
        //measure = newPos / (autoScroll.getBeatInterval() * autoScroll.getScoreData().getBeats());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        updateProgress = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        updateProgress = false;
  /*      int progress = getProgress();
=======
        int progress = getProgress();
>>>>>>> Stashed changes
        if (progress == getMax()) {
            ScrollActivity.mediaPlayer.pause();
            scrollActivity.getIvPlay().setImageResource(android.R.drawable.ic_media_play);
        }
        else {
            ScrollActivity.mediaPlayer.seekTo(seekBar.getProgress());
        }
<<<<<<< Updated upstream
        ScrollActivity.mediaPlayer.seekTo(seekBar.getProgress());*/
    }
   // public AutoScroll (ScrollActivity scrollActivity) {
   //     this.scrollActivity = scrollActivity;
   // }

  //  public AutoScroll (ScrollActivity scrollActivity, File file) {}*/
    public void initialize (ScrollActivity scrollActivity, File file) {};

    public void initialize (ScrollActivity scrollActivity, AutoScroll autoScroll) {};

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

    public int getLineMeasures (int progress) {return 0;}

    public int getRepeat () {return 1;}

    public int getScrollLine() {return 0;}

    public int getLineMeasures () {return 0;}

    public boolean isChordLine (int position) {return false;}

    public void showBeat () {};

    public GroupArray getGroupArray() { return null; }

    public int getSongDuration () { return 0; }

    public int getProgressMeasures () { return 0; }

    public void setOnSeekBarProgressChanged (SeekBar seekBar, int i, boolean b) {}

    public long getTimePerMeasure () { return 0; }

    public void setSeekBarProgress() {
        long elpasedTime = scrollActivity.getSong().getPosition();
        setProgress((int) (getMax()*((float) elpasedTime/scrollActivity.getSong().getDuration())));
    }

    public void pageUp () {}

    public void pageDown () {}

}
