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
        ScoreData scoreData = scrollActivity.getAutoScroll().getScoreData();
        int totalChordCount = 0;

        if (size() > 0) {
            int chordline = 0;

            //// this array has all lines at this point, chords lines and non-chord lines, etc
            //// find real chord lines, eliminate others and save relevant data
            int chordStart = 0;
            for (int i = chordline; i < size(); i++) {
                GroupData gd = get(i);
                int chordlineLength = gd.getLyrics(score).length();
                int chordIndex;
                int numChords = 0;

                for (chordIndex = chordStart; chordIndex < chordPos.size(); chordIndex++) {
                    ChordData chordData = chordPos.get(chordIndex);
                    if (chordData.getStartPos() >= gd.getOffsetChords() && chordData.getStartPos() <= gd.getOffsetChords() + chordlineLength) {
                        /// has chords on this line
                        if (++numChords == 1) {
                            //// clean up - remove non-chord lines (all from last chord line to current chord line
                            for (int j = chordline; j < i; j++) {
                                remove(chordline);
                            }

                            if (chordIndex == chordStart) {
                                //// increment chord lines counter
                                i = chordline;
                                chordline++;
                            }
                        }
                    }
                    else {
                        //// no chords on line, go to next line
                        break;
                    }
                }
                /// go to next if chords finished
                if (numChords > 0) {
                    chordStart = chordIndex;
                    totalChordCount += numChords;
                }
            }

            //// remove what is left after last chord line
            int size = size();
            for (int i = chordline; i < size; i++) {
                remove(chordline);
            }
        }

        int beats = totalChordCount/this.size();

        //// guess 4 or 8 beats per line
        scoreData.setBeats (beats < 6 ? 4 : 8);
        createScoreData(scoreData, scrollActivity.getAutoScroll(), score);
    }

    public void createScoreData (ScoreData scoreData, AutoScroll autoScroll, String text) {
        //String text = autoScroll.getText();
        String next;
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
            scoreData.setBeats(Integer.parseInt(((String)map.get("beats"))));
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
        else {
            //// tempo = beats (per measure) * (number of beats/song duration in seconds) * 60
            int bpm = (int) (scoreData.getBeats() * getTotalBeats() * 60 / (scrollActivity.getSong().getDuration()/1000));
            scoreData.setBpm(bpm);
        }

    //    scoreData.setScrollOffset(3);
    //    autoScroll.setScoreData(scoreData);
     //   autoScroll.setBeatInterval(60000 / scoreData.getBpm());
        //setMax (getSongDuration());
        //return scoreData;
    }
}
