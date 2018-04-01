package com.mobileapps.brad.songscroller;

/**
 * Created by brad on 3/27/18.
 */

public class ChordData {
    int startPos;
    String chord;

    public ChordData() {
    }

    public ChordData(int startPos, String chord) {
        this.startPos = startPos;
        this.chord = chord;
    }

    public int getStartPos() {

        return startPos;
    }

    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    public String getChord() {
        return chord;
    }

    public void setChord(String chord) {
        this.chord = chord;
    }

}
