package com.mobileapps.brad.songscroller;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by brad on 2/26/18.
 */

public class AutoScroll {

    private ScoreData scoreData;
    private int BeatInterval;
    private String text;
    private GroupArray groupArray;
    private ScrollActivity scrollActivity;

    public boolean isValid () {
        return groupArray != null && groupArray.size() > 0;
    }

    public int getBeatInterval() {
        return BeatInterval;
    }

    public void setBeatInterval(int beatInterval) {
        BeatInterval = beatInterval;
    }

    public GroupArray getGroupArray() {
        return groupArray;
    }

    public ScoreData getScoreData() {
        return scoreData;
    }

    public int getNumLines () {
        return text.split("\n").length;
    }

    public AutoScroll (ScrollActivity scrollActivity, File file) {
        this.scrollActivity = scrollActivity;
        groupArray = new GroupArray();
        text = "";

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            groupArray = new GroupArray();

            while (scoreData == null && (line = br.readLine()) != null) {
                text += getScoreData(line);
                text += "\n";
            }

            if (scoreData != null) {
                text = groupArray.create(br, text, scoreData);
            }
            br.close();
        }
        catch (Exception e) {
            Log.e("File Read Error", e.toString());
        }
    }

    public String getText() {
        return text;
    }

    private String getScoreData (String JSON) {
        try {
            JSONObject jsonObject = new JSONObject(JSON);
            scoreData = new ScoreData(jsonObject.optInt("bpm"), jsonObject.optInt("beats", 4), jsonObject.optInt("measures", 16));
            BeatInterval = 60000 / scoreData.getBpm();
            return "";
        }
        catch (Exception e){
            Log.e("JSON Parsing Error:", e.toString());
            return JSON;
        }
    }
}
