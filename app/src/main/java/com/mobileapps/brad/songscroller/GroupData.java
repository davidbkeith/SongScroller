package com.mobileapps.brad.songscroller;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by brad on 2/26/18.
 */

public class GroupData implements Serializable {
    int offsetChords;
    int ChordsLength;
    int LyricsLength;
    int measuresToEndofLine;
    int repeat;
    int priorWrappedLines;
    ScoreData scoreData;

    public GroupData (ScoreData scoreData) {
        measuresToEndofLine = -1;
        repeat = 1;
        this.scoreData = scoreData;
    }

    public int getPriorWrappedLines() {
        return priorWrappedLines;
    }

    public void setPriorWrappedLines(int priorWrappedLines) {
        this.priorWrappedLines = priorWrappedLines;
    }

    public int getChordsLength() {
        return ChordsLength;
    }

    public void setChordsLength(int chordsLength) {
        ChordsLength = chordsLength;
    }

    public int getLyricsLength() {
        return LyricsLength;
    }

    public void setLyricsLength(int lyricsLength) {
        LyricsLength = lyricsLength;
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

    public void setMeasuresToEndofLine(int measuresToEndofLine) {
        this.measuresToEndofLine = measuresToEndofLine;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public static class PositionCompare implements Comparator<GroupData> {
        @Override
        public int compare(GroupData gd1, GroupData gd2) {
            return gd1.getOffsetChords() - gd2.getOffsetChords();
        }
    }

    public static class LengthCompare implements Comparator<GroupData> {
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
    }

    public void getLineMetaData (String JSON) throws Exception {
        JSONObject jsonObject = new JSONObject(JSON);
        setMeasuresToEndofLine(jsonObject.optInt("measures", -1));
        setRepeat(jsonObject.optInt("repeat", 1));
    }
}

