package com.mobileapps.brad.songscroller;


import android.content.Context;
import android.util.AttributeSet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Created by brad on 3/2/18.
 */

public class AutoScrollGuess extends AutoScroll implements android.widget.SeekBar.OnSeekBarChangeListener{

    public AutoScrollGuess(Context context, AutoScroll autoScroll) {
        super(context);
        this.setScoreData(autoScroll.scoreData);
        this.text = autoScroll.text;
        this.scrollActivity = (ScrollActivity) context;
    }

    public AutoScrollGuess(Context context) {
        super(context);
        this.scrollActivity = (ScrollActivity) context;
    }

    public AutoScrollGuess(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoScrollGuess(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void initialize (ScrollActivity scrollActivity, AutoScroll autoScroll) {

        this.scrollActivity = scrollActivity;
        text = autoScroll.getText();
        scoreData = new ScoreData();
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

        ///////////// bpm
        if (map.get("bpm") != null) {
            scoreData.setBpm(Integer.parseInt(((String)map.get("bpm"))));
            autoScroll.setBeatInterval((int) (60000 / scoreData.getBpm()));
        }

        ///////////// bpm
        if (map.get("duration") != null) {
            String[] timeParts = ((String)map.get("duration")).split(":");
            if (timeParts.length > 1) {
                int seconds = 60 * Integer.parseInt(timeParts[0].trim()) + Integer.parseInt(timeParts[1]);
                scrollActivity.getSong().setDuration(1000 * seconds);
            }
            else {
                scrollActivity.getSong().setDuration(Long.parseLong(((String)map.get("duration"))));
            }
        }

        ///////////// beats
        if (map.get("beats") != null) {
            scoreData.setBeats(Integer.parseInt(((String)map.get("beats"))));
        }

        autoScroll.setScoreData(scoreData);
        setMax (getSongDuration());
    }

    @Override
    public void onScrollChanged(ScrollViewExt scrollView, int x, int y, int oldx, int oldy) {
        if ( ScrollActivity.mediaPlayer == null || !ScrollActivity.mediaPlayer.isPlaying()) {
            posOffset = y;
        }
        else {
            int mediaPos_new = ScrollActivity.mediaPlayer.getCurrentPosition();

            if (posOffset == posOffset || scrollActivity.getIvMute().isActivated()) {
                ///////// how it is calulated for moving scroller
                //scrollY = calculatedPos + posOffet
                //posOffest = scrollY - calculatedPos;
                ///////// this code allows user to shift position of vertical scroll while mp3 is playing
                //posOffset = (y - ((int) (((double) (mediaPos_new - scrollActivity.getPause()) / scrollActivity.getSong().getDuration()) * scrollActivity.getTextVeiwHeight())));
                posOffset = (y - ((int) (((double) (getProgress() - scrollActivity.getPause()) / scrollActivity.getSong().getDuration()) * scrollActivity.getTextVeiwHeight())));
            } else {
                ///////// how it is calulated for new song seek position
                //scrollY = calculatedPos + posOffet (from that equation)
                ///////// this code sets the mp3 current position to match the vertical scroll position
                scrollActivity.setNewSeek((int) ((scrollActivity.getSong().getDuration() * (y - (posOffset)) / (double) scrollActivity.getTextVeiwHeight()) + scrollActivity.getPause()));
            }
        }
    }

    @Override
    public int getSongDuration () {
        if (ScrollActivity.mediaPlayer != null) {
            return (int) (ScrollActivity.mediaPlayer.getDuration());
        }
        return (int) scrollActivity.getSong().getDuration();
    }

    @Override
    public boolean isChordLine (int position) {return true;}

    @Override
    public int getScrollLine() {
        return getProgress();
    }

    @Override
    public void setSeekBarProgress() {
        long elpasedTime = scrollActivity.getSong().getPosition();
        setProgress((int) (elpasedTime));
        //Toast.makeText(scrollActivity, String.format("%d",getProgressMeasures()), Toast.LENGTH_SHORT).show();
    }
}
