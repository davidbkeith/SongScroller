package com.mobileapps.brad.songscroller;

import android.text.SpannableStringBuilder;
import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brad on 2/26/18.
 */

public class GroupData implements Serializable {
    int offsetChords;
    int lengthChords; /// length of chords line, not number of chords
    float measures;
    int[] chords;

    public GroupData () {
        measures = -1;
     //   repeat = 1;
    }

    public void setBeats (int beats) {
        double measures = beats / (double) AutoScroll.scoreData.getBeatsPerMeasure();
        setMeasures (measures);
    }

    public int getBeats() {
        return AutoScroll.scoreData.getBeatsPerLine(measures);
    }

    public float getMeasures() {
        if (measures == -1) {
            return AutoScroll.scoreData.getMeasuresPerLine();
        }
        return measures;
    }

    public void setMeasures () {
        double guessMeasures = (chords.length/2.0) / AutoScroll.scoreData.getBeatsPerMeasure();
        setMeasures(guessMeasures);
    }

    public void setMeasures(double lineMeasures) {
        if (lineMeasures != getMeasures()) {
            this.measures = (float) lineMeasures;
        }
    }

    public boolean equals(Object object2) {
        return object2 instanceof GroupData && getOffsetChords() == ((GroupData) object2).getOffsetChords();
    }

   /* public int getMeasuresPerChord () {
        return beats/(chords.length/2);
    }*/

    public int[] getChords() {
        return chords;
    }

    public int[] getChordsStartPositions() {
        if (chords != null) {
            int[] starts = new int[chords.length / 2];
            for (int i = 0; i < chords.length; i += 2) {
                starts[i / 2] = chords[i] + 1;  /// add 1 so user not confused with zero index
            }
            return starts;
        }
        return new int[]{};
    }

    public String[] getChords(String text) {
        if (chords != null) {
            String[] chordArray = new String[chords.length / 2];
            for (int i = 0; i < chords.length; i += 2) {
                int start = chords[i] + getOffsetChords();
                chordArray[i / 2] = text.substring(start, start + chords[i + 1]);
            }
            return chordArray;
        }
        return new String[]{};
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
 /*   public void setChords(List chordsPositions) {
        chords = new int[chordsPositions.size()];
        for (int i=0; i<chordsPositions.size(); i++) {
            chords[i] = (int) chordsPositions.get(i);
        }
    }*/


    public void setChords(List chordsPositions) {
        chords = new int[chordsPositions.size()];
        for (int i=0; i<chordsPositions.size(); i++) {
            chords[i] = (int) chordsPositions.get(i);
        }

        //setMeasures();
    }

    public String getLyrics (String score) {
        if (score != null) {
            int indexOf = score.indexOf("\n", offsetChords + lengthChords + 1);
            if (indexOf != -1) {
                return (score.substring(offsetChords, indexOf));
            }
            return score.substring(offsetChords);
        }
        return "";
    }

    public void setLyrics (String newLine, int lineStart, ScrollActivity scrollActivity) {
        SpannableStringBuilder newScore = scrollActivity.getSb();
        String text = newScore.toString();
        int indexOf = text.indexOf("\n", lineStart);
        int offsetCharPos = 0;
        //int offsetLineCount = newLine.split("\n").length - 1;

        if (indexOf != -1) {
            newScore = newScore.replace(lineStart, indexOf, newLine);
            offsetCharPos = newLine.length() - (indexOf - lineStart);
           // newScore = text.substring(0, offsetChords).concat(newLine).concat(text.substring(indexOf));
        } else {
            newScore = newScore.replace(lineStart,text.length(), newLine);
            //offsetCharPos = text.length() - offsetChords;
        }

        scrollActivity.getAutoScroll().getGroupArray().updatePositions (offsetChords, offsetCharPos);
        scrollActivity.setSb(newScore);
    }

  /*  public int getGroupLineCount() {
        return groupLineCount;
    }

    public void setGroupLineCount(int groupLineCount) {
        this.groupLineCount = groupLineCount;
    }
*/
   /* public int getChordsLineNumber() { return chordsLineNumber; }

    public void setChordsLineNumber(int chordsLineNumber) { this.chordsLineNumber = chordsLineNumber; }*/

   /* public int getWrappedLines() { return wrappedLines; }

    public void setWrappedLines(int wrappedLines) {
        this.wrappedLines = wrappedLines;
    }
*/
    public int getLengthChords() {
        return lengthChords;
    }

    public void setLengthChords(int lengthChords) {
        this.lengthChords = lengthChords;
    }

    public int getOffsetChords() {
        return offsetChords;
    }

    public void setOffsetChords(int offsetChords) {
        this.offsetChords = offsetChords;
    }


    //public int getMeasuresToEndofLine() {
    //    return measuresToEndofLine;
    //}

    //public void setMeasuresToEndofLine(int measuresToEndofLine) { this.measuresToEndofLine = measuresToEndofLine; }

   /* public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }*/

    /*public static class PositionCompare implements Comparator<GroupData> {
        @Override
        public int compare(GroupData gd1, GroupData gd2) {
            return gd1.getOffsetChords() - gd2.getOffsetChords();
        }
    }

    /*public static class LengthCompare implements Comparator<GroupData> {
        @Override
        public int compare(GroupData gd1, GroupData gd2) {
            if (gd2.getLyricsLength() > gd2.getLengthChords()) {
                if (gd1.getLyricsLength() > gd1.getLengthChords()) {
                    return gd2.getLyricsLength() - gd1.getLyricsLength();
                }
                else {
                    return gd2.getLyricsLength() - gd1.getLengthChords();
                }
            }
            else {
                if (gd1.getLyricsLength() > gd1.getLengthChords()) {
                    return gd2.getLengthChords() - gd1.getLyricsLength();
                }
                else {
                    return gd2.getLengthChords() - gd1.getLengthChords();
                }
            }
        }
    }*/

    public void getLineMetaData (String JSON) throws Exception {
        JSONObject jsonObject = new JSONObject(JSON);
        int beats = jsonObject.optInt("beats", -1);
        int repeat = jsonObject.optInt("repeat", 1);

        beats = beats == -1 ? AutoScroll.scoreData.getBeatsPerLine() : beats;
        beats = repeat * beats;
        setBeats(repeat * beats);
    }
}

