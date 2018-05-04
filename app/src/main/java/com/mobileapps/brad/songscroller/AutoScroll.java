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
import java.util.Arrays;
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
    protected List<ChordData> chordPos;
    private GroupArray groupArray;
    protected GroupArray groupArrayEdited;

    public void setMax () {
        if (getGroupArray() != null) {
            if (scrollActivity.isEditLine()) {
                setMax(scrollActivity.getTextView().getLineCount());
            } else if (scrollActivity.isEditGroup()) {
                setMax(getGroupArray().size()-1);
            } else {
                setMax(getGroupArray().getTotalMeasures());
            }
        }
    }

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

    //public long getTimePerBeat() {

        //return 60000/scoreData.getBpm();
        //return scrollActivity.getAutoScroll().getScoreData().getBeatsPerMeasure() * getBeatInterval();
    //}

    public double getScrollYmin () {
        double retval = ((scrollActivity.getScrollVeiwHeight() / 2.0) / getGroupArray().getTotalMeasures());
        return retval < 2 ? 2 : retval;
    }

    public boolean isValid () {
        return scoreData != null;
    }

    public String create(ScrollActivity scrollActivity, File file) {
        this.scrollActivity = scrollActivity;
        String text = "";
        int posOffset = 0;
        scoreData = null;
        //SpannableStringBuilder sb = new SpannableStringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String songData = scrollActivity.getSongData();
            if (songData != null && !songData.isEmpty()) {
                scoreData = new ScoreData(songData);
                if (scoreData.getScorePath() == null || scoreData.getScorePath().isEmpty()) {
                    scoreData.setScorePath(scrollActivity.getSong().getSheetMusicPath());
                }
                groupArray = new GroupArray(scrollActivity, songData);
            }
            else {
                groupArray = new GroupArray(scrollActivity);
            }

            String line;
            while ((line = br.readLine()) != null) {
                GroupData gd = new GroupData();
                gd.setOffsetChords(text.length());
                groupArray.add(gd);
                //////////////////////////////////////

                if (scoreData == null) {
                    text += getScoreDataFromJson(line);
                }
                else {
                    text += line;
                }
                text += "\n";
                posOffset = ++startLine;
            }

            //// autoscroll for 3 lines in per group format
            if (scoreData != null && groupArray == null) {
                //scoreData.setSongStartLine(posOffset);
                groupArray = new GroupArray(scrollActivity);
                text = groupArray.create(br, text, scoreData);
          //      findChords(text);
         //       groupArray.setChordData(chordPos);
         //       setMax (getSongDuration());
            }
          /*  else if ((scoreDataIndex = text.indexOf("{scoredata={")) != -1) {

                int end = text.indexOf(",}}", scoreDataIndex);

                if (end != -1) {
                    String score = text.substring(scoreDataIndex + 12, end);
                    List<String> data = Arrays.asList(score.split(","));
                    scoreData = new ScoreData(Integer.parseInt((String) data.get(0)), 4, Integer.parseInt((String) data.get(1)), Integer.parseInt((String) data.get(2)));

                    groupArray = new GroupArray(scrollActivity);
                    Layout layout = scrollActivity.getTextView().getLayout();

                    for (int i=3; i<data.size(); i++) {
                        String[] items = data.get(i).split("\\.");
                        int measures = Integer.parseInt(items[1]);
                        GroupData groupData = new GroupData(Integer.parseInt(items[0]), measures);
                        groupArray.add(groupData);
                    }
                }

                text = text.substring(0, scoreDataIndex);
                setMax(scrollActivity.getSong().getTotalMeasures()); // number of measures in song
                scrollActivity.setView(text, true);
                br.close();
            }*/
            else if (scoreData == null) {
                scoreData = new ScoreData(120, 4, 4 , 3, "");
                groupArray = new GroupArrayGuess(scrollActivity, groupArray);
                findChords(text);
                groupArray.create(chordPos, text);

                //if (scrollActivity.isEditScore()) {
                //    groupArrayEdited = (GroupArray) groupArray.clone();
               // }
                //posOffset = groupArray.get(0).getChordsLineNumber();
                //groupArray.setScoreData(this);
            }

            groupArrayEdited = (GroupArray) groupArray.clone();

          //  long duration = (long) ((double) groupArray.getTotalMeasures() / ( (double) scoreData.getBpm()/scoreData.getMeasures()) * 60000);
          //  scrollActivity.getSong().setDuration(duration);
            scrollActivity.getScrollView().setMaxMeasuresPerLine (scoreData.getMeasures() == 3 ? 9 : 8);
            setMax (scrollActivity.getSong().getTotalMeasures()); // number of measures in song
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
            scoreData = new ScoreData(jsonObject.optInt("bpm", 120), jsonObject.optInt("measures", 4), jsonObject.optInt("timesig", 4), jsonObject.optInt("start", 3), jsonObject.optString("scorepath", ""));
            //BeatInterval = 60000 / scoreData.getBpm();

            //// tempo = measures (per measure) * (number of measures/song duration in seconds) * 60
          //  int bpm = (int) (scoreData.getMeasures() * groupArray.getTotalMeasures() * 60 / (scrollActivity.getSong().getDuration()/1000));
          //  scoreData.setBpm(bpm);

            /* (bpm/60) =  measures (per measure) * (number of measures/song duration in seconds) */
            /* (bpm/60) / measures (per measure) = (number of measures/song duration in seconds) */
            /* number of measures / ((bpm/60) / measures (per measure)) = song duration in seconds */

            return "";
        }
        catch (Exception e){
            Log.e("JSON Parsing Error:", e.toString());
            return JSON;
        }
    }

   /* public int getSongDuration () {
        return getGroupArray().getTotalMeasures();
    }*/

    public void setSeekBarProgress() {
        long elpasedTime = scrollActivity.getSong().getPosition();
        setProgress((int) (elpasedTime/ (scoreData.getBeatInterval() * scoreData.getBeatsPerMeasure())));
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (updateProgress) {
            scrollActivity.getSong().setStartPosition(i * scoreData.getBeatInterval() * scoreData.getBeatsPerMeasure());
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

    private int getGroupIndex () {
        return getGroupArray().getGroupIndex (getProgress());
    }

    public int getStartLineMeasures() {
        int measures = getProgress();
        return getGroupArray().getStartLineMeasuresFromTotalMeasures(measures);
    }

    public int getLineMeasures() {
        int group = getGroupArray().getCurrentGroup();
        //return getGroupArray().getLineMeasuresFromTotalMeasures(measures);
        return getGroupArray().get(group).getMeasures();
    }

    public int getScrollLine(int measures) {
        return getGroupArray().getLine(measures) - scoreData.getScrollOffset();
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

    public void pageUp () {
        int newScrollLine = scrollActivity.getScrollView().getScrollLine() - scrollActivity.getLinesPerPage() - 3;
        newScrollLine = newScrollLine < 0 ? 0 : newScrollLine;

        if (scrollActivity.isEditLine()) {
            setProgress(newScrollLine);
        }
        else if (scrollActivity.isEditGroup()){
            int groupIndex = getGroupArray().getGroupFromLine(newScrollLine);
            setProgress(groupIndex);
        }
        else {
            int groupIndex = getGroupArray().getGroupFromLine(newScrollLine);
            int beats = groupIndex == 0 ? 0 : getGroupArray().getBeatsToEndOfLine(groupIndex) + 1;
            scrollActivity.getSong().setStartPosition(beats * scoreData.getBeatInterval());
        }
    }

    public void pageDown () {
       // int newScrollLine = scrollActivity.getScrollView().getScrollLine() + scrollActivity.getLinesPerPage();
        int newScrollLine = scrollActivity.getLastVisibleLine() - 3;
       // int groupIndex = getGroupIndex(newScrollLine) - 1;
       // int lastVisibleLine = scrollActivity.getLastVisibleLine();


        if (scrollActivity.isEditLine()) {
            setProgress(newScrollLine);
        }
        else if (scrollActivity.isEditGroup()){
            int groupIndex = getGroupArray().getGroupFromLine(newScrollLine);
            setProgress(groupIndex);
        }
        else {
            if (newScrollLine > scrollActivity.getTotalLines() - scrollActivity.getLinesPerPage()) {
                newScrollLine = scrollActivity.getTotalLines() - scrollActivity.getLinesPerPage();
            }

            //int lastGroupIndex = getGroupArray().getLastPageGroupIndex();
            int groupIndex = getGroupArray().getGroupFromLine(newScrollLine);

            //groupIndex = groupIndex > lastGroupIndex ? lastGroupIndex : groupIndex;
            int beats = groupIndex > 0 ? getGroupArray().getBeatsToEndOfLine(groupIndex - 1) + 1 : 1;
            scrollActivity.getSong().setStartPosition(beats * scoreData.getBeatInterval());
        }
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
