package com.mobileapps.brad.songscroller;

import android.text.SpannableStringBuilder;
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

    public ScrollActivity getScrollActivity() {
        return scrollActivity;
    }

    public void GroupArray () {}

    public GroupArray (ScrollActivity scrollActivity) {
        this.scrollActivity = scrollActivity;
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
                //// line 3 is line metadata (number of beats, time signature, etc)
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
            if (gd.getBeats() > 0) {
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
        if (get(groupIndex).getBeats() > 0) {
            for (int i=0; i<groupIndex; i++) {
                get(i).setBeats(0);
            }
            scrollActivity.getSong().setStartPosition(0);
            scrollActivity.setSpans();
        }
    }

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


    public void duplicateGroup (int group) {
        if (group == -1) {
            group = getCurrentGroup();
        }

        GroupData copyGroup = get(group);
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
        scrollActivity.duplicateText(chordPos, chordPos + groupLength);
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

    public void setGroupText (int group, String text) {
        if (group == -1) {
            group = getCurrentGroup();
        }

        int chordPos = get(group).getOffsetChords();
        int endPos = -1;

        if (group < size()-1) {
            endPos = get(group + 1).getOffsetChords();
            int groupLength = endPos - chordPos;
            int offset = groupLength - text.length();

            for (int i = group + 1; i < size(); i++) {
                get(i).setOffsetChords(get(i).getOffsetChords()-offset);
            }
        }

        scrollActivity.replaceText(chordPos, endPos, text);
    }

    public int getCurrentGroup () {
        if (ScrollActivity.isEditGroup()) {
            return scrollActivity.getAutoScroll().getProgress();
        }
        else {
            if (scrollActivity.getTextView().getLayout() != null) {
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
        }
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
        return 0;
    }

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
        return scrollActivity.getTextView().getLayout().getLineCount()-1;  /// not found
    }

    public int getScrollLineFromPos (int pos){
        int LinePos = (int) (pos/scrollActivity.getScrollView().getLineHeight());
        if (LinePos < size()) {
            return get(LinePos).getOffsetChords();
        }
        return -1;
    }

    public void updatePositions (int offsetChords, int offsetCharPos) {
        int count=0;
        while (get(count++).getOffsetChords() < offsetChords);
        count = count - 1 == -1 ? 0 : count - 1;

        for (int i=count; i<size(); i++) {
            get(i).setOffsetChords(get(i).getOffsetChords() + offsetCharPos);
        }
    }

    public String getText (int group, String text) {
        int start = get(group).getOffsetChords();
        return group < size()-1 ? text.substring(start, get(group+1).getOffsetChords()) : text.substring(start);
    }
}
