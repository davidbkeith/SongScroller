package com.mobileapps.brad.songscroller;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by brad on 1/29/18.
 */

public class Album implements Serializable {

    private long id;
    private String album;
    private String art;
    private String artist;
    private String numberSongs;

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
        ArrayList<Album> albumArrayList = getAlbumById(context, albumid, MainActivity.ALBUM);
        this.equals(albumArrayList.get(0));
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

   // public ArrayList<Song> getSongs() {
   //     return songs;
   // }

    ///public void setSongs(ArrayList<Song> songs) {
    //    this.songs = songs;
    //}

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    static public ArrayList<Album> getAlbumById (Context context, long albumid, int sortBy) {

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
        if (sortBy == MainActivity.ALBUM) {
            sortOrder = MediaStore.Audio.AlbumColumns.ALBUM + " COLLATE LOCALIZED ASC";
        }
        else {
            sortOrder = MediaStore.Audio.AlbumColumns.ARTIST + " COLLATE LOCALIZED ASC";
        }

        Cursor songCursor = contentResolver.query(songUri, projection,null,null, sortOrder);
        //Cursor songCursor = context.getContentResolver().query(songUri, projection,null,null,null);


        if (songCursor != null && songCursor.moveToFirst()) {
            //   int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            //   int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            //   int songLocation = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            //   int songDuration = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
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
                Log.d("Album Info", "Album: " + album);
                Log.d("Artist Info", "Artist: " + artist);
                Log.d("Art Info", "Art: " + art);
                Log.d("Art URI", songUri.toString());

                if (art == null || art.isEmpty()) {
                    //File audioDir = new File (dirmusic.getAbsolutePath() + "/" + artist, "folder.jpg");
                    File audioDir = FindFile.find("folder.jpg", new File(dirmusic, artist));
                    if (audioDir.exists()) {
                        art = audioDir.getAbsolutePath();
                    }
                    Log.d("Album Directory", audioDir.getAbsolutePath());
                }

                albumList.add(new Album (id, album, art, artist, numberSongs));

            } while (songCursor.moveToNext());

            //Collections.sort(arrayList);
        }
        return albumList;
    }


/*    public void getAlbumSongs (Context context) {
        songs = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        MediaStore.Audio.Media media = new MediaStore.Audio.Media();
        String selection = "is_music != 0";

        if (getId() > 0) {
            selection = selection + " and album_id = " + getId();
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

        Uri songUri = media.EXTERNAL_CONTENT_URI;
        String sortOrder = MediaStore.Audio.AudioColumns.TRACK + " COLLATE LOCALIZED ASC";
        Cursor songCursor = null;

        File sdcard = Environment.getExternalStorageDirectory();
        try {
            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            songCursor = contentResolver.query(uri, projection, selection, null, sortOrder);
            if (songCursor != null) {
                songCursor.moveToFirst();
                int position = 1;

                while (!songCursor.isAfterLast()) {
                    Song song = new Song();
                    song.setArtist(songCursor.getString(0));
                    song.setTitle(songCursor.getString(1));
                    song.setPath(songCursor.getString(2));
                    File file = new File(sdcard, "/Music/" + song.getArtist() + "-" + song.getTitle() + ".txt");
                    if (file.exists()){
                        song.setSheetMusicPath(file.getPath());
                    }
                    song.setDispayName((songCursor.getString(3)));
                    song.setDuration(songCursor.getLong(4));
                    song.setAlbumId(songCursor.getInt(5));
                    song.setTrack(songCursor.getString(6));
                    song.setPosition(position);
                    song.setArt(getArt());
                    songs.add(song);

                    songCursor.moveToNext();
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
    }*/
}
