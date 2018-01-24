package com.mobileapps.brad.songscroller;

import java.util.Comparator;

/**
 * Created by brad on 1/23/18.
 */

public class Music implements Comparable<Music>{
    private String name;
    private String singer;
    private String song;

    public Music(String name, String singer, String song) {
        this.name = name;
        this.singer = singer;
        this.song = song;
    }

    @Override
    public int compareTo (Music music) {
        return this.singer.compareToIgnoreCase(music.singer);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }
}
