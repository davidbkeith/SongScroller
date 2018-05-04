package com.mobileapps.brad.songscroller;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by brad on 2/26/18.
 */

public class GroupData implements Serializable {
    int offsetChords;
    int measures;
    int next;

    public GroupData () {
        measures = -1;
        next = -1;
    }

    public GroupData (int offsetChords, int measures) {
        this.offsetChords = offsetChords;
        this.measures = measures;
    }

    public GroupData (GroupData groupData) {
        this.offsetChords = groupData.offsetChords;
        this.measures = groupData.measures;
    }

    public void setMeasures(int measures) {
        this.measures = measures == AutoScroll.scoreData.getMeasures() ? -1 : measures;
    }

    public int getMeasures() {
        return measures == -1 ? AutoScroll.scoreData.getMeasures() : measures;
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

    public int getOffsetChords() {
        return offsetChords;
    }

    public void setOffsetChords(int offsetChords) {
        this.offsetChords = offsetChords;
    }

    public void getLineMetaData (String JSON) throws Exception {
        JSONObject jsonObject = new JSONObject(JSON);
        int linemeasures = jsonObject.optInt("measures", -1);
        int repeat = jsonObject.optInt("repeat", 0) + 1;

        int measures = linemeasures == -1 ? AutoScroll.scoreData.getMeasures() : linemeasures;
        //measures = repeat * linemeasures;
        setMeasures(repeat * measures);
    }
}

