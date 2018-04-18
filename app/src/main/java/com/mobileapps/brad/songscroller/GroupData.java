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
    int beats;

    public GroupData () {
        beats = -1;
    }

    public void setBeats (int beats) {
        this.beats = beats == AutoScroll.scoreData.getBeats() ? -1 : beats;
    }

    public int getBeats() {
        return beats == -1 ? AutoScroll.scoreData.getBeats() : beats;
    }

    public boolean equals(Object object2) {
        return object2 instanceof GroupData && getOffsetChords() == ((GroupData) object2).getOffsetChords();
    }

    public String getLyrics (String score) {
        if (score != null) {
            int indexOf = score.indexOf("\n", offsetChords);
            if (indexOf != -1) {
                return (score.substring(offsetChords, indexOf));
            }
            return score.substring(offsetChords);
        }
        return "";
    }

    /*public void setLyrics (String newLine, int lineStart, ScrollActivity scrollActivity) {
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
       // scrollActivity.getAutoScroll().getGroupArray().updatePositions (offsetChords, offsetCharPos);
        scrollActivity.setSb(newScore);
    }*/

    public int getOffsetChords() {
        return offsetChords;
    }

    public void setOffsetChords(int offsetChords) {
        this.offsetChords = offsetChords;
    }

    public void getLineMetaData (String JSON) throws Exception {
        JSONObject jsonObject = new JSONObject(JSON);
        int beats = jsonObject.optInt("beats", -1);
        int repeat = jsonObject.optInt("repeat", 1);

        beats = beats == -1 ? AutoScroll.scoreData.getBeats() : beats;
        beats = repeat * beats;
        setBeats(repeat * beats);
    }
}

