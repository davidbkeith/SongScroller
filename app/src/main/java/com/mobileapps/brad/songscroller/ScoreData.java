package com.mobileapps.brad.songscroller;

import java.io.Serializable;

/**
 * Created by brad on 2/26/18.
 */

public class ScoreData implements Serializable {
    int bpm;
    int beats;
    int measuresPerLine;

    int scrollStart;

    ScoreData () {}

    ScoreData (int bmp, int beats, int measuresPerLine, int scrollStart) {
        this.bpm = bmp;
        this.beats = beats;
        this.measuresPerLine = measuresPerLine;
        this.scrollStart = scrollStart;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public int getBeats() {
        return beats;
    }

    public void setBeats(int beats) {
        this.beats = beats;
    }

    public int getMeasuresPerLine() {
        return measuresPerLine;
    }

    public void setMeasuresPerLine(int measuresPerLine) {
        this.measuresPerLine = measuresPerLine;
    }

    public int getScrollStart() {
        return scrollStart;
    }

    public void setScrollStart(int scrollStartLine) {
        this.scrollStart = scrollStart;
    }
}
