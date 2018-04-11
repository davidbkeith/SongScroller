package com.mobileapps.brad.songscroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Created by brad on 3/26/18.
 */

public class GroupArrayGuess extends GroupArray {


    public GroupArrayGuess (ScrollActivity scrollActivity, GroupArray groupArray) {
        super(scrollActivity);
        this.addAll(groupArray);
    }

    @Override
    public void create (List<ChordData> chordPos, String score) {
       // measuresPerChord = 1;
      //  ScoreData scoreData = new ScoreData(1, 120, 1, 3);
        ScoreData scoreData = scrollActivity.getAutoScroll().getScoreData();

        if (size() > 0) {
            int chordline = 0;
            int chordlineOffset = 0;
            int linesRemoved = 0;

            //// this array has all lines at this point, chords lines and non-chord lines, etc
            //// find real chord lines, eliminate others and save relevant data
            int chordStart = 0;
            for (int i = chordline; i < size(); i++) {
                GroupData gd = get(i);
                int chordlineLength = gd.getLyrics(score).length();
                //for (ChordData chordData : chordPos) {
                int chordIndex;
                List chords = new ArrayList();
                for (chordIndex = chordStart; chordIndex < chordPos.size(); chordIndex++) {
                    ChordData chordData = chordPos.get(chordIndex);
                    if (chordData.getStartPos() >= gd.getOffsetChords() && chordData.getStartPos() <= gd.getOffsetChords() + chordlineLength) {
                        /// has chords on this line

                        //// only add chords that are separated by 1 or more characters
                        //if (chordData.getStartPos() != lastChord + 1 && chordData.getStartPos() != nextChord - 1) {
                            chords.add(chordData.getStartPos() - gd.getOffsetChords());
                            chords.add(chordData.getChord().length());
                       // }

                        if (chords.size() == 2) {

                            //// clean up - remove non-chord lines (all from last chord line to current chord line
                            for (int j = chordline; j < i; j++) {
                                remove(chordline);
                            }

                            linesRemoved = (i - chordline);

                            /// set number of lines in a chord group (lines removed earlier)
                            if (chordline > 0) {
                                //get(chordline - 1).setGroupLineCount(linesRemoved + 1);
                            } else {
                                //// this is first chord line so happens once only
                                chordlineOffset = linesRemoved;
                                //scoreData.setSongStartLine(linesRemoved);
                                //scoreData.setScrollStart(linesRemoved);
                            }

                            /// set chords line numbers so first chord line is 1
                            //gd.setChordsLineNumber(gd.getChordsLineNumber() - chordlineOffset);

                            if (chordIndex == chordStart) {
                                //// increment chord lines counter
                                i = chordline;
                                chordline++;
                            }
                        }
                        //break;

                    }
                    else {
                        //// no chords on line, go to next line
                        break;
                    }
                }
                /// no chords on this line, go to next
                if (chords.size() > 0) {
                    //gd.setChords(chords);
                    chordStart = chordIndex;
                }
            }


            //// remove what is left after last chord line
            int size = size();
            for (int i = chordline; i < size; i++) {
                remove(chordline);
            }

            /// set number of lines in a chord group (lines removed earlier)
            //get(size()-1).setGroupLineCount(size-chordline+1);
        }

        /// set guess beats
        //int count = 0;
       /* int index = 0;
        for ( int i=0; i<size(); i++) {
            GroupData gd = get(i);
            int count = (gd.chords.length/2) * measuresPerChord;
            gd.setBeats(count);
        }*/
        createScoreData(scoreData, scrollActivity.getAutoScroll());
    }

    public void createScoreData (ScoreData scoreData, AutoScroll autoScroll) {

        //this.scrollActivity = scrollActivity;
        String text = autoScroll.getText();
        //groupArray = autoScroll.getGroupArray();
        String next;
        String Metadata = "";
        String[] scoredataKey = {"title", "artist", "genre", "bpm","duration", "beats", "pause", "mp3"};
        Map map = new HashMap();

        for (int i=0; i<scoredataKey.length; i++) {
            Matcher matcher = java.util.regex.Pattern.compile("@!".concat(scoredataKey[i]).concat(".+?\\n")).matcher(text);
            if (matcher.find()) {
                next = matcher.group(0);
                String[] data = next.split(scoredataKey[i]);
                if (data.length == 2) {
                    map.put (scoredataKey[i], data[1].trim());
                }
                //Metadata = Metadata.concat(next);
                String[] parts = text.split(next);
                if (parts.length > 1) {
                    text = parts[0].concat(parts[1]);
                }
                else {
                    text = parts[0];
                }
            }
        }

        ///////////// beats per measure (default 4/4 time)
        if (map.get("beats") != null) {
            scoreData.setBeatsPerMeasure(Integer.parseInt(((String)map.get("beats"))));
        }

        ///////////// duration (from mp3 if available)
        /*if (map.get("duration") != null) {
            String[] timeParts = ((String)map.get("duration")).split(":");
            if (timeParts.length > 1) {
                int seconds = 60 * Integer.parseInt(timeParts[0].trim()) + Integer.parseInt(timeParts[1]);
                scrollActivity.getSong().setDuration(1000 * seconds);
            }
            else {
                scrollActivity.getSong().setDuration(Long.parseLong(((String)map.get("duration"))));
            }
        }*/

        ///////////// bpm
        if (map.get("bpm") != null) {
            scoreData.setBpm(Integer.parseInt(((String)map.get("bpm"))));
           // autoScroll.setBeatInterval((int) (60000 / scoreData.getBpm()));
        }
       // else {
            //// tempo = beats (per measure) * (number of beats/song duration in seconds) * 60
       //     int bpm = (int) (scoreData.getBeatsPerMeasure() * getTotalBeats() * 60 / (scrollActivity.getSong().getDuration()/1000));
       //     scoreData.setBpm(bpm);
       // }

    //    scoreData.setScrollOffset(3);
    //    autoScroll.setScoreData(scoreData);
     //   autoScroll.setBeatInterval(60000 / scoreData.getBpm());
        //setMax (getSongDuration());
        //return scoreData;
    }
}
