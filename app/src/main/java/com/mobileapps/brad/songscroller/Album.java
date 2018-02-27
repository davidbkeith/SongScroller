package com.mobileapps.brad.songscroller;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brad on 1/29/18.
 */

public class Album implements Serializable {

    private String album;
    private String art;
    private String artist;
    private long id;
    private String numberSongs;

    /// constructors
    public Album(Album album) {
        this.id = album.id;
        this.album = album.album;
        this.art = album.art;
        this.artist = album.artist;
        this.numberSongs = album.numberSongs;
    }

    public Album(long id, String album, String art, String artist, String numberSongs) {
        this.id = id;
        this.album = album;
        this.art = art;
        this.artist = artist;
        this.numberSongs = numberSongs;
    }

    public Album (Context context, long albumid) {
        ArrayList<Album> albumArrayList = getAlbumById(context, albumid, MediaStore.Audio.AlbumColumns.ALBUM);
        this.equals(albumArrayList.get(0));
    }

    //// getters and setters
    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNumberSongs() {
        return numberSongs;
    }

    public void setNumberSongs(String numberSongs) {
        this.numberSongs = numberSongs;
    }


    /// global functions
    static public ArrayList<Album> getAlbumById (Context context, long albumid, String sortBy) {

        File dirmusic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        ArrayList albumList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        MediaStore.Audio.Albums albums = new MediaStore.Audio.Albums();
        Uri songUri = albums.EXTERNAL_CONTENT_URI;
        String selection = "is_music != 0";
        if (albumid > 0) {
            selection += " and album_id = " + Long.toString(albumid);
        }

        String[] projection = new String[] {albums._ID, albums.ALBUM, albums.ARTIST, albums.ALBUM_ART, albums.NUMBER_OF_SONGS};
        String sortOrder;
        if (MediaStore.Audio.AlbumColumns.ALBUM.compareTo(sortBy) == 0) {
            sortOrder = MediaStore.Audio.AlbumColumns.ALBUM + " COLLATE LOCALIZED ASC";
        }
        else {
            sortOrder = MediaStore.Audio.AlbumColumns.ARTIST + " COLLATE LOCALIZED ASC";
        }

        Cursor songCursor = contentResolver.query(songUri, projection,null,null, sortOrder);

        if (songCursor != null && songCursor.moveToFirst()) {
            int songAlbumId = songCursor.getColumnIndex(albums._ID);
            int songAlbum = songCursor.getColumnIndex(albums.ALBUM);
            int songArtist = songCursor.getColumnIndex(albums.ARTIST);
            int songArt = songCursor.getColumnIndex(albums.ALBUM_ART);
            int songNumberOfSongs = songCursor.getColumnIndex(albums.NUMBER_OF_SONGS);

            do {
                long id = songCursor.getLong(songAlbumId);
                String album = songCursor.getString(songAlbum);
                String artist = songCursor.getString(songArtist);
                String art = songCursor.getString(songArt);
                String numberSongs = songCursor.getString(songNumberOfSongs);

                ///// code to find album cover - not recommended to use
                //if (art == null || art.isEmpty()) {
                   /* List<String> arrPattern = new ArrayList<>();
                    arrPattern.add (artist);
                    arrPattern.add (album);

                    File audioDir = FindFile.findFileWithExt(dirmusic, arrPattern, ".jpg");
                    if (audioDir.exists()) {
                        art = audioDir.getAbsolutePath();
                    }*/
                    //Log.d("Album Directory", audioDir.getAbsolutePath());
               // }

                albumList.add(new Album (id, album, art, artist, numberSongs));

            } while (songCursor.moveToNext());
        }
        return albumList;
    }
}
