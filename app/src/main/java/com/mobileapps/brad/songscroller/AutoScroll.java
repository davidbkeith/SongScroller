package com.mobileapps.brad.songscroller;

import android.content.Context;
import android.support.v7.widget.AppCompatSeekBar;
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
   // protected int BeatInterval;
    protected int startLine;
    //protected String text;
    protected ScrollActivity scrollActivity;
    protected boolean updateProgress;
    protected double scrollSensitivity;  /// how much is scrolled per finger movement
    protected List<ChordData> chordPos;
    private GroupArray groupArray;
    protected GroupArray groupArrayEdited;

   /* public String getText() { return text; }

    public void setText(String text) { this.text = text; }
*/
    public int getBeatInterval() {
        return scoreData.getBeatInterval();
    }

   // public void setBeatInterval(int beatInterval) {
   //     BeatInterval = beatInterval;
   // }

    public ScoreData getScoreData() {
        return scoreData;
    }

    public void setScoreData (ScoreData scoreData) { this.scoreData = scoreData; }

 //   public int getNumLines () {
//        return text.trim().split("\n").length;
  //  }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        updateProgress = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) { updateProgress = false; }

    public GroupArray getGroupArrayOriginal() {
        return groupArray;
    }

    public GroupArray getGroupArray() {
        return groupArrayEdited == null ? groupArray: groupArrayEdited;
    }

    public AutoScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoScroll(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public long getTimePerBeat() {
        return scrollActivity.getAutoScroll().getScoreData().getBeats() * getBeatInterval();
    }

    public double getScrollYmin () {
        double retval = ((scrollActivity.getScrollVeiwHeight() / 2.0) / getGroupArray().getTotalBeats());
        return retval < 2 ? 2 : retval;
    }

    public boolean isValid () {
        return scoreData != null;
    }

    public String create(ScrollActivity scrollActivity, File file) {
        this.scrollActivity = scrollActivity;
        scrollSensitivity = 2.0;
        String text = "";
        int posOffset = 0;
        scoreData = null;
        //SpannableStringBuilder sb = new SpannableStringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            groupArray = new GroupArray(scrollActivity);

            while (scoreData == null && (line = br.readLine()) != null) {
                //// used for autoscrollguess only ///
                GroupData gd = new GroupData();
                gd.setOffsetChords(text.length());
                //gd.setLengthChords(line.length());
                //gd.setChordsLineNumber(posOffset);
                groupArray.add(gd);
                //////////////////////////////////////

                text += getScoreDataFromJson(line);
                text += "\n";
                posOffset = ++startLine;
            }

            if (scoreData != null) {
                //scoreData.setSongStartLine(posOffset);
                groupArray = new GroupArray(scrollActivity);
                text = groupArray.create(br, text, scoreData);
                findChords(text);
         //       groupArray.setChordData(chordPos);
         //       setMax (getSongDuration());
            }
            else {
                scoreData = new ScoreData(1, 120, 4 , 3);
                groupArray = new GroupArrayGuess(scrollActivity, groupArray);
                findChords(text);
                groupArray.create(chordPos, text);

                //if (scrollActivity.isEditScore()) {
                    groupArrayEdited = (GroupArray) groupArray.clone();
               // }
                //posOffset = groupArray.get(0).getChordsLineNumber();
                //groupArray.setScoreData(this);
            }

          //  long duration = (long) ((double) groupArray.getTotalBeats() / ( (double) scoreData.getBpm()/scoreData.getBeats()) * 60000);
          //  scrollActivity.getSong().setDuration(duration);
            scrollActivity.getScrollView().setMaxMeasuresPerLine (scoreData.getBeats() == 3 ? 9 : 8);
            setMax (getSongDuration()); // number of beats in song
            br.close();
        }
        catch (Exception e) {
            Log.e("File Read Error", e.toString());
        }

        return text;
    }

    public String getScoreDataFromJson (String JSON) {
        try {
            JSONObject jsonObject = new JSONObject(JSON);
            scoreData = new ScoreData(jsonObject.optInt("measures", 1), jsonObject.optInt("bpm", 120), jsonObject.optInt("beats", 4), jsonObject.optInt("start", 3));
            //BeatInterval = 60000 / scoreData.getBpm();

            //// tempo = beats (per measure) * (number of beats/song duration in seconds) * 60
          //  int bpm = (int) (scoreData.getBeats() * groupArray.getTotalBeats() * 60 / (scrollActivity.getSong().getDuration()/1000));
          //  scoreData.setBpm(bpm);

            /* (bpm/60) =  beats (per measure) * (number of beats/song duration in seconds) */
            /* (bpm/60) / beats (per measure) = (number of beats/song duration in seconds) */
            /* number of beats / ((bpm/60) / beats (per measure)) = song duration in seconds */

            return "";
        }
        catch (Exception e){
            Log.e("JSON Parsing Error:", e.toString());
            return JSON;
        }
    }

    public int getSongDuration () {
        return getGroupArray().getTotalBeats();
    }

    public void setSeekBarProgress() {
        long elpasedTime = scrollActivity.getSong().getPosition();
        setProgress((int) (elpasedTime/ getTimePerBeat()));
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (updateProgress) {
            scrollActivity.getSong().setStartPosition(i* getTimePerBeat());
        }
    }

  //  public void onScrollChanged(ScrollViewExt scrollView, int x, int y, int oldx, int oldy) {}
  //  public void showBeat () {}

    /*
    *
    *   groupArray convenience functions
    *
    *
    public void setWrappedLines () {
        if (getGroupArray() != null) {
            getGroupArray().setWrappedLines();
        }
    }*/

    private int getGroupIndex (int line) {
       // int groupIndex = (line - posOffset + scoreData.getScrollStart())/3;
       // return groupIndex > groupArray.size() - 1 ? groupArray.size() - 1 : groupIndex;
        if (scrollActivity.getTextView().getLayout() != null) {
            int lineStartPos = scrollActivity.getTextView().getLayout().getLineStart(line);

            int group = 0;
            for (GroupData gd: getGroupArray()) {
                if (lineStartPos <= gd.getOffsetChords()) {
                    return group;
                }
                group++;
            }
        }

     /*   int offset = line - scoreData.getSongStartLine() + scoreData.getScrollOffset();
        int groupIndex = 0;
        int lineCount = 0;
        for (int i=0; i<groupArray.size(); i++) {
            GroupData gdCurrent = groupArray.get(i);
            GroupData gdLast = i == 0 ? new GroupData() : groupArray.get(i-1);

            lineCount += gdCurrent.getGroupLineCount() + gdCurrent.getWrappedLines() - gdLast.getWrappedLines();

            if (lineCount >= offset) {
                groupIndex = i;
                break;
            }
        }*/
        return -1;
    }

    private int getGroupIndex () {
        return getGroupArray().getGroupIndex (getProgress());
    }

    public int getStartLineBeats() {
        int beats = getProgress();
        return getGroupArray().getStartLineBeatsFromTotalBeats(beats);
    }

    public int getLineBeats() {
        int beats = getProgress();
        return getGroupArray().getLineBeatsFromTotalBeats(beats);
    }

    public int getScrollLine(int beats) {
        return getGroupArray().getLine(beats) - scoreData.getScrollOffset();
    }

    public int getScrollLine() {
        if (scrollActivity.isEditLine()) {
            return getProgress();
        }
        else if (scrollActivity.isEditGroup()) {
           // if (scrollActivity.getTextView().getLayout() != null) {
            return scrollActivity.getTextView().getLayout().getLineForOffset(getGroupArray().get(getProgress()).getOffsetChords());
        }
        else {
            return getScrollLine(getProgress());
        }
        //return scrollActivity.getScrollView().getScrollLine();
    }

    public boolean isChordLine (int charPosition, String score) {
        return getGroupArray().isChordLine(charPosition, score);
    }

    /*public int getRepeat () {
        return groupArray.getCurrentGroup().getRepeat();
    }*/

    public void setSongPosition (double scrollY) {
        double offset = scrollY*scrollSensitivity;
        int newbeat = getProgress() + (int) (offset/getScrollYmin());
        scrollActivity.getSong().setStartPosition(newbeat * getTimePerBeat());
    }

    public void pageUp () {
        int newScrollLine = scrollActivity.getScrollView().getScrollLine() - scrollActivity.getLinesPerPage();
        newScrollLine = newScrollLine < 0 ? 0 : newScrollLine;

        int groupIndex = getGroupIndex(newScrollLine);
        int beats = groupIndex == 0 ? 0 : getGroupArray().getBeatsToEndOfLine(groupIndex) + 1;
        scrollActivity.getSong().setStartPosition(beats* getTimePerBeat());
    }

    public void pageDown () {
        int newScrollLine = scrollActivity.getScrollView().getScrollLine() + scrollActivity.getLinesPerPage();
        int groupIndex = getGroupIndex(newScrollLine) - 1;
        int lastGroupIndex = getGroupArray().getLastPageGroupIndex();

        groupIndex = groupIndex > lastGroupIndex ? lastGroupIndex : groupIndex;
        int beats = groupIndex > 0 ? getGroupArray().getBeatsToEndOfLine(groupIndex -1) + 1 : 1;
        scrollActivity.getSong().setStartPosition(beats* getTimePerBeat());
    }

    public void findChords(String text) {
        SpannableStringBuilder sb = new SpannableStringBuilder (text);

      /*  Matcher matcher = java.util.regex.Pattern.compile("\\[(.*?)\\]").matcher(sb.toString());
        while (matcher.find()) {
            final ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.songannotation));
            sb.setSpan(fcs, matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }

        matcher = java.util.regex.Pattern.compile("\\((.*?)\\)").matcher(sb.toString());
        while (matcher.find()) {
            final ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.songlinemod));
            sb.setSpan(fcs, matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }*/

        chordPos = new ArrayList<ChordData>();
 //       matcher = java.util.regex.Pattern.compile("(\\(*[CDEFGAB](?:b|bb)*(?:|#|##|add|sus|maj|min|aug|m|M|b|°|[0-9])*[\\(]?[\\d\\/-/+]*[\\)]?(?:[CDEFGAB](?:b|bb)*(?:#|##|add|sus|maj|min|aug|m|M|b|°|[0-9])*[\\d\\/]*)*\\)*)(?=[\\s|$])(?![a-z])").matcher(sb.toString());
       /// prevent consecutive capital letters
        Matcher matcher = java.util.regex.Pattern.compile("(\\(*(?<![A-Z])[CDEFGAB](?![A-Z])(?:b|bb)*(?:|#|##|add|sus|maj|min|aug|m|M|b|°|[0-9])*[\\(]?[\\d\\/-/+]*[\\)]?(?:[CDEFGAB](?:b|bb)*(?:#|##|add|sus|maj|min|aug|m|M|b|°|[0-9])*[\\d\\/]*)*\\)*)(?=[\\s|$])(?![a-z])").matcher(sb.toString());
        final ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.songchords));
        while (matcher.find()) {
            if (isChordLine(matcher.start(), text)) {
                //sb.setSpan(fcs, matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                chordPos.add(new ChordData(matcher.start(), text.substring(matcher.start(), matcher.end())));
            }
        }
        //final ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.songchords));
        //ChordData.setChords(chordPos, sb, fcs);
        //return sb;
    }
}
