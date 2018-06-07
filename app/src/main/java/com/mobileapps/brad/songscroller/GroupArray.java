package com.mobileapps.brad.songscroller;

import android.text.Layout;
import android.util.Log;
import android.view.Gravity;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by brad on 2/26/18.
 */

public class GroupArray extends ArrayList<GroupData> {

    protected ScrollActivity scrollActivity;

    public boolean isSetPositions() {
        return setPositions;
    }

    public boolean setPositions;

    public ScrollActivity getScrollActivity() {
        return scrollActivity;
    }

    public GroupArray () {
        setPositions = false;
    }

    public GroupArray (ScrollActivity scrollActivity, String datafile) {
        this.scrollActivity = scrollActivity;
        setPositions = true;  /// used to convert line positions to character positions after view is built

        String[] data = datafile.split(",");
        int first = 5;
        switch (AutoScroll.scoreData.getVersion()) {
            default:
                first = 5;
        }

        for (int i=first; i<data.length; i++) {
            String[] items = data[i].split("\\.");
            int measures = Integer.parseInt(items[1]);
            GroupData groupData = new GroupData(Integer.parseInt(items[0]), measures);
            add(groupData);
        }
    }

    public GroupArray (ScrollActivity scrollActivity) {
        this.scrollActivity = scrollActivity;
        setPositions = false;
    }

    public void create (List<ChordData> chordPos, String scrore) {}

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

    public String getSerializedData () {
        String retval = "";
        Layout layout = scrollActivity.getTextView().getLayout();
        for (GroupData gd: this) {
            retval += (String.format("%d.%d,", layout.getLineForOffset(gd.getOffsetChords()), gd.getMeasures()));
        }
        return retval;
    }

   /* public void reset () {
        for (GroupData gd: this) {
            gd.setMeasures();
        }
    }*/

    public void setScoreData (AutoScroll autoScroll) {};

    public String create (BufferedReader br, String scoreText, ScoreData scoreData) {
        int groupLineCount = 1;
        String line;
        GroupData groupData = null;

        try {
            while ((line = br.readLine()) != null) {
                //// line 1 is chords
                line = line.trim();

                if (groupLineCount == 1) {
                    groupData = new GroupData();
                    groupData.setOffsetChords(scoreText.length());
                }
                //// line 3 is line metadata (number of measures, time signature, etc)
                else if (groupLineCount == 3)  {
                    try {
                        if (line.trim().length() > 0) {
                            groupData.getLineMetaData(line);
                        }

                        add(groupData);
                        line = "";
                    } catch (Exception e) {
                        Log.e("JSON Parsing Error:", e.toString());
                    }
                }
                scoreText += line + "\n";
                if (++groupLineCount > 3) {
                    groupLineCount = 1;
                }
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

    /*public int getStartLineMeasures(int measures) {
        //int measures = getProgress();
        return getStartLineMeasuresFromTotalMeasures(measures);
    }*/

    public int getScrollLine(int measures) {
       return getLine(measures) - AutoScroll.scoreData.getScrollOffset();
    }

    public boolean isChordLine (int charPosition, String score) {
        int i=0;
        while (i < size() && charPosition >= get(i).getOffsetChords()){i++;}
        i--;
        return charPosition >= get(i).getOffsetChords() && charPosition < get(i).getOffsetChords() + get(i).getLyrics(score).length();
    }

    public boolean isSongStart (int groupIndex) {
       // int currentGroup = getCurrentGroup();
        int group = 0;
        for (GroupData gd: this) {
            if (gd.getMeasures() > 0) {
                break;
            }
            group++;
        }
        if (group < size()) {
            return get(groupIndex).equals(get(group));
        }
        return get(groupIndex).equals(get(size()-1));
    }

    public void setSongStart (int groupIndex) {
        if (get(groupIndex).getMeasures() > 0) {
            for (int i=0; i<groupIndex; i++) {
                get(i).setMeasures(0);
            }
            scrollActivity.getSong().setStartPosition(0);
            scrollActivity.setView();
        }
    }

    public void setPositions () {
        Layout layout = scrollActivity.getTextView().getLayout();

        for (GroupData gd :this) {
            gd.offsetChords = layout.getLineStart(gd.getOffsetChords());
        }
        setPositions = false;
    }

    public int getTotalMeasures() {
        int count = 0;
        for (GroupData gd :this) {
            count += gd.getMeasures();
        }
        return count;
    }

    public int getStartLineMeasuresFromTotalMeasures(int measures) {
        int groupIndex = 0;
        int count = 0;
        if (size() > 1) {
            for (GroupData gd : this) {
                count += gd.getMeasures();
                if (measures < count) {
                    if (groupIndex > 0) {
                        return (int) (count - get(groupIndex).getMeasures());
                    } else {
                        return 0;
                    }
                }
                groupIndex++;
            }
            return (count - get(size() - 1).getMeasures());
        }
        return 0;
    }

    public int getMeasuresToStartOfLine(int index) {
        index = index <= size() -1 ? index : size() - 1;
        int count = 0;
        for (int i = 0; i<index; i++) {
            count += get(i).getMeasures();
        }
        return (count);
    }

    public int getMeasuresToEndOfLine(int index) {
        index = index <= size() -1 ? index : size() - 1;
        int count = 0;
        for (int i = 0; i<=index; i++) {
            count += get(i).getMeasures();
        }
        return (count);
    }

    public int getLineMeasuresFromTotalMeasures(int measures) {
        int groupIndex = 0;
        int count = 0;
        for (GroupData gd : this) {
            count += gd.getMeasures();
            if (measures < count) {
                return (int) (get(groupIndex).getMeasures());
            }
            groupIndex++;
        }
        return (get(size()-1).getMeasures());
    }

    public GroupData getGroupFromMeasures(int measures) {
        int count = 0;
        for (GroupData gd : this) {
            count += gd.getMeasures();
            if (measures < count) {
                return gd;
            }
        }
        //// never here hopefully
        return (get(size()-1));
    }


    public void duplicateGroup (int group) {
        if (group == -1) {
            group = getCurrentGroup();
        }

        GroupData copyGroup = new GroupData(get(group));
        int chordPos = get(group).getOffsetChords();
        int endPos = -1;
        int groupLength;
        if (group < size()-1) {
            endPos = get(group + 1).getOffsetChords();
            groupLength = endPos - chordPos;

            for (int i = group; i < size(); i++) {
                get(i).setOffsetChords(get(i).getOffsetChords()+groupLength);
            }
        }
        else {
            groupLength = scrollActivity.getSb().length() - chordPos;
            get(size()-1).setOffsetChords(groupLength);
        }

        //GroupData newGroup = get(group);
        copyGroup.setOffsetChords(chordPos);
        add (group, copyGroup);
        scrollActivity.duplicateText(chordPos, chordPos + groupLength, group);
    }


    public void deleteGroup (int group) {
        if (group == -1) {
            group = getCurrentGroup();
        }
        int chordPos = get(group).getOffsetChords();
        int endPos = -1;

        if (group < size()-1) {
            endPos = get(group + 1).getOffsetChords();
            int groupLength = endPos - chordPos;
            for (int i = group + 1; i < size(); i++) {
                get(i).setOffsetChords(get(i).getOffsetChords()-groupLength);
            }
        }

        remove(group);
        scrollActivity.removeText(chordPos, endPos);
    }

    ////// must be in line edit or group edit mode to use this function
    public void setGroupText (int group, String text) {
        if (!scrollActivity.isEditText()) {
            //throw (new Throwable("Wrong mode!"));
        }
        else {
            int chordPos;
            int endPos = -1;
            int groupLength = 0;

            if (group == -1) {
                group = getCurrentGroup();
            }

            //// edit line
            if (group == -1) {
                AutoScroll autoScroll = scrollActivity.getAutoScroll();
                Layout layout = scrollActivity.getTextView().getLayout();
                chordPos = layout.getLineStart(autoScroll.getProgress());
                endPos = scrollActivity.getSb().toString().indexOf("\n", chordPos);
                groupLength = endPos - chordPos;

            }
            //// edit group
            else {
                chordPos = get(group).getOffsetChords();
                if (group < size() - 1) {
                    endPos = get(group + 1).getOffsetChords();
                    groupLength = endPos - chordPos;
                    //offset = groupLength - text.length();
                 }
            }

            int offset = groupLength - text.length();
            for (int i = group + 1; i < size(); i++) {
                get(i).setOffsetChords(get(i).getOffsetChords() - offset);
            }

            scrollActivity.replaceText(chordPos, endPos, text);
        }
    }

    public int getMeasures(int group) {
        int groupIndex = group == -1 ? getCurrentGroup() == -1 ? 0 : getCurrentGroup() : group;
        return get(groupIndex).getMeasures();
    }

    public int getCurrentGroup () {
        if (ScrollActivity.isEditText()) {
            return scrollActivity.getAutoScroll().getGroupArray().getGroupFromLine(scrollActivity.getAutoScroll().getProgress());
        } else {
            //if (scrollActivity.getTextView().getLayout() != null) {
            //    int currentScrollPos = scrollActivity.getScrollView().getChordsPos();
            int measures = scrollActivity.getAutoScroll().getProgress();
            //int measures = scrollActivity.getSong().getMeasure();
            //    if (currentScrollPos != -1) {
            int group = 0;
            int sum = 0;
            for (GroupData gd : this) {
                //if (group < size() - 1) {
                sum += gd.getMeasures();
                if (measures < sum) {
                    return group;
                }
                // } else {
                //     if (currentScrollPos >= gd.getOffsetChords()) {
                //return group;
                //     }
                // }
                group++;
                // }
                //   }
                //}
            }
            return size()-1;
        }
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
        return 0;
    }

    public int getGroupOrMakeChordLine (int line) {
        if (scrollActivity.getTextView().getLayout() != null) {
            int lineStartPos = scrollActivity.getTextView().getLayout().getLineStart(line);

            if (lineStartPos >= get(0).getOffsetChords()) {
                int group = 0;
                for (GroupData gd : this) {
                    if (lineStartPos <= gd.getOffsetChords()) {
                        if (lineStartPos == gd.getOffsetChords()) {
                            return group;
                        }
                        else {
                            this.add(group, new GroupData(lineStartPos, 0));
                            return group;
                        }
                    }
                    group++;
                }
            }
        }
        return -1;
    }

    public int getGroupIfChordLine (int line) {
        if (scrollActivity.getTextView().getLayout() != null) {
            int lineStartPos = scrollActivity.getTextView().getLayout().getLineStart(line);

            if (lineStartPos >= get(0).getOffsetChords()) {
                int group = 0;
                for (GroupData gd : this) {
                    if (lineStartPos <= gd.getOffsetChords()) {
                        if (lineStartPos == gd.getOffsetChords()) {
                            return group;
                        }
                        else {
                            return -1;
                        }
                    }
                    group++;
                }
            }
        }
        return -1;
    }

    public int getGroupFromLine (int line) {
        if (scrollActivity.getTextView().getLayout() != null) {
            int lineStartPos = scrollActivity.getTextView().getLayout().getLineStart(line);

            if (lineStartPos >= get(0).getOffsetChords()) {
                int group = 0;
                for (GroupData gd : this) {
                    if (lineStartPos <= gd.getOffsetChords()) {
                        return group;

                    }
                    group++;
                }
            }
        }
        return -1;
    }

    public int getGroupIndex(int measure) {
        int count = 0;
        int sum = 0;
        for (GroupData gd : this) {
            sum += gd.getMeasures();
            if (measure < sum) {
                return count;
            }
            count++;
        }
        return (int) (size()-1);
    }

    public int getGroupIndex() {
        int count = 0;
        int sum = 0;
        int measure = scrollActivity.getAutoScroll().getProgress();
        for (GroupData gd : this) {
            sum += gd.getMeasures();
            if (measure < sum) {
                return count;
            }
            count++;
        }
        return (int) (size()-1);
    }

    public int getLine(int measures) {
        int sum = 0;
        for (GroupData gd : this) {
            sum += gd.getMeasures();
            if (measures < sum) {
                return scrollActivity.getTextView().getLayout().getLineForOffset(gd.getOffsetChords());
            }
        }
        return scrollActivity.getTextView().getLayout().getLineCount()-1;  /// not found
    }

    public int getLineFromGroup(int group) {
        return scrollActivity.getTextView().getLayout().getLineForOffset(get(group).getOffsetChords());
    }

    /*public int getScrollLineFromPos (int pos){
        int LinePos = (int) (pos/scrollActivity.getScrollView().getLineHeight());
        if (LinePos < size()) {
            return get(LinePos).getOffsetChords();
        }
        return -1;
    }*/

    public void updatePositions (int lineStartPos, int offset) {
        for (int i=0; i<size(); i++) {
            if (get(i).getOffsetChords() > lineStartPos) {
                get(i).setOffsetChords(get(i).getOffsetChords() + offset);
            }
        }
    }

    public String getText (int group, String text) {
        int start = get(group).getOffsetChords();
        return group < size()-1 ? text.substring(start, get(group+1).getOffsetChords()) : text.substring(start);
    }
}
