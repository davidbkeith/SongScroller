package com.mobileapps.brad.songscroller;

import android.graphics.Point;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brad on 2/26/18.
 */

public class GroupArray extends ArrayList<GroupData> {

    protected ScrollActivity scrollActivity;

    public int getMeasuresPerChord() {
        return measuresPerChord;
    }

    public void setMeasuresPerChord(int measuresPerChord) {
        this.measuresPerChord = measuresPerChord;
    }

    int measuresPerChord;

    public GroupData getCurrentGroup() {
        return getGroupFromMeasure(scrollActivity.getAutoScroll().getProgress());
    }

    public ScrollActivity getScrollActivity() {
        return scrollActivity;
    }

    public void GroupArray () {}

    public GroupArray (ScrollActivity scrollActivity) {
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

    public void setLineMeasuresCount (GroupData gd, int lineMeasureCount) {
        int index = get(gd);
        if (index != -1) {
            int delta, lineMeasures;
            if (index > 0) {
                lineMeasures = get(index).getMeasuresToEndofLine() - get(index-1).getMeasuresToEndofLine();
            }
            else {
                lineMeasures = get(index).getMeasuresToEndofLine();
            }

            delta = lineMeasures - lineMeasureCount;
            for (int i=index; i<size(); i++) {
                get(i).setMeasuresToEndofLine(get(i).getMeasuresToEndofLine()-delta);
            }
        }
    }

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
                    groupData = new GroupData(scoreData);
                    groupData.setOffsetChords(scoreText.length());
                    groupData.setChordsLength(line.length());
                    groupData.setChordsLineNumber(lineCount);
                }
                //// line 2 is lyrics, annotations, etc
                else if (groupLineCount == 2) {
                    //groupData.setLyricsLength(line.length());
                }
                //// line 3 is line metadata (number of measures, time signature, etc)
                else {
                    try {
                        if (line.trim().length() > 0) {
                            groupData.getLineMetaData(line);
                            if (groupData.getMeasuresToEndofLine() == -1) {
                                groupData.setMeasuresToEndofLine(scoreData.getMeasuresPerLine());
                            }
                            measuresToEndofLine += groupData.getRepeat() * groupData.getMeasuresToEndofLine();
                        } else {
                            measuresToEndofLine += groupData.getRepeat() * scoreData.getMeasuresPerLine();
                        }

                        groupData.setMeasuresToEndofLine(measuresToEndofLine);
                        groupData.setGroupLineCount(3);
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
        return charPosition >= get(i).getOffsetChords() && charPosition < get(i).getOffsetChords() + get(i).getChordsLength();
    }

    public void setWrappedLines () {
        TextView textView = scrollActivity.getTextView();
        int wrapped = 0;

        String text = textView.getText().toString();
        int linePos = scrollActivity.getAutoScroll().getPosOffset();

        /*for(GroupData gd: this) {
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
*/
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
            wrapped += groupwrapped;
            get(i).setWrappedLines(wrapped);
        }
    }

    public int getStartOfLineMeasures (int line) {
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
    }

    public int getTotalMeasures () {
        return get(size()-1).getMeasuresToEndofLine();
    }

    public int getStartLineMeasuresFromTotalMeasures (int measures) {
        int groupIndex = 0;
        for (GroupData gd : this) {
            if (measures < gd.getMeasuresToEndofLine()) {
                if (groupIndex > 0) {
                    return (int) (get(groupIndex - 1).getMeasuresToEndofLine());
                }
                else {
                    break;
                }
            }
            groupIndex++;
        }
        return (0);
    }

    public int getLineMeasuresFromTotalMeasures (int measures) {
        int groupIndex = 0;
        for (GroupData gd : this) {
            if (measures < gd.getMeasuresToEndofLine()) {
                if (groupIndex > 0) {
                    return (int) (get(groupIndex).getMeasuresToEndofLine() - get(groupIndex - 1).getMeasuresToEndofLine());
                }
                else {
                    break;
                }
            }
            groupIndex++;
        }
        return (get(0).getMeasuresToEndofLine());
    }

    public GroupData getGroupFromMeasure (int measure) {
        AutoScroll autoScroll = scrollActivity.getAutoScroll();
        for (GroupData gd : this) {
            if (measure < gd.getMeasuresToEndofLine()) {
                return gd;
            }
        }
        //// never here hopefully
        return (get(size()-1));
    }

    public int getMeasuresFromSongPos (int groupIndex, long songPos) {
        AutoScroll autoScroll = scrollActivity.getAutoScroll();
        //int groupIndex = getGroupIndexFromSongPos(songPos);
        int measuresToGroup = getStartOfLineMeasures (groupIndex);
        long timePerMeasure = autoScroll.getTimePerMeasure();
        long timeToStartofGroup = measuresToGroup * timePerMeasure;

        int measure = 1;
        while (timeToStartofGroup + measure * timePerMeasure < songPos) {
            measure++;
        }
        return measure;
    }

    public int getLastPageGroupIndex () {
        int totalLines = scrollActivity.getLinesPerPage();
        int lineCount = 0;
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
        }
        return 0;
    }

    public int getLastVisibleLineMeasure (int groupIndex) {
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
    }

    public int getGroupIndex(int measure) {
        int count = 0;
        for (GroupData gd : this) {
            if (measure < gd.getMeasuresToEndofLine()) {
                return count;
            }
            count++;
        }
        return (int) (size()-1);
    }

    public int getGroupIndex() {
        int count = 0;
        int measure = scrollActivity.getAutoScroll().getProgress();
        for (GroupData gd : this) {
            if (measure < gd.getMeasuresToEndofLine()) {
                return count;
            }
            count++;
        }
        return (int) (size()-1);
    }

    public int getLine(int measure) {
        int count = 0;
        for (GroupData gd : this) {
            if (measure < gd.getMeasuresToEndofLine()) {
                if (count > 0) {
                    //return (int) ((3 * count + get(count - 1).getWrappedLines()));
                    return (gd.getChordsLineNumber() + get(count - 1).getWrappedLines());
                }
                else {
                    //return (int) ((3 * count));
                    return (gd.getChordsLineNumber());
                }
            }
            count++;
        }
        //// never here hopefully
        return (int) (3 * count);
    }

    public int getScrollLineFromPos (int pos){
        int LinePos = (int) (pos/scrollActivity.getScrollView().getLineHeight());
        if (LinePos < size()) {
            return get(LinePos).getOffsetChords();
        }
        return -1;
    }
}
