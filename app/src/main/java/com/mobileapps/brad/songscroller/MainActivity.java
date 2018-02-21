package com.mobileapps.brad.songscroller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.File;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    public static final int ALBUM = 0;
    public static final int ARTIST = 1;
    public static final int SONG = 0;

  /*  public String getMainView() {
        return mainView;
    }

    public void setMainView(String mainView) {
        this.mainView = mainView;
    }*/

    //private String mainView;
    private String[] albumViews = {"ALBUM", "ARTIST"};
    private String[] songViews = {"SONG", "ARTIST"};
    private String[] scoreViews = {"SONG", "ARTIST"};

    //private String[] albumTitleBar = {"Albums", "Artists"};
    //private String[] songTitleBar = {"Songs", "Songs", "Scores", "Scores"};

    private AlbumAdapter albumAdapter;
    private SongAdapter songAdapter;
    private ScoreAdapter scoreAdapter;

    private ListView albumList;
    private Spinner viewSpinner;

    private Integer mAlbumView;
    private Integer mSongView;
    private Integer mScoreView;
    private boolean mShowSongsWithScore;
    private Button songsButton;
    private Album album;
    private Context context;

    public Integer getmAlbumView() {
        return mAlbumView;
    }

    public void setmAlbumView(Integer AlbumView) {
        this.mAlbumView = AlbumView > 1 ? 0: AlbumView;
    }

    public String getCurrentView () {
        return (String) viewSpinner.getSelectedItem();
    }

    public void refreshAlbumView () {
        /// forces refresh of view
        albumAdapter = null;
    }

    public Integer getmSongView() {
        return mSongView;
    }

    public void setmSongView(Integer SongView) {
        this.mSongView = SongView > 1 ? 0 : SongView;
    }

    public Integer getmScoreView() {
        return mScoreView;
    }

    public void setmScoreView(Integer ScoreView) {
        this.mScoreView = ScoreView > 1 ? 0 : ScoreView;
    }

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        albumList = (ListView) findViewById(R.id.albumList); //activity_main.xml - list view
        songsButton = (Button) findViewById(R.id.buttonSongs);
        viewSpinner = (Spinner) findViewById(R.id.view_spinner);

        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(context, R.array.view_spinner, android.R.layout.simple_spinner_dropdown_item);
        viewSpinner.setAdapter(adapterSpinner);
        viewSpinner.setOnItemSelectedListener(this);

        /// set listeners
        //artistsButton.setOnClickListener(this);
        songsButton.setOnClickListener(this);

        /// populate view
        SharedPreferences sharedPreferences = this.getPreferences(context.MODE_PRIVATE);
        setmAlbumView(sharedPreferences.getInt("AlbumView", 0));
        setmSongView(sharedPreferences.getInt("SongView", 0));
        setmScoreView(sharedPreferences.getInt("ScoreView", 0));

        viewSpinner.setSelection(sharedPreferences.getInt("selectedView", 0));
        //setMainView(sharedPreferences.getString("mainView", "Album"));

   //     getListItems ();
  /*      if ("Song".compareTo(mainView) == 0) {
            getListItems (songsButton);
        }
        else {
            getListItems(artistsButton);
        }*/
    }

    @Override protected void onStop () {
        SharedPreferences sharedPreferences = this.getPreferences(context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt ("selectedView", viewSpinner.getSelectedItemPosition());
        editor.putInt ("AlbumView", mAlbumView);
        editor.putInt ("SongView", mSongView);
        editor.putInt ("ScoreView", mScoreView);
        editor.commit();
        super.onStop();
    }

    @Override
    public void onItemSelected (AdapterView<?> parent, View view, int pos, long id) {
        if ("Album".compareTo((String) viewSpinner.getSelectedItem()) == 0) {
            songsButton.setText(albumViews[getmAlbumView()]);
        }
        else if ("Song".compareTo((String) viewSpinner.getSelectedItem()) == 0) {
            songsButton.setText(songViews[getmSongView()]);
        }
        else  {
            songsButton.setText(scoreViews[getmScoreView()]);
        }
        getListItems();
    }

    @Override
    public void onNothingSelected (AdapterView<?> parent) {
    }

    // Defines the buttons behavior when clicked.
    @Override
    public void onClick (View v) {
        if ("Album".compareTo((String) viewSpinner.getSelectedItem()) == 0) {
            setmAlbumView(getmAlbumView() + 1);
            songsButton.setText(albumViews[getmAlbumView()]);
        }
        else if ("Song".compareTo((String) viewSpinner.getSelectedItem()) == 0) {
            setmSongView(getmSongView() + 1);
            songsButton.setText(songViews[getmSongView()]);
        }
        else  {
            setmScoreView(getmScoreView() + 1);
            songsButton.setText(scoreViews[getmScoreView()]);
        }
        getListItems ();
    }

    public void getListItems() {
       // Button button = (Button) findViewById(buttonId);;
        if ("Album".compareTo((String) viewSpinner.getSelectedItem()) == 0) {

            if (albumAdapter != null) {
                albumAdapter.SetView (getmAlbumView());
                albumAdapter.notifyDataSetChanged();
            }
            else {
                albumAdapter = new AlbumAdapter(this, R.layout.album_list_item, getmAlbumView());
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

            //// set title
            //getSupportActionBar().setTitle(String.format("Song Scroller: %s", albumTitleBar[getmAlbumView()]));
            songAdapter = null;
            scoreAdapter = null;
        }
        else if ("Song".compareTo((String) viewSpinner.getSelectedItem()) == 0) {
           // setmSongView(songAdapter == null ? getmSongView() : getmSongView() + 1);
           // button.setText(songViews[getmSongView()]);

            if (songAdapter != null) {
                songAdapter.sortSongsBy(getmSongView());
                songAdapter.notifyDataSetChanged();
            }
            else {
                songAdapter = new SongAdapter(this, R.layout.song_list_item);
                albumList.setAdapter(songAdapter);
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

            //// set title
            //getSupportActionBar().setTitle(String.format("Song Scroller: %s", songTitleBar[getmSongView()]));
            albumAdapter = null;
            scoreAdapter = null;
        }
        else {
            if (scoreAdapter != null) {
                scoreAdapter.sortSongsBy(getmScoreView());
                scoreAdapter.notifyDataSetChanged();
            }
            else {
                scoreAdapter = new ScoreAdapter(this, R.layout.song_list_item);
                albumList.setAdapter(scoreAdapter);
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

            //// set title
            //getSupportActionBar().setTitle(String.format("Song Scroller: %s", songTitleBar[getmSongView()]));
            albumAdapter = null;
            songAdapter = null;
        }
    }
}
