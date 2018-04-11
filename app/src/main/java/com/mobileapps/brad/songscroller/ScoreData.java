package com.mobileapps.brad.songscroller;

import java.io.Serializable;

/**
 * Created by brad on 2/26/18.
 */

public class ScoreData implements Serializable {
    private int bpm;
    private float measuresPerLine;
    private int beatsPerMeasure; // is time signature basically (beatsPerMeasure/4 time);
    private float beatsPerChord;
    private int scrollOffset;
    //private int songStartLine;  /// line number (zero indexed) of first chord of song

   /* public float getBeatsPerChord() {
        return beatsPerChord;
    }

    public void setBeatsPerChord(float beatsPerChord) {
        this.beatsPerChord = beatsPerChord;
    }*/

 /*   public int getSongStartLine() {
        return songStartLine;
    }

    public void setSongStartLine(int songStartLine) {
        this.songStartLine = songStartLine;
    }*/

    ScoreData () {}

    ScoreData (int measuresPerLine, int bpm, int beatsPerMeasure, int scrollOffset) {
        this.bpm = bpm;
        this.measuresPerLine = measuresPerLine;
        this.beatsPerMeasure = beatsPerMeasure;
        //this.beatsPerChord = beatsPerChord;

        //// sets the number of lines before beginning of song at which scrolling will begin
        //// default is 3 lines
        this.scrollOffset = scrollOffset;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public int getBeatsPerMeasure() {
        return beatsPerMeasure;
    }

    public void setBeatsPerMeasure(int beatsPerMeasure) {
        this.beatsPerMeasure = beatsPerMeasure;
    }

    public void setMeasuresPerLine(float measuresPerLine) {
        this.measuresPerLine = measuresPerLine;
    }

    public float getMeasuresPerLine() {
        return measuresPerLine;
    }

    public void setBeatsPerLine (int beatsPerLine) {
        measuresPerLine = beatsPerLine / (float) beatsPerMeasure;
    }

    public int getBeatsPerLine () {
       // if (ScrollActivity.isEditing) {
       //     return 1;
       // }
       // else {
            return (int) (measuresPerLine * beatsPerMeasure);
       // }
    }

    public int getBeatsPerLine (float measuresperline) {
        if (measuresperline == -1) {
            return getBeatsPerLine();
        }
        return (int) (measuresperline * beatsPerMeasure);
    }
    // public void setBeatsPerLine(int beatsPerLine) {
   //     this.measuresPerLine = measuresPerLine;
   // }

    public int getScrollOffset() { return scrollOffset; }

    public void setScrollOffset(int scrollOffset) {
        this.scrollOffset = scrollOffset;
    }

    public int getBeatInterval () {
        return 60000 / bpm;
    }
}
