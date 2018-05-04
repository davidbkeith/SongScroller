package com.mobileapps.brad.songscroller;

import android.text.Layout;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by brad on 2/26/18.
 */

public class ScoreData implements Serializable {

    private int timesignature;
    private int bpm;
    private int measures; // number of measures on most lines;
    private int scrollOffset;
    private String scorePath;

    private int version = 0;

    public int getTimesignature() {
        return timesignature;
    }

    public void setTimesignature(int timesignature) {
        this.timesignature = timesignature;
    }

    ScoreData () {}

    ScoreData (String datafile) {
        String[] data = datafile.split(",");
        this.version = Integer.parseInt(data[0]);
        switch (version) {
            default:
                this.bpm = Integer.parseInt(data[1]);
                this.measures = 4;
                this.timesignature = Integer.parseInt(data[2]);
                this.scrollOffset = Integer.parseInt(data[3]);
                this.scorePath = data[4];
        }
    }

    ScoreData (int bpm, int measures, int timesignature, int scrollOffset, String scorePath) {
        this.bpm = bpm;

        this.measures = measures;
        //this.beatsPerChord = beatsPerChord;
        this.timesignature = timesignature;

        //// sets the number of lines before beginning of song at which scrolling will begin
        //// default is 3 lines
        this.scrollOffset = scrollOffset;
        this.scorePath = scorePath;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public int getMeasures() {
        return measures;
    }

    public int getBeatsPerMeasure () {
        switch (timesignature) {
            case 6:
                return 6;
            default:
                return 4;
        }
    }

    public String getScorePath() {
        return scorePath;
    }

    public void setScorePath(String scorePath) {
        this.scorePath = scorePath;
    }

    public String getSerializedData () {
        return String.format("%d,%d,%d,%d,%s,", version, bpm, timesignature, scrollOffset, scorePath);
    }

    public void setMeasures(int measures) {
        this.measures = measures;
    }

    public int getScrollOffset() { return scrollOffset; }

    public void setScrollOffset(int scrollOffset) {
        this.scrollOffset = scrollOffset;
    }

    public int getBeatInterval () {
        return 60000 / bpm;
    }

    public int getVersion() {
        return version;
    }
}
