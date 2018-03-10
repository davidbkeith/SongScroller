package com.mobileapps.brad.songscroller;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by brad on 3/2/18.
 */

public class AutoScrollCalculated extends AutoScroll implements android.widget.SeekBar.OnSeekBarChangeListener {
    protected GroupArray groupArray;

    public GroupArray getGroupArray() {
        return groupArray;
    }

    public AutoScrollCalculated (Context context) {
        super(context);
        this.scrollActivity = (ScrollActivity) context;
    }

    public AutoScrollCalculated(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoScrollCalculated(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public long getTimePerMeasure () {
        return scrollActivity.getAutoScroll().getScoreData().getBeats() * scrollActivity.getAutoScroll().getBeatInterval();
    }

    @Override
    public boolean isValid () {
        return groupArray != null && groupArray.size() > 0;
    }

    //@Override
    //public void setSongProgress() {
    //    long elpasedTime = scrollActivity.getSong().getPosition();
    //    int groupIndex = getGroupArray().getGroupIndexFromSongPos (elpasedTime);
    //    setProgress(groupIndex);
    //}

    @Override
    public void initialize (ScrollActivity scrollActivity, File file) {
        this.scrollActivity = scrollActivity;
        groupArray = new GroupArray(scrollActivity);
        text = "";

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            groupArray = new GroupArray(scrollActivity);

            while (scoreData == null && (line = br.readLine()) != null) {
                text += getScoreData(line);
                text += "\n";
                posOffset = ++startLine;
            }

            if (scoreData != null) {
                text = groupArray.create(br, text, scoreData);
            }
            br.close();
            setMax (getSongDuration());
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

    @Override
    public int getSongDuration () {
        return groupArray.getTotalMeasures();
    }


    @Override
    public void setSeekBarProgress() {
        long elpasedTime = scrollActivity.getSong().getPosition();

        setProgress((int) (elpasedTime/getTimePerMeasure ()));
        //Toast.makeText(scrollActivity, String.format("%d",getProgressMeasures()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (updateProgress) {
            //i = i == getGroupArray().size() ? i - 1 : i;
            //int measuresToLine = getGroupArray().getStartOfLineMeasures(i);
            scrollActivity.getSong().setStartPosition(i*getTimePerMeasure ());
            //scrollActivity.getSong().seekTo (i);
        }

        //scrollActivity.seekTo(seekBar.getProgress());
        //int newPos = seekBar.getProgress();
        //int timeLeft = seekBar.getMax() - newPos;
        //long minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeft) % TimeUnit.HOURS.toMinutes(1);
        //long seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeft) % TimeUnit.MINUTES.toSeconds(1);
        // textCountdown.setText(String.format("%d:%02d", minutes, seconds));

        //beatPos = newPos / autoScroll.getBeatInterval();
        //measure = newPos / (autoScroll.getBeatInterval() * autoScroll.getScoreData().getBeats());
    }


    @Override
    public void onScrollChanged(ScrollViewExt scrollView, int x, int y, int oldx, int oldy) {
        /*
        * y = (int) (autoScroll.getGroupArray().getScrollLine(measure) * actualLineHeight + posOffset * actualLineHeight);
        * progress = measure
        */
        //setProgress(groupArray.getStartOfLineMeasures((int)(y/scrollView.getLineHeight()) - posOffset));
        //setProgress((int)(((y/scrollView.getLineHeight()) - posOffset)/3));
        //scrollActivity.setNewSeek(groupArray.getStartOfLineMeasures(getProgress()) * scoreData.getBeats() * BeatInterval);
    }

    @Override
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
    @Override
    public void setPriorWrappedLines () {
        getGroupArray().setPriorWrappedLines();
    }

    @Override
    public int getLineMeasures () {
        int measures = getProgress();
        return groupArray.getLineMeasuresFromTotalMeasures(measures);
    }

    @Override
    public int getScrollLine() {
        return groupArray.getLine(getProgress()) + posOffset;
    }

    @Override
    public boolean isChordLine (int charPosition) {
        return groupArray.isChordLine(charPosition);
    }

    @Override
    public int getRepeat () {
        return groupArray.getCurrentGroup().getRepeat();
    }

}
