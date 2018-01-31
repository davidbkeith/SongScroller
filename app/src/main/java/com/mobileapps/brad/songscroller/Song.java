package com.mobileapps.brad.songscroller;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by brad on 1/23/18.
 */

public class Song implements Serializable {
    private String title;
    private String dispayName;
    private String art;
    private String artist;
    private String path;
    private long duration;
    private int position;
    private int albumId;
    private String track;

    public Song() {
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getTitle() {

        return title;
    }

    public String getArt() {
        return art;
    }

    public void setArt(String art) {
        this.art = art;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDispayName() {
        return dispayName;
    }

    public void setDispayName(String dispayName) {
        this.dispayName = dispayName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }
//@Override
    //public int compareTo (Song music) {
    //    return this.artist.compareToIgnoreCase(music.artist);
    //}



}
