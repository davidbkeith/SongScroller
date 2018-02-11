package com.mobileapps.brad.songscroller;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int ALBUM = 0;
    public static final int ARTIST = 1;
    public static final int SONG = 0;
    public static final int SCORE = 2;
    private String[] albumViews = {"ALBUM", "ARTIST"};
    private String[] songViews = {"SONG", "ARTIST", "SCORE"};

    //private ArrayList<Album> arrayList;
    //private CustomMusicAdapter adapter;
    private AlbumAdapter albumAdapter;
    private SongAdapter songAdapter;
    private ListView albumList;

    private Button artistsButton, songsButton;
    private Album album;
    private Context context;

    public Integer getmAlbumView() {
        return mAlbumView;
    }

    public void setmAlbumView(Integer AlbumView) {
        this.mAlbumView = AlbumView;
    }

    public Integer getmSongView() {
        return mSongView;
    }

    public void setmSongView(Integer SongView) {
        this.mSongView = SongView > 2 ? 0 : SongView;
    }

    private Integer mAlbumView;
    private Integer mSongView;
    private boolean mShowSongsWithScore;

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
        setmAlbumView(ALBUM);
        setmSongView(SONG);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //// set title
        getSupportActionBar().setTitle(String.format("%s: %s", getSupportActionBar().getTitle(), "Albums"));

        albumList = (ListView) findViewById(R.id.albumList); //activity_main.xml - list view
        artistsButton = (Button) findViewById(R.id.buttonArtists);
        songsButton = (Button) findViewById(R.id.buttonSongs);
        artistsButton.setOnClickListener(this);
        songsButton.setOnClickListener(this);
   /*     artistsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.sortAblumsBy(++mSortBy == 2 ? mSortBy=0 : mSortBy);
                adapter.notifyDataSetChanged();
            }
        });*/
       // arrayList = new ArrayList<>();

       // getMusic();
       // adapter = new CustomMusicAdapter(this, R.layout.custom_music_item, arrayList);
      //  albumAdapter = new AlbumAdapter(this, R.layout.album_list_item);
     //   onClick(artistsButton);
        getListItems (artistsButton);

     /*    albumList.setAdapter(albumAdapter);
        albumList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                album = (Album) albumList.getItemAtPosition(position);
                Intent intent = new Intent(context, AlbumSongsActivity.class);
                intent.putExtra("songscroller_album", album);
                context.startActivity (intent);
            }
        });

   /*     artistsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                adapter.sortAblumsBy(++mSortBy == 2 ? mSortBy=0 : mSortBy);
                adapter.notifyDataSetChanged();
            }
        });*/
    }

    // Defines the buttons behavior when clicked.
    @Override
    public void onClick (View v) {
        getListItems ((Button) v);
    }

    public void getListItems(Button button) {
       // Button button = (Button) findViewById(buttonId);;
        switch (button.getId()) {
            case R.id.buttonArtists: {
                setmAlbumView(albumAdapter == null ? getmAlbumView() : getmAlbumView()== ARTIST ? ALBUM : ARTIST);
                button.setText(albumViews[getmAlbumView()]);

                if (albumAdapter != null) {
                    albumAdapter.sortAblumsBy(mAlbumView);
                    albumAdapter.notifyDataSetChanged();
                }
                else {
                    albumAdapter = new AlbumAdapter(this, R.layout.album_list_item);
                    albumList.setAdapter(albumAdapter);
                    albumList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            album = (Album) albumList.getItemAtPosition(position);
                            Intent intent = new Intent(context, AlbumSongsActivity.class);
                            intent.putExtra("songscroller_album", album);
                            context.startActivity (intent);
                        }
                    });
                }
                songAdapter = null;
                break;
            }
            case R.id.buttonSongs: {
                setmSongView(songAdapter == null ? getmSongView() : getmSongView() + 1);
                button.setText(songViews[getmSongView()]);

                if (songAdapter != null) {
                    songAdapter.sortAblumsBy(getmSongView());
                    if (getmSongView() == SCORE) {
                        songAdapter.getScores();
                    }
                    songAdapter.notifyDataSetChanged();
                }
                else {
                    songAdapter = new SongAdapter(this, R.layout.song_list_item);
                    albumList.setAdapter(songAdapter);
                    songAdapter.sortAblumsBy(getmSongView());

                    albumList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            Song song = (Song) albumList.getItemAtPosition(position);
                            Intent intent = new Intent(context, ScrollActivity.class);
                            intent.putExtra("songscroller_song", song);
                            context.startActivity(intent);
                        }
                    });
                }
                albumAdapter = null;
                break;
            }
        }
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
