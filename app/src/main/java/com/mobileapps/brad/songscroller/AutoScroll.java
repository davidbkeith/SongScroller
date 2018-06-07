package com.mobileapps.brad.songscroller;

import android.content.Context;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.SeekBar;

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
    static public ScoreData scoreData;
    protected ScrollActivity scrollActivity;
    protected boolean updateProgress;
    protected List<ChordData> chordPos;
    private GroupArray groupArray;

    public void setMax () {
        if (getGroupArray() != null) {
            if (scrollActivity.isEditText()) {
                setMax(scrollActivity.getTextView().getLineCount());
            }/* else if (scrollActivity.isEditGroup()) {
                setMax(getGroupArray().size()-1);
            } */
            else {
                setMax(getGroupArray().getTotalMeasures());
            }
        }
    }

    public int getBeatInterval() {
        return scoreData.getBeatInterval();
    }

    public ScoreData getScoreData() {
        return scoreData;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        updateProgress = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) { updateProgress = false; }


    public GroupArray getGroupArray() {
        return groupArray;
    }

    public AutoScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoScroll(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public double getScrollYmin () {
        double retval = ((scrollActivity.getScrollVeiwHeight() / 2.0) / getGroupArray().getTotalMeasures());
        return retval < 2 ? 2 : retval;
    }

   /* public int getStartLineMeasures() {
        int measures = getProgress();
        return getGroupArray().getStartLineMeasuresFromTotalMeasures(measures);
    }*/

    private String getScoreText (File file) {
        String text = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if (scoreData == null) {
                    GroupData gd = new GroupData();
                    gd.setOffsetChords(text.length());
                    groupArray.add(gd);

                    text += getScoreDataFromJson(line);
                    if (scoreData != null) {
                        //// autoscroll for 3 lines in per group format
                        text += "\n";

                        groupArray = new GroupArray(scrollActivity);
                        text = groupArray.create(br, text, scoreData);
                        break;
                    }
                }
                else {
                    text += line;
                }
                text += "\n";
            }
            br.close();
        }
        catch (Exception e) {
            Log.e("File Read Error", e.toString());
        }
        return text;
    }

    public String create(ScrollActivity scrollActivity, File file) {
        this.scrollActivity = scrollActivity;
        scoreData = null;
        String text = "";

        String songData = scrollActivity.getSongData();
        if (songData != null && !songData.isEmpty()) {
            scoreData = new ScoreData(songData);
            if (scoreData.getScorePath() == null || scoreData.getScorePath().isEmpty()) {
                scoreData.setScorePath(scrollActivity.getSong().getSheetMusicPath());
            }
            groupArray = new GroupArray(scrollActivity, songData);
            text = getScoreText(file);
        }
        else {
            groupArray = new GroupArray(scrollActivity);
            text = getScoreText(file);

            if (scoreData == null) {
                scoreData = new ScoreData(120, 4, 4 , 3, "");
                groupArray = new GroupArrayGuess(scrollActivity, groupArray);
                findChords(text);
                groupArray.create(chordPos, text);
            }
        }
        return text;
    }

    public String getScoreDataFromJson (String JSON) {
        try {
            JSONObject jsonObject = new JSONObject(JSON);
            scoreData = new ScoreData(jsonObject.optInt("bpm", 120), jsonObject.optInt("measures", 4), jsonObject.optInt("timesig", 4), jsonObject.optInt("start", 3), jsonObject.optString("scorepath", ""));
            return "";
        }
        catch (Exception e){
            Log.e("JSON Parsing Error:", e.toString());
            return JSON;
        }
    }

    public void setSeekBarProgress() {
        if (scrollActivity.isPlayLine()) {
            if (!scrollActivity.isEditText()) {
                int newGroup = groupArray.getGroupIndex(getProgress());
                if (newGroup !=  groupArray.getCurrentGroup()) {
                    scrollActivity.getSong().pauseSeekStart();    //// reset to beginning of line
                    scrollActivity.getIvPlay().setImageResource(android.R.drawable.ic_media_play);
                }
            }
            else {
                int newLine = groupArray.getLine(scrollActivity.getSong().getMeasure());
                if (newLine != getProgress()) {
                    scrollActivity.getSong().pauseSeekStart();    //// reset to beginning of line
                    scrollActivity.getIvPlay().setImageResource(android.R.drawable.ic_media_play);
                }
            }
        }
        //else {
        if (!scrollActivity.isEditText()) {
            setProgress(scrollActivity.getSong().getMeasure());
        }
        //}
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (updateProgress) {
            scrollActivity.getSong().setStartPosition(i * scoreData.getBeatInterval() * scoreData.getBeatsPerMeasure());
        }
    }

    public int getScrollLine() {
        if (scrollActivity.isEditText()) {
            return getProgress();
        }
        /*else if (scrollActivity.isEditGroup()) {
             return getGroupArray().getLineFromGroup(getProgress());
        }*/
        else {
            return getGroupArray().getScrollLine(getProgress());
        }
    }

    public boolean isChordLine (int charPosition, String score) {
        return getGroupArray().isChordLine(charPosition, score);
    }

    public void pageUp () {
        int newScrollLine = scrollActivity.getScrollView().getScrollLine() - scrollActivity.getLinesPerPage() - 3;
        newScrollLine = newScrollLine < 0 ? 0 : newScrollLine;

        if (scrollActivity.isEditText()) {
            setProgress(newScrollLine);
        }
       /* else if (scrollActivity.isEditGroup()){
            int groupIndex = getGroupArray().getGroupFromLine(newScrollLine);
            setProgress(groupIndex);
        }*/
        else {
            int groupIndex = getGroupArray().getGroupFromLine(newScrollLine);
            int measures = groupIndex == 0 ? 0 : getGroupArray().getMeasuresToEndOfLine(groupIndex) + 1;
            scrollActivity.getSong().setStartPosition(measures * scoreData.getBeatsPerMeasure()  * scoreData.getBeatInterval());
        }
    }

    public void pageDown () {
        int newScrollLine = scrollActivity.getLastVisibleLine() - 3;

        if (scrollActivity.isEditText()) {
            setProgress(newScrollLine);
        }
       /* else if (scrollActivity.isEditGroup()){
            int groupIndex = getGroupArray().getGroupFromLine(newScrollLine);
            setProgress(groupIndex);
        }*/
        else {
            if (newScrollLine > scrollActivity.getTotalLines() - scrollActivity.getLinesPerPage()) {
                newScrollLine = scrollActivity.getTotalLines() - scrollActivity.getLinesPerPage();
            }

            int groupIndex = getGroupArray().getGroupFromLine(newScrollLine);

            int measures = groupIndex > 0 ? getGroupArray().getMeasuresToEndOfLine(groupIndex - 1) + 1 : 1;
            scrollActivity.getSong().setStartPosition(measures * scoreData.getBeatsPerMeasure() * scoreData.getBeatInterval());
        }
    }

    public void findChords(String text) {
        SpannableStringBuilder sb = new SpannableStringBuilder (text);

        chordPos = new ArrayList<ChordData>();
        Matcher matcher = java.util.regex.Pattern.compile("(\\(*(?<![A-Z])[CDEFGAB](?![A-Z])(?:b|bb)*(?:|#|##|add|sus|maj|min|aug|m|M|b|°|[0-9])*[\\(]?[\\d\\/-/+]*[\\)]?(?:[CDEFGAB](?:b|bb)*(?:#|##|add|sus|maj|min|aug|m|M|b|°|[0-9])*[\\d\\/]*)*\\)*)(?=[\\s|$])(?![a-z])").matcher(sb.toString());
        final ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.songchords));
        while (matcher.find()) {
            if (isChordLine(matcher.start(), text)) {
                chordPos.add(new ChordData(matcher.start(), text.substring(matcher.start(), matcher.end())));
            }
        }
    }
}
