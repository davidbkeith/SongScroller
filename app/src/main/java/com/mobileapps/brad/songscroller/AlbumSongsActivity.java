package com.mobileapps.brad.songscroller;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AlbumSongsActivity extends AppCompatActivity {
    private AlbumSongAdapter adapter;
    private ListView songList;
    private Context context;
    private Album album;
    private Song selectedsong;

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    private TextView txtAlbum, txtArtist, txtSongCount;
    private ImageView ivAlbumArt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_album_songs); //activity_album_songs - list view
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        album = album == null ? (Album) getIntent().getSerializableExtra("songscroller_album") : album;

        songList = (ListView) findViewById(R.id.songList);
        adapter = new AlbumSongAdapter(this, R.layout.album_song_list_item);

        try {
            songList.setAdapter(adapter);

            txtAlbum = (TextView) findViewById(R.id.textAlbum);
            txtArtist = (TextView) findViewById(R.id.textArtist);
            ivAlbumArt = (ImageView) findViewById(R.id.album_art);

            Drawable albumimage = Drawable.createFromPath(album.getArt());
            ivAlbumArt.setImageDrawable(albumimage);
            txtAlbum.setText(album.getAlbum());
            txtArtist.setText(album.getArtist());

            songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    selectedsong = (Song) songList.getItemAtPosition(position);
                    Intent intent = new Intent(context, ScrollActivity.class);
                    intent.putExtra("songscroller_song", selectedsong);
                    startActivityForResult (intent, 1);
                }
            });
        }
        catch (Exception e){
            Log.e("Error", e.toString());
        }
     }
}
