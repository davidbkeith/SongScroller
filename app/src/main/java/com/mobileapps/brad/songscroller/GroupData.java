package com.mobileapps.brad.songscroller;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;

/**
 * Created by brad on 2/26/18.
 */

public class GroupData implements Serializable {
    int offsetChords;
    int ChordsLength;
    int measuresToEndofLine;
    int repeat;
    int chordsLineNumber;
    int groupLineCount;
    int wrappedLines;
    String[] chords;
    ScoreData scoreData;

    public GroupData () {
        measuresToEndofLine = -1;
        repeat = 1;
    }

    public GroupData (ScoreData scoreData) {
        measuresToEndofLine = -1;
        repeat = 1;
        this.scoreData = scoreData;
    }

    public String[] getChords() {
        return chords;
    }

    public void setChords(String[] chords) {
        this.chords = chords;
    }

    public String getLyrics (String score) {
        int indexOf = score.indexOf ("\n", offsetChords);
        if (indexOf != -1) {
            return (score.substring(offsetChords, indexOf));
        }
        return score.substring(offsetChords);
    }

    public String setLyrics (String newLine, String score) {
        String newScore;
        int indexOf = score.indexOf("\n", offsetChords);
        if (indexOf != -1) {
            newScore = score.substring(0, offsetChords).concat(newLine).concat(score.substring(indexOf));
        } else {
            newScore = score.substring(offsetChords).concat(newLine);
        }
        return newScore;
    }

    public int getGroupLineCount() {
        return groupLineCount;
    }

    public void setGroupLineCount(int groupLineCount) {
        this.groupLineCount = groupLineCount;
    }

    public int getChordsLineNumber() { return chordsLineNumber; }

    public void setChordsLineNumber(int chordsLineNumber) { this.chordsLineNumber = chordsLineNumber; }

    public int getWrappedLines() { return wrappedLines; }

    public void setWrappedLines(int wrappedLines) {
        this.wrappedLines = wrappedLines;
    }

    public int getChordsLength() {
        return ChordsLength;
    }

    public void setChordsLength(int chordsLength) {
        ChordsLength = chordsLength;
    }

    public int getOffsetChords() {
        return offsetChords;
    }

    public void setOffsetChords(int offsetChords) {
        this.offsetChords = offsetChords;
    }

    public int getBeats () {
        return scoreData.getBeats();
    }

    public int getMeasuresToEndofLine() {
        return measuresToEndofLine;
    }

    public void setMeasuresToEndofLine(int measuresToEndofLine) { this.measuresToEndofLine = measuresToEndofLine; }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    /*public static class PositionCompare implements Comparator<GroupData> {
        @Override
        public int compare(GroupData gd1, GroupData gd2) {
            return gd1.getOffsetChords() - gd2.getOffsetChords();
        }
    }

    /*public static class LengthCompare implements Comparator<GroupData> {
        @Override
        public int compare(GroupData gd1, GroupData gd2) {
            if (gd2.getLyricsLength() > gd2.getChordsLength()) {
                if (gd1.getLyricsLength() > gd1.getChordsLength()) {
                    return gd2.getLyricsLength() - gd1.getLyricsLength();
                }
                else {
                    return gd2.getLyricsLength() - gd1.getChordsLength();
                }
            }
            else {
                if (gd1.getLyricsLength() > gd1.getChordsLength()) {
                    return gd2.getChordsLength() - gd1.getLyricsLength();
                }
                else {
                    return gd2.getChordsLength() - gd1.getChordsLength();
                }
            }
        }
    }*/

    public void getLineMetaData (String JSON) throws Exception {
        JSONObject jsonObject = new JSONObject(JSON);
        setMeasuresToEndofLine(jsonObject.optInt("measures", -1));
        setRepeat(jsonObject.optInt("repeat", 1));
    }
}

