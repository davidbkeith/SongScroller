package com.mobileapps.brad.songscroller;

import android.util.Log;
import android.widget.ScrollView;
import android.widget.SeekBar;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by brad on 3/2/18.
 */

public class AutoScrollCalculated extends AutoScroll {
    protected GroupArray groupArray;
    public GroupArray getGroupArray() {
        return groupArray;
    }

    public int getMeasure() {
        return measure;
    }

    public void setMeasure(int measure) {
        this.measure = measure;
    }

    private int measure;


    public boolean isValid () {
        return groupArray != null && groupArray.size() > 0;
    }

    public AutoScrollCalculated (ScrollActivity scrollActivity, File file) {

        super(scrollActivity);
        groupArray = new GroupArray(scrollActivity);
        text = "";

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            groupArray = new GroupArray(scrollActivity);

            while (scoreData == null && (line = br.readLine()) != null) {
                text += getScoreData(line);
                text += "\n";
                scrollActivity.setPosOffset(++startLine);
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

    public int getSongDuration () {
        return groupArray.getTotalMeasures();
    }

    public void setProgress (int measure) {
        this.measure = measure;
    }

    public int getScrollPosition () {
        return ((int) ((getScrollLine(measure) + posOffset) * scrollActivity.getScrollView().getLineHeight()));
       // currentScrollPos = (int) (scrollView.getScrollLinePos() * scrollView.getLineHeight());
       // textCountdown.setText(String.format("%d",measure));

    }

    public void onScrollChanged(ScrollViewExt scrollView, int x, int y, int oldx, int oldy) {
        /*
        * y = (int) (autoScroll.getGroupArray().getScrollLine(measure) * actualLineHeight + posOffset * actualLineHeight);
        *
        */
        measure = groupArray.getStartOfLineMeasures((int)(y/scrollView.getLineHeight()) - posOffset);
        //scrollActivity.setNewSeek(measure * (BeatInterval * scoreData.getBeats()));
    }


    public void showBeat () {
        /*long elapsedTime = scrollActivity.getElapsedTime();
        AutoScroll autoScroll = scrollActivity.getAutoScroll();
        ScrollViewExt scrollView = scrollActivity.getScrollView();

        if (autoScroll.getBeatInterval() > 0) {
            elapsedTime +=  100;
            elapsedTime = 100 * autoScroll.getBeatInterval() == elapsedTime ? 0 : elapsedTime;

            if (scrollActivity.isPlaying()) {
                scrollView.setBeatpos(scrollActivity.getMeasure());
                int span = autoScroll.getLineMeasures(scrollActivity.getMeasure());
                scrollView.setBeatspan(span > autoScroll.getScoreData().getMeasuresPerLine() ? autoScroll.getScoreData().getMeasuresPerLine() : span);
                elapsedTime = 0;
            }
            else {
                scrollView.setBeatpos((int) elapsedTime / autoScroll.getBeatInterval());
                scrollView.setBeatspan(autoScroll.getScoreData().getBeats());
            }

            scrollView.invalidate();
            // textCountdown.setText(String.format("%d",elapsedTime));
        }*/
    }

    /*
    *
    *   groupArray convenience functions
    *
    */
    public void setPriorWrappedLines () {
        getGroupArray().setPriorWrappedLines();
    }

    public int getLineMeasures (int measure) {
        return groupArray.getLineMeasures(measure);
    }

    public int getScrollLine(int measure) {
        return groupArray.getScrollLine(measure);
    }

    public boolean isChordLine (int position) {
        return groupArray.isChordLine(position);
    }

    public int getRepeat () {
        return groupArray.getCurrentGroup().getRepeat();
    }
}
