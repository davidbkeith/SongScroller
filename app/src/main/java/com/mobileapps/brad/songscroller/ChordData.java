package com.mobileapps.brad.songscroller;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import java.util.ArrayList;
import java.util.List;

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

    static public void setChords (List<ChordData> chords, SpannableStringBuilder sb, ForegroundColorSpan fcs) {
         for (ChordData chordData: chords) {
             sb.setSpan(fcs, chordData.getStartPos(), chordData.getStartPos() + chordData.getChord().length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
         }
    }
}
