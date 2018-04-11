package com.mobileapps.brad.songscroller;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brad on 2/26/18.
 */

public class GroupArray extends ArrayList<GroupData> {

    protected ScrollActivity scrollActivity;

   // public int getMeasuresPerChord() {
    //    return measuresPerChord;
   // }

    /*public void setMeasuresPerChord(int measuresPerChord) {
        this.measuresPerChord = measuresPerChord;
    }

    int measuresPerChord;*/

   /* public int getSongStartLine() {
        int count = 0;
        for (GroupData gd: this) {
            if (gd.getBeats() > 0) {
                return count;
            }
            count++;
        }
        return 0;
    }

    public void setSongStartLine(int startline) {
        this.startline = startline;
        //updateProgress = false;
    }
*/
   // public GroupData getCurrentGroup() {
    //    return getGroupFromBeats(scrollActivity.getAutoScroll().getProgress());
  //  }

    public ScrollActivity getScrollActivity() {
        return scrollActivity;
    }

    public void GroupArray () {}

    public GroupArray (ScrollActivity scrollActivity) {
        //measuresPerChord = 2;
        this.scrollActivity = scrollActivity;
    }

    public void create (List<ChordData> chordPos) {}

    public int get (GroupData groupData) {
        int count = 0;
        for (GroupData gd: this) {
            if (gd.equals(groupData)) {
                return count;
            }
            count++;
        }
        return -1;
    }

    public void reset () {
        for (GroupData gd: this) {
            gd.setMeasures();
        }
    }

    /*public void setLineMeasuresCount (GroupData gd, int lineMeasureCount) {
        int index = get(gd);
        if (index != -1) {
            int lineMeasures = get(index).getBeats();
            int delta = lineMeasures - lineMeasureCount;

            if (delta != 0) {
                int totMeasrues = 0;

                for (int i = index; i < size(); i++) {
                    totMeasrues += get(i).getBeats() - delta;
                    get(i).setBeats(get(i).getBeats() - delta);
                }

                /// since total beats has changed, must reset scroll max
                scrollActivity.getAutoScroll().setMax(totMeasrues);
            }
        }
    }*/

    public void setScoreData (AutoScroll autoScroll) {};

    public String create (BufferedReader br, String scoreText, ScoreData scoreData) {
        int groupLineCount = 1;
        int measuresToEndofLine = 0;
        String line;
        GroupData groupData = null;
        int lineCount = 0;

        try {
            while ((line = br.readLine()) != null) {
                //// line 1 is chords
                line = line.trim();
                //lineCount++;

                if (groupLineCount == 1) {
                    groupData = new GroupData();
                    groupData.setOffsetChords(scoreText.length());
                    groupData.setLengthChords(line.length());
                    //groupData.setChordsLineNumber(lineCount);
                }
                //// line 2 is lyrics, annotations, etc
                else if (groupLineCount == 2) {
                    //groupData.setLyricsLength(line.length());
                }
                //// line 3 is line metadata (number of beats, time signature, etc)
                else {
                    try {
                        if (line.trim().length() > 0) {
                            groupData.getLineMetaData(line);
                           // if (groupData.getMeasures() == -1) {
                           //     groupData.setMeasures(groupData.getRepeat() * scoreData.getBeatsPerLine());
                           // }
                            //measuresToEndofLine += groupData.getRepeat() * groupData.getMeasuresToEndofLine();
                       // } else {
                            //measuresToEndofLine += groupData.getRepeat() * scoreData.getMeasuresPerLine();
                            //groupData.setBeats(scoreData.getBeatsPerLine());
                        }

                       // groupData.setMeasuresToEndofLine(measuresToEndofLine);
                       // groupData.setGroupLineCount(3);
                        add(groupData);
                        line = "";
                    } catch (Exception e) {
                        Log.e("JSON Parsing Error:", e.toString());
                    }
                }
                scoreText += line + "\n";
                if (++groupLineCount > 3) {
                    groupLineCount = 1;
                };

                lineCount++;
            }
        }
        catch (Exception e) {
            Log.e("File Read Error", e.toString());
        }
        return scoreText;
    }


    public void setChordData (List<ChordData> chordPos) {

        if (size() > 0) {
            //// this array has all lines at this point, chords lines and non-chord lines, etc
            //// find real chord lines, eliminate others and save relevant data
            int chordStart = 0;
            for (GroupData gd: this) {
                //GroupData gd = get(i);
                //for (ChordData chordData : chordPos) {
                int chordIndex;
                List chords = new ArrayList();
                for (chordIndex = chordStart; chordIndex < chordPos.size(); chordIndex++) {
                    ChordData chordData = chordPos.get(chordIndex);
                    if (chordData.getStartPos() >= gd.getOffsetChords() && chordData.getStartPos() <= gd.getOffsetChords() + gd.getLengthChords()) {
                        /// has chords on this line
                        chords.add(chordData.getStartPos() - gd.getOffsetChords());
                        chords.add(chordData.getChord().length());
                    } else {
                        //// no chords on line, go to next line
                        break;
                    }
                }
                /// no chords on this line, go to next
                if (chords.size() > 0) {
                    gd.setChords(chords);
                    chordStart = chordIndex;
                }
            }
        }
    }

   /* public void sortGroups (int sortOrder) {
        if (sortOrder == 0) {
            Collections.sort(this, new GroupData.PositionCompare());
        }
        else {
            Collections.sort(this, new GroupData.LengthCompare());
        }
    }*/

    public boolean isChordLine (int charPosition) {
        int i=0;
        while (i < size() && charPosition >= get(i).getOffsetChords()){
            i++;
        }

        i--;
        return charPosition >= get(i).getOffsetChords() && charPosition < get(i).getOffsetChords() + get(i).getLengthChords();
    }

  /*  public int getPriorWrapped (int groupIndex) {
        int wrappedLines = 0;
        for (int i=0; i<groupIndex; i++) {
            wrappedLines += get(i).getWrappedLines();
        }
        return wrappedLines;
    }*/

   /* public void setWrappedLines () {
        TextView textView = scrollActivity.getTextView();
        int wrapped = 0;

        String text = textView.getText().toString();
        int linePos = scrollActivity.getAutoScroll().getScoreData().getSongStartLine();

        *//*for(GroupData gd: this) {
            int groupwrapped = 0;
            for (int groupLine = 0; groupLine < 2; groupLine++) {
                int lineEnd = textView.getLayout().getLineEnd(linePos + groupLine);

                String lineText = text.substring(0, lineEnd);
                if (!lineText.endsWith("\n")) {
                    groupwrapped++;
                }
            }
            linePos += (3 + groupwrapped);
            wrapped += groupwrapped;
            gd.setWrappedLines(wrapped);
        }
*//*
        //// preamble lines wrapped
        for (int i=0; i<linePos; i++) {
            int lineEnd = textView.getLayout().getLineEnd(i);

            String lineText = text.substring(0, lineEnd);
            if (!lineText.endsWith("\n")) {
                wrapped++;
            }
        }

        //// groups lines wrapped
        for(int i=0; i<size(); i++) {
            int groupwrapped = 0;
            int numLines = get(i).getGroupLineCount();

            for (int groupLine = 0; groupLine < numLines; groupLine++) {
                int lineEnd = textView.getLayout().getLineEnd(linePos + groupLine);

                String lineText = text.substring(0, lineEnd);
                if (!lineText.endsWith("\n")) {
                    groupwrapped++;
                }
            }
            linePos += (numLines + groupwrapped);
            get(i).setWrappedLines(groupwrapped);
        }
    }
*/
  /*  public int getStartOfLineMeasures (int line) {
        int group = line/3;
        if (group > 0 && group < size()) {
            return get(group-1).getMeasuresToEndofLine();
        }
        else if (group >= size()) {
            return (get(size() - 2).getMeasuresToEndofLine());
        }
        return 0;
    }

    public int getEndOfLineMeasures (int line) {
        int group = line/3;
        if (group < size()) {
            return get(group).getMeasuresToEndofLine();
        }
        return (0);
    }*/

    public int getTotalBeats() {
        int count = 0;
        for (GroupData gd :this) {
            count += gd.getBeats();
        }
        return count;
    }

    public int getStartLineBeatsFromTotalBeats(int beats) {
        int groupIndex = 0;
        int count = 0;
        if (size() > 1) {
            for (GroupData gd : this) {
                count += gd.getBeats();
                if (beats < count) {
                    if (groupIndex > 0) {
                        return (int) (count - get(groupIndex).getBeats());
                    } else {
                        return 0;
                    }
                }
                groupIndex++;
            }
            return (count - get(size() - 1).getBeats());
        }
        return 0;
    }

    public int getBeatsToEndOfLine(int index) {
        index = index <= size() -1 ? index : size() - 1;
        int count = 0;
        for (int i = 0; i<=index; i++) {
            count += get(i).getBeats();
        }
        return (count);
    }

    public int getLineBeatsFromTotalBeats(int beats) {
        int groupIndex = 0;
        int count = 0;
        for (GroupData gd : this) {
            count += gd.getBeats();
            if (beats < count) {
                return (int) (get(groupIndex).getBeats());
            }
            groupIndex++;
        }
        return (get(size()-1).getBeats());
    }

    public GroupData getGroupFromBeats(int beats) {
        AutoScroll autoScroll = scrollActivity.getAutoScroll();
        int count = 0;
        for (GroupData gd : this) {
            count += gd.getBeats();
            if (beats < count) {
                return gd;
            }
        }
        //// never here hopefully
        return (get(size()-1));
    }

  /*  public int getMeasuresFromSongPos (int groupIndex, long songPos) {
        AutoScroll autoScroll = scrollActivity.getAutoScroll();
        //int groupIndex = getGroupIndexFromSongPos(songPos);
        int measuresToGroup = getStartOfLineMeasures (groupIndex);
        long timePerMeasure = autoScroll.getTimePerBeat();
        long timeToStartofGroup = measuresToGroup * timePerMeasure;

        int measure = 1;
        while (timeToStartofGroup + measure * timePerMeasure < songPos) {
            measure++;
        }
        return measure;
    }*/
    public int getCurrentGroup () {
        if (scrollActivity.getTextView().getLayout() != null) {
         //   int currentScrollLine = scrollActivity.getScrollView().getScrollLine();
         //   int currentScrollPos = scrollActivity.getTextView().getLayout().getLineStart(currentScrollLine);
          //  currentScrollPos += get(0).getOffsetChords();  /// add position of first chord line
            int currentScrollPos = scrollActivity.getScrollView().getChordsPos();
            if (currentScrollPos != -1) {
                int group = 0;
                for (GroupData gd : this) {
                    if (group < size() - 1) {
                        if (currentScrollPos >= gd.getOffsetChords() && currentScrollPos < get(group + 1).getOffsetChords()) {
                            return group;
                        }
                    } else {
                        if (currentScrollPos >= gd.getOffsetChords()) {
                            return group;
                        }
                    }
                    group++;
                }
            }
        }
         //   int lineStartPos;//scrollActivity.getAutoScroll().getScoreData().getSongStartLine();

           /* if (scrollActivity.isEditing()) {
                currentGroupLine = currentScrollLine - scrollActivity.getAutoScroll().scoreData.getSongStartLine() - 3 + scrollActivity.getAutoScroll().scoreData.getScrollOffset();
                lineStartPos = scrollActivity.getTextView().getLayout().getLineStart(currentGroupLine);
                int group = 0;
                for (GroupData gd : this) {
                    if (group > 0) {
                        //int startLine = gd.getChordsLineNumber() + get(group - 1).getWrappedLines();
                        if (currentGroupLine >= startLine && currentGroupLine < startLine + gd.getGroupLineCount() + gd.getWrappedLines() - 1) {
                            return group;
                        }
                    } else if (currentGroupLine >= startLine && currentGroupLine < startLine + gd.getGroupLineCount()) {
                        return group;
                    }

                    int offsetNext;
                    if (group < size() - 1) {
                        offsetNext = get(group + 1).offsetChords;
                    } else {
                        offsetNext = get(size() - 1).offsetChords;
                    }
                    lineStartPos += scrollActivity.getTextView().getLayout().getLineStart(offsetNext);
                    group++;
                }
            } else {
                //currentGroupLine = currentScrollLine - scrollActivity.getAutoScroll().scoreData.getSongStartLine() + scrollActivity.getAutoScroll().scoreData.getScrollOffset();
                currentScrollLine = scrollActivity.getScrollView().getScrollLine() - scrollActivity.getAutoScroll().scoreData.getSongStartLine() + scrollActivity.getAutoScroll().scoreData.getScrollOffset();
                int group = 0;
                for (GroupData gd : this) {
               *//* if (group > 0) {
                    if (currentScrollLine == startLine + get(group - 1).getWrappedLines() ) {
                        return group;
                    }
                }*//*
                    if (currentScrollLine == startLine) {
                        return group;
                    }

                    startLine += get(group).getGroupLineCount() + get(group).getWrappedLines();
                    group++;
                }
            }
        }*/
        //GroupData groupData = new GroupData();
        return -1;
    }

    public int getLastPageGroupIndex () {
        if (scrollActivity.getTextView().getLayout() != null) {
            int totalLines = scrollActivity.getTotalLines();
            int linesPerPage = scrollActivity.getLinesPerPage();

            int lineStartLastPage = totalLines - linesPerPage >= 0 ? totalLines - linesPerPage : 0;
            int lineStartPos = scrollActivity.getTextView().getLayout().getLineStart(lineStartLastPage);

            int group = 0;
            for (GroupData gd: this) {
                if (gd.getOffsetChords() > lineStartPos) {
                    return group > 0 ? group - 1 : 0;
                }
            }
        }


    /*    int lineCount = 0;
        AutoScroll autoScroll = scrollActivity.getAutoScroll();
        for (int i=size()-1; i>=0; i--) {
            if (i==0) {
                lineCount += get(i).getGroupLineCount() + get(i).getWrappedLines();
            }
            else {
                lineCount += get(i).getGroupLineCount() + get(i).getWrappedLines() - get(i - 1).getWrappedLines();
            }
            if (lineCount >= totalLines) {
                return i+1;
            }
        }*/
        return 0;
    }

  /*  public int getLastVisibleLineMeasure (int groupIndex) {
        //int count = scrollActivity.getAutoScroll().getCurrentGroupIndex();
        int totalLines = scrollActivity.getLinesPerPage();
        int lineCount = 0;
        Point point = new Point(0,0);
        int measure;
        int lastPageGroupIndex = getLastPageGroupIndex();
        int index=lastPageGroupIndex;

        if (groupIndex < lastPageGroupIndex) {
            if (groupIndex + 1 < size()) {
                for (int i = groupIndex + 1; i < size(); i++) {
                    int count = lineCount + 3 + get(i).getWrappedLines() - get(i-1).getWrappedLines();
                    if (count < totalLines) {
                        lineCount = count;
                    } else {
                       // measure = get(i - 1).getMeasuresToEndofLine() + 1;
                       // point.x = measure;
                       // point.y = lineCount;
                        index = i > 0 ? i-1 : 0;
                        break;
                    }
                }
            }// else if (size() > 0) {
               // point.x = get(size() - 1).getMeasuresToEndofLine();
           //     point.y = size() * 3 + get(size() - 1).getPriorWrappedLines();
           // }
        }


        //index = index < lastPageGroupIndex ? index : lastPageGroupIndex;
        if (index > lastPageGroupIndex) {
            index = lastPageGroupIndex;
        }
        return index > 0 ? get(index - 1).getMeasuresToEndofLine() + 1: 1;
    }*/

    public int getGroupIndex(int measure) {
        int count = 0;
        int beats = 0;
        for (GroupData gd : this) {
            beats += gd.getBeats();
            if (measure < beats) {
                return count;
            }
            count++;
        }
        return (int) (size()-1);
    }

    public int getGroupIndex() {
        int count = 0;
        int beats = 0;
        int measure = scrollActivity.getAutoScroll().getProgress();
        for (GroupData gd : this) {
            beats += gd.getBeats();
            if (measure < beats) {
                return count;
            }
            count++;
        }
        return (int) (size()-1);
    }

    public int getLine(int beats) {
        int beatCount = 0;
        for (GroupData gd : this) {
            beatCount += gd.getBeats();
            if (beats < beatCount) {
                return scrollActivity.getTextView().getLayout().getLineForOffset(gd.getOffsetChords());
            }
        }
        return -1;  /// not found
    }

    public int getScrollLineFromPos (int pos){
        int LinePos = (int) (pos/scrollActivity.getScrollView().getLineHeight());
        if (LinePos < size()) {
            return get(LinePos).getOffsetChords();
        }
        return -1;
    }

    public void updatePositions (int offsetChords, int offsetCharPos) {
        //int group = groupI
        int count=0;
        while (get(count++).getOffsetChords() < offsetChords);
        count = count - 1 == -1 ? 0 : count - 1;

        for (int i=count; i<size(); i++) {
            get(i).setOffsetChords(get(i).getOffsetChords() + offsetCharPos);
            //get(i).setChordsLineNumber(get(i).getChordsLineNumber() + offsetLineCount);
        }
    }

    public int insertNewGroup (GroupData groupData) {
        int count = 0;
        for (GroupData gd: this) {
            if (groupData.offsetChords <= gd.offsetChords) {
                add(count, groupData);
                return count;
            }
            count++;
        }
        add(groupData);
        return count;
    }
}
