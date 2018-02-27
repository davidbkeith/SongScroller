package com.mobileapps.brad.songscroller;

/**
 * Created by brad on 2/26/18.
 */

public class ScoreData {
    int bpm;
    int beats;
    int measuresPerLine;

    ScoreData () {}

    ScoreData (int bmp, int beats, int measuresPerLine) {
        this.bpm = bmp;
        this.beats = beats;
        this.measuresPerLine = measuresPerLine;
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
}
