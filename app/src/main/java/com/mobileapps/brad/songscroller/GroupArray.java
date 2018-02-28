package com.mobileapps.brad.songscroller;

import android.util.Log;

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

    public GroupArray () {}

    public String create (BufferedReader br, String scoreText, ScoreData scoreData) {
        int lineCount = 1;
        int measuresToEndofLine = 0;
        String line;
        GroupData groupData = null;

        try {
            while ((line = br.readLine()) != null) {
                //// line 1 is chords

                if (lineCount == 1) {
                    groupData = new GroupData(scoreData);
                    groupData.setOffsetChords(scoreText.length());
                    groupData.setChordsLength(line.length());
                }
                //// line 2 is lyrics, annotations, etc
                else if (lineCount == 2) {
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
                if (++lineCount > 3) {
                    lineCount = 1;
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

    public void setPriorWrappedLines (int numWrapped) {
        List<Integer> arrlengths = new ArrayList<Integer> ();

        for (GroupData gd : this) {
            arrlengths.add(gd.getLyricsLength());
            arrlengths.add(gd.getChordsLength());
        }

        if (arrlengths.size() > numWrapped) {
            Collections.sort(arrlengths);
            Integer minWrapLength = (Integer) arrlengths.toArray()[arrlengths.size() - numWrapped];

            int priorWrapped = 0;
            for (GroupData gd : this) {
                if (gd.getChordsLength() >= minWrapLength || gd.getLyricsLength() >= minWrapLength) {
                    gd.setPriorWrappedLines(priorWrapped);
                    if (gd.getChordsLength() >= minWrapLength) {
                        ++priorWrapped;
                    }
                    if (gd.getLyricsLength() >= minWrapLength) {
                        ++priorWrapped;
                    }
                } else {
                    gd.setPriorWrappedLines(priorWrapped);
                }
            }
        }
    }

    public int getLineMeasures (int measure) {
        int count = 0;
        for (GroupData gd : this) {
            if (measure <= gd.getMeasuresToEndofLine()) {
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
