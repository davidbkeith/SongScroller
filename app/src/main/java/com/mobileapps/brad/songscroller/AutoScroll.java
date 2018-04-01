package com.mobileapps.brad.songscroller;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by brad on 3/2/18.
 */

public class AutoScroll extends AppCompatSeekBar implements android.widget.SeekBar.OnSeekBarChangeListener {
    protected ScoreData scoreData;
    protected int BeatInterval;
    protected int startLine;
    protected String text;
    protected ScrollActivity scrollActivity;
    protected boolean updateProgress;
    protected double scrollSensitivity;  /// how much is scrolled per finger movement
    protected List<ChordData> chordPos;

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }

    public int getBeatInterval() {
        return BeatInterval;
    }

    public void setBeatInterval(int beatInterval) {
        BeatInterval = beatInterval;
    }

    public ScoreData getScoreData() {
        return scoreData;
    }

    public void setScoreData (ScoreData scoreData) { this.scoreData = scoreData; }

    public int getNumLines () {
        return text.trim().split("\n").length;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        updateProgress = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) { updateProgress = false; }

    public int getPosOffset() { return posOffset; }

    public void setPosOffset(int posOffset) {
        this.posOffset = posOffset;
        updateProgress = false;
    }

    protected int posOffset;   protected GroupArray groupArray;

    public GroupArray getGroupArray() {
        return groupArray;
    }

    public AutoScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoScroll(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public long getTimePerMeasure () {
        return scrollActivity.getAutoScroll().getScoreData().getBeats() * getBeatInterval();
    }

    public double getScrollYmin () {
        double retval = ((scrollActivity.getScrollVeiwHeight() / 2.0) / groupArray.getTotalMeasures());
        return retval < 2 ? 2 : retval;
    }

    public boolean isValid () {
        return scoreData != null;
    }

    public SpannableStringBuilder initialize (ScrollActivity scrollActivity, File file) {
        this.scrollActivity = scrollActivity;
        scrollSensitivity = 2.0;
        text = "";
        SpannableStringBuilder sb = new SpannableStringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            groupArray = new GroupArray(scrollActivity);

            while (scoreData == null && (line = br.readLine()) != null) {
                //// used for autoscrollguess only ///
                GroupData gd = new GroupData();
                gd.setOffsetChords(text.length());
                gd.setChordsLength(line.length());
                gd.setChordsLineNumber(posOffset);
                groupArray.add(gd);
                //////////////////////////////////////

                text += getScoreDataFromJson(line);
                text += "\n";
                posOffset = ++startLine;
            }

            if (scoreData != null) {
                groupArray = new GroupArray(scrollActivity);
                text = groupArray.create(br, text, scoreData);
                sb = formatText();
         //       setMax (getSongDuration());
            }
            else {
                groupArray = new GroupArrayGuess(scrollActivity, groupArray);
                sb = formatText();
                groupArray.create(chordPos);
                //posOffset = groupArray.get(0).getChordsLineNumber();
                //groupArray.setScoreData(this);
            }

            setMax (getSongDuration());
            br.close();
        }
        catch (Exception e) {
            Log.e("File Read Error", e.toString());
        }

        return sb;
    }

    public String getScoreDataFromJson (String JSON) {
        try {
            JSONObject jsonObject = new JSONObject(JSON);
            scoreData = new ScoreData(jsonObject.optInt("bpm"), jsonObject.optInt("beats", 4), jsonObject.optInt("measures", 16), jsonObject.optInt("start", 3));
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

    public void setSeekBarProgress() {
        long elpasedTime = scrollActivity.getSong().getPosition();
        setProgress((int) (elpasedTime/getTimePerMeasure ()));
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (updateProgress) {
            scrollActivity.getSong().setStartPosition(i*getTimePerMeasure ());
        }
    }

    public void onScrollChanged(ScrollViewExt scrollView, int x, int y, int oldx, int oldy) {}
    public void showBeat () {}

    /*
    *
    *   groupArray convenience functions
    *
    */
    public void setWrappedLines () {
        if (getGroupArray() != null) {
            getGroupArray().setWrappedLines();
        }
    }

    private int getGroupIndex (int line) {
       // int groupIndex = (line - posOffset + scoreData.getScrollStart())/3;
       // return groupIndex > groupArray.size() - 1 ? groupArray.size() - 1 : groupIndex;

        int offset = line - posOffset + scoreData.getScrollStart();
        int groupIndex = 0;
        int lineCount = 0;
        for (int i=0; i<groupArray.size(); i++) {
            GroupData gdCurrent = groupArray.get(i);
            GroupData gdLast = i == 0 ? new GroupData() : groupArray.get(i-1);

            lineCount += gdCurrent.getGroupLineCount() + gdCurrent.getWrappedLines() - gdLast.getWrappedLines();

            if (lineCount > offset) {
                groupIndex--;
                break;
            }
        }
        return groupIndex > 0 ? groupIndex : 0;
    }

    private int getGroupIndex () {
        return groupArray.getGroupIndex (getProgress());
    }

    public int getStartLineMeasure () {
        int measures = getProgress();
        return groupArray.getStartLineMeasuresFromTotalMeasures(measures);
    }

    public int getLineMeasures () {
        int measures = getProgress();
        return groupArray.getLineMeasuresFromTotalMeasures(measures);
    }

    public int getScrollLine(int measure) {
        return groupArray.getLine(measure) + posOffset - scoreData.getScrollStart();
    }

    public int getScrollLine() {
        return getScrollLine(getProgress());
    }

    public boolean isChordLine (int charPosition) {
        return groupArray.isChordLine(charPosition);
    }

    public int getRepeat () {
        return groupArray.getCurrentGroup().getRepeat();
    }

    public void setSongPosition (double scrollY) {
        double offset = scrollY*scrollSensitivity;
        int newMeasure = getProgress() + (int) (offset/getScrollYmin());
        scrollActivity.getSong().setStartPosition(newMeasure * getTimePerMeasure());
    }

    public void pageUp () {
        int newScrollLine = scrollActivity.getScrollView().getScrollLine() - scrollActivity.getLinesPerPage();
        newScrollLine = newScrollLine < 0 ? 0 : newScrollLine;

        int groupIndex = getGroupIndex(newScrollLine);
        int measures = groupIndex == 0 ? 0 : groupArray.get(groupIndex).getMeasuresToEndofLine() + 1;
        scrollActivity.getSong().setStartPosition(measures*getTimePerMeasure ());
    }

    public void pageDown () {
        int newScrollLine = scrollActivity.getScrollView().getScrollLine() + scrollActivity.getLinesPerPage();
        int groupIndex = getGroupIndex(newScrollLine) - 1;
        int lastGroupIndex = groupArray.getLastPageGroupIndex();

        groupIndex = groupIndex > lastGroupIndex ? lastGroupIndex : groupIndex;
        int measures = groupIndex > 0 ? groupArray.get(groupIndex -1).getMeasuresToEndofLine() + 1 : 1;
        scrollActivity.getSong().setStartPosition(measures*getTimePerMeasure ());
    }

    public SpannableStringBuilder formatText() {
        SpannableStringBuilder sb = new SpannableStringBuilder (text);

        Matcher matcher = java.util.regex.Pattern.compile("\\[(.*?)\\]").matcher(sb.toString());
        while (matcher.find()) {
            final ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.songannotation));
            sb.setSpan(fcs, matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }

        matcher = java.util.regex.Pattern.compile("\\((.*?)\\)").matcher(sb.toString());
        while (matcher.find()) {
            final ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.songlinemod));
            sb.setSpan(fcs, matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }

        chordPos = new ArrayList<>();
        matcher = java.util.regex.Pattern.compile("(\\(*[CDEFGAB](?:b|bb)*(?:|#|##|add|sus|maj|min|aug|m|M|b|°|[0-9])*[\\(]?[\\d\\/-/+]*[\\)]?(?:[CDEFGAB](?:b|bb)*(?:#|##|add|sus|maj|min|aug|m|M|b|°|[0-9])*[\\d\\/]*)*\\)*)(?=[\\s|$])(?! [a-z])").matcher(sb.toString());
        while (matcher.find()) {
            if (isChordLine(matcher.start())) {
                final ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.songchords));
                sb.setSpan(fcs, matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                chordPos.add(new ChordData(matcher.start(), text.substring(matcher.start(), matcher.end())));
            }
        }
        return sb;
    }
}
