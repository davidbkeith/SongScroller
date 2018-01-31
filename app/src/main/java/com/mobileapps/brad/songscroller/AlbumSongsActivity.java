package com.mobileapps.brad.songscroller;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class AlbumSongsActivity extends AppCompatActivity {
    private SongAdapter adapter;
    private ListView songList;
    private Context context;
    private Album album;

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_songs); //activity_album_songs - list view

        album = (Album) getIntent().getSerializableExtra("songscroller_album");

        songList = (ListView) findViewById(R.id.songList);
       // arrayList = new ArrayList<>();

        // getMusic();
        // adapter = new CustomMusicAdapter(this, R.layout.custom_music_item, arrayList);
        adapter = new SongAdapter(this, R.layout.song_list_item, album);
        songList.setAdapter(adapter);

    }
}
