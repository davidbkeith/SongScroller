package com.mobileapps.brad.songscroller;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Pattern;

/**
 * Created by brad on 1/23/18.
 */

public class Song implements Serializable {
    private String title;
    private String dispayName;
    private String art;
    private String artist;
    private String lyrics;
    private String path;
    private String sheetMusicPath;
    private long duration;
    private long startTime;
    private boolean isPlaying;

    public long getStartPosition() {
        return startPosition;
    }
    private long startPosition;

    public boolean isPlaying() {
        return isPlaying;
    }

    private int albumId;
    private String track;

    public  long getPosition () {
        if (isPlaying) {
            return System.currentTimeMillis() - startTime + startPosition;
        }
        else {
            return startPosition;
        }
    }

    public void setStartPosition(long startPosition) {
        this.startPosition = startPosition;
        startTime = System.currentTimeMillis();

        if (ScrollActivity.mediaPlayer != null) {
            ScrollActivity.mediaPlayer.seekTo((int) startPosition);
        }
    }

    public Song () {}

    public void pause() {
        //startPosition = System.currentTimeMillis() - startTime;
        if (ScrollActivity.mediaPlayer != null) {
            ScrollActivity.mediaPlayer.pause();
        }
        isPlaying = false;
    }

    public void start() {
        startTime = System.currentTimeMillis();

        if (ScrollActivity.mediaPlayer != null) {
            ScrollActivity.mediaPlayer.start();
        }
        isPlaying = true;
    }

    public boolean equals(Object object2) {
        return object2 instanceof Song && sheetMusicPath != null && sheetMusicPath.equals(((Song)object2).sheetMusicPath);
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getTrack() {
        int intTrack = Integer.parseInt(track);
        if (intTrack > 999) intTrack -= 1000;

        return (String.format("%d",intTrack));
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

    public String getSheetMusicPath() {
        return sheetMusicPath;
    }

    public void setSheetMusicPath(String sheetMusicPath) {
        this.sheetMusicPath = sheetMusicPath;
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

    static public ArrayList<Song> getScores (Context context, Album album, String SortOrder) {
        File dirmusic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        File[] files = FindFile.findFilesWithExt(dirmusic, "txt");
        ArrayList<Song> songs = new ArrayList<>();

        for (int i=0; i<files.length; i++) {
            Song song = new Song ();
            String name = files[i].getName();
            String[] parts = name.split("-");
            if (parts.length > 1) {
                song.setArtist (parts[1]);
                song.setTitle(parts[0]);
            }
            else {
                song.setTitle(parts[0]);
            }
            song.setSheetMusicPath(files[i].getAbsolutePath());
            songs.add(song);
        }
        return songs;
    }

    static public ArrayList<Song> getSongs (Context context, Album album, String SortOrder) {
       // MainActivity mainActivity = (MainActivity) context;
        ArrayList<Song> songs = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        MediaStore.Audio.Media media = new MediaStore.Audio.Media();
        String selection = "is_music != 0";
        Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
        long albumId = album == null ? 0 : album.getId();

        if (albumId > 0) {
            selection = selection + " and album_id = " + albumId;
        }

        String[] projection = new String[]{
                media.ARTIST,
                media.TITLE,
                media.DATA,
                media.DISPLAY_NAME,
                media.DURATION,
                media.ALBUM_ID,
                media.TRACK
        };

        File dirmusic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        //Uri songUri = media.EXTERNAL_CONTENT_URI;
        String sortOrder = SortOrder + " COLLATE LOCALIZED ASC";
        Cursor songCursor = null;

        File sdcard = Environment.getExternalStorageDirectory();
        try {
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            songCursor = contentResolver.query(uri, projection, selection, null, sortOrder);
            if (songCursor != null) {
                songCursor.moveToFirst();
                //int position = 1;

                while (!songCursor.isAfterLast()) {
                    Song song = new Song();
                    song.setArtist(songCursor.getString(0));
                    song.setTitle(songCursor.getString(1));
                    //File file = new File(sdcard, "/Music/" + song.getArtist() + "-" + song.getTitle() + ".txt");
                    File score = FindFile.find(song.getArtist() + "-" + song.getTitle() + ".txt", dirmusic, false );
                    Log.d("Score Path is: ", score.getPath());
                    if (score.exists()){
                        song.setSheetMusicPath(score.getPath());
                    }
/*                    else if (mainActivity.getmView() == MainActivity.SCORE )  {
                        continue;
                    }*/
                    song.setPath(songCursor.getString(2));
                    song.setDispayName((songCursor.getString(3)));
                    song.setDuration(songCursor.getLong(4));
                    song.setAlbumId(songCursor.getInt(5));
                    song.setTrack(songCursor.getString(6));
                    //song.setPosition(position);

                    if (album != null && album.getArt() == null) {
                        //// to delete existing artwork!!!!!!!
                        //int deleted = context.getContentResolver ().delete (ContentUris.withAppendedId(artworkUri, song.getAlbumId()), null, null);
                        File albumFile = new File(song.getPath());
                        File artFile = FindFile.findFileWithExt(albumFile.getParentFile(), null, ".jpg");

                        if (artFile.exists()) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("album_id", song.getAlbumId());
                            contentValues.put("_data", artFile.getAbsolutePath());
                            context.getContentResolver().insert (artworkUri, contentValues);
                            song.setArt(artFile.getAbsolutePath());
                            album.setArt(artFile.getAbsolutePath());
                        }
                    }

                    songs.add(song);
                    songCursor.moveToNext();

                    ////// no album in particular must find album art (works but too slow)

                    /*if (AlbumId == 0) {
                        Cursor cursor = contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                                new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                                MediaStore.Audio.Albums._ID + "=?",
                                new String[]{String.valueOf(song.getAlbumId())},
                                null);

                        if (cursor != null && cursor.moveToFirst()) {
                            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                           // Log.d("Art Path.....", path);
                            // do whatever you need to do
                            if (path != null) {
                                song.setArt(path);
                            }
                            cursor.close();
                        }

                    }*/
                }
                songCursor.close();

            }
        } catch (Exception e) {
            Log.e("Media", e.toString());
        } finally {
            if (songCursor != null) {
                songCursor.close();
            }
        }
        return songs;
    }
}
