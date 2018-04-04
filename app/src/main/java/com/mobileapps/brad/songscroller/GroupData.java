package com.mobileapps.brad.songscroller;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
    int[] chords;
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

    public boolean equals(Object object2) {
        return object2 instanceof GroupData && getOffsetChords() == ((GroupData) object2).getOffsetChords();
    }

    public int[] getChords() {
        return chords;
    }

    public int[] getChordsStartPositions() {
        int[] starts = new int[chords.length/2];
        for (int i=0; i<chords.length; i+=2) {
            starts[i/2] = chords[i]+1;  /// add 1 so user not confused with zero index
        }
        return starts;
    }

    public String[] getChords(String text) {
        String[] chordArray = new String[chords.length/2];
        for (int i=0; i<chords.length; i+=2) {
            int start = chords[i] + getOffsetChords();
            chordArray[i/2] = text.substring(start, start + chords[i+1]);
        }
        return chordArray;
    }

    public void setChords(String[] chordsPositions, int[] originalChords) {
        //this.chords = chords;
        //chords = new int[chordsPositions.length];
        //int count = 0;
        //for (int i=0; i<chordsPositions)
       // List origChords = Arrays.asList(getChords());
        List newChords = new ArrayList();

        for (int i=0; i<chordsPositions.length; i++){
            try {
                int chordPos = Integer.parseInt(chordsPositions[i].trim()) - 1;
                newChords.add(chordPos);

                if (i*2+1 < originalChords.length) {
                    newChords.add(originalChords[i*2 + 1]);
                }
                else {
                    newChords.add(1);
                }
            } catch (Exception e) {
                Log.d("ParseInt", "Error in setChords converting chord position to integer");
            }
        }

        setChords(newChords);

       /* for (int i=0; i<chords.length; i+=2) {
            if (i < chordsPositions.length) {
                if (chordsPositions[i].length() > 0) {
                    try {
                        chords[i] = Integer.parseInt(chordsPositions[i]);
                        //count++;
                    } catch (Exception e) {
                        Log.d("ParseInt", "Error in setChords converting chord position to integer");
                    }
                } else {
                    chords[i] = -1;
                }
            }
            else {
                chords[i] = -1;
            }
        }*/
    }

    public void setChords(List chordsPositions) {
        chords = new int[chordsPositions.size()];
        for (int i=0; i<chordsPositions.size(); i++) {
            chords[i] = (int) chordsPositions.get(i);
        }
    }

    public String getLyrics (String score) {
        if (score != null) {
            int indexOf = score.indexOf("\n", offsetChords + ChordsLength + 1);
            if (indexOf != -1) {
                return (score.substring(offsetChords, indexOf));
            }
            return score.substring(offsetChords);
        }
        return "";
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

