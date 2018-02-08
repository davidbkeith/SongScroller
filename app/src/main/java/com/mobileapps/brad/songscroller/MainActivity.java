package com.mobileapps.brad.songscroller;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MainActivity extends AppCompatActivity {

    //private ArrayList<Album> arrayList;
    //private CustomMusicAdapter adapter;
    private AlbumAdapter adapter;
    private ListView albumList;
    private Album album;
    private Context context;

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        albumList = (ListView) findViewById(R.id.albumList); //activity_main.xml - list view
       // arrayList = new ArrayList<>();

       // getMusic();
       // adapter = new CustomMusicAdapter(this, R.layout.custom_music_item, arrayList);
        adapter = new AlbumAdapter(this, R.layout.album_list_item);
        albumList.setAdapter(adapter);
        albumList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                album = (Album) albumList.getItemAtPosition(position);

                Intent intent = new Intent(context, AlbumSongsActivity.class);
                intent.putExtra("songscroller_album", album);
                context.startActivity (intent);
            //    Intent intent = new Intent(getApplicationContext(), ScrollActivity.class);
            //    intent.putExtra("ScrollSong", music);
            //    getApplicationContext().startActivity (intent);

            }
        });
    }

 /*   public void getMusic () {
        ContentResolver contentResolver = getContentResolver();
        MediaStore.Audio.Albums albums = new MediaStore.Audio.Albums();

        String[] projection = new String[] {albums._ID, albums.ALBUM, albums.ARTIST, albums.ALBUM_ART, albums.NUMBER_OF_SONGS};
        Uri songUri = albums.EXTERNAL_CONTENT_URI;
        //String sortOrder = MediaStore.Audio.Media.ALBUM + "ASC";
        Cursor songCursor = contentResolver.query(songUri, projection,null,null,null);

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
                String id = songCursor.getString(songAlbumId);
                String album = songCursor.getString(songAlbum);
                String artist = songCursor.getString(songArtist);
                String art = songCursor.getString(songArt);
                String numberSongs = songCursor.getString(songNumberOfSongs);

                //if (!"<unknown>".equals(currentArtist)) {
                    arrayList.add(new Album (id, album, artist, art, numberSongs));
               // }
            } while (songCursor.moveToNext());

            //Collections.sort(arrayList);
        }
    }
*/
}
