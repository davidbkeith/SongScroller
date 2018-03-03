package com.mobileapps.brad.songscroller;

import android.graphics.Rect;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by brad on 2/26/18.
 */

public class GroupArray extends ArrayList<GroupData> {

    private ScrollActivity scrollActivity;

    public GroupData getCurrentGroup() {
        return currentGroup;
    }

    private GroupData currentGroup;

    public ScrollActivity getScrollActivity() {
        return scrollActivity;
    }

    public GroupArray (ScrollActivity scrollActivity) {
        this.scrollActivity = scrollActivity;
    }

    public String create (BufferedReader br, String scoreText, ScoreData scoreData) {
        int groupLineCount = 1;
        int measuresToEndofLine = 0;
        String line;
        GroupData groupData = null;

        try {
            while ((line = br.readLine()) != null) {
                //// line 1 is chords
                line = line.trim();

                if (groupLineCount == 1) {
                    groupData = new GroupData(scoreData);
                    groupData.setOffsetChords(scoreText.length());
                    groupData.setChordsLength(line.length());
                }
                //// line 2 is lyrics, annotations, etc
                else if (groupLineCount == 2) {
                    groupData.setLyricsLength(line.length());
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
            }
        }
        catch (Exception e) {
            Log.e("File Read Error", e.toString());
        }
        return scoreText;
    }

    public void sortGroups (int sortOrder) {
        if (sortOrder == 0) {
            Collections.sort(this, new GroupData.PositionCompare());
        }
        else {
            Collections.sort(this, new GroupData.LengthCompare());
        }
    }

    public boolean isChordLine (int position) {
        int i=0;
        while (i < size() && position < get(i++).getLyricsLength());
        return --i % 2 == 0;
    }

    public void setPriorWrappedLines () {
        TextView textView = scrollActivity.getTextView();
        int priorWrapped = 0;

        String text = textView.getText().toString();
        int linePos = scrollActivity.getPosOffset();

        for(GroupData gd: this) {
            gd.setPriorWrappedLines(priorWrapped);
            int groupwrapped = 0;
            for (int groupLine = 0; groupLine < 2; groupLine++) {
                int lineEnd = textView.getLayout().getLineEnd(linePos + groupLine);

                String lineText = text.substring(0, lineEnd);
                if (!lineText.endsWith("\n")) {
                    groupwrapped++;
                }
            }
            linePos += (3 + groupwrapped);
            priorWrapped += groupwrapped;
        }
    }

    public int getStartOfLineMeasures (int line) {
        int group = line/3;
        if (group > 0 && group < size()) {
            return get(group-1).getMeasuresToEndofLine() + 1;
        }
        return (0);
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

    public int getLineMeasures (int measure) {
        int count = 0;
        for (GroupData gd : this) {
            if (measure <= gd.getMeasuresToEndofLine()) {
                currentGroup = gd;
                int prevMeasures = 0;
                if (count > 0) {
                    prevMeasures = get(count - 1).getMeasuresToEndofLine();
                }
                return ( gd.getMeasuresToEndofLine() - prevMeasures);
            }
            count++;
        }
        //// never here hopefully
        return (0);
    }

    public int getScrollLine(int measure) {

        int count = 0;
        for (GroupData gd : this) {
            if (measure <= gd.getMeasuresToEndofLine()) {
                int prevMeasures = 0;
                if (count > 0) {
                    prevMeasures = get(count - 1).getMeasuresToEndofLine();
                }
                return (int) ((3 * count + gd.getPriorWrappedLines()));
                //return (int) ((3 * count));
            }
            count++;
        }
        //// never here hopefully
        return (int) (3 * count);
    }

    public int getScrollLineFromPos (int pos, float lineHeight){
        int LinePos = (int) (pos/lineHeight);
        if (LinePos < size()) {
            return get(LinePos).getOffsetChords();
        }
        return -1;
    }
}
