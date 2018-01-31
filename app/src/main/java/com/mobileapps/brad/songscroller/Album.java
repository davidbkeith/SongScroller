package com.mobileapps.brad.songscroller;

import java.io.Serializable;

/**
 * Created by brad on 1/29/18.
 */

public class Album implements Serializable {
    private long id;
    private String album;
    private String art;
    private String artist;
    private String numberSongs;

    public Album(long id, String album, String art, String artist, String numberSongs) {
        this.id = id;
        this.album = album;
        this.art = art;
        this.artist = artist;
        this.numberSongs = numberSongs;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getArt() {
        return art;
    }

    public void setArt(String art) {
        this.art = art;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getNumberSongs() {
        return numberSongs;
    }

    public void setNumberSongs(String numberSongs) {
        this.numberSongs = numberSongs;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
}
