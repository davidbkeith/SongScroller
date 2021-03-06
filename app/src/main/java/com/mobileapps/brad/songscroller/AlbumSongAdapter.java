package com.mobileapps.brad.songscroller;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by brad on 1/23/18.
 */

public class AlbumSongAdapter extends BaseAdapter {

    private AlbumSongsActivity albumSongsActivity;
    private int layout;
    public Album getAlbum() {
        return album;
    }
    public void setAlbum(Album album) {
        this.album = album;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    private Album album;
    private ArrayList<Song> songs;
    //private ArrayList<Song> songList;
    //private MediaPlayer mediaPlayer;

    public AlbumSongAdapter(Context context, int layout) {
        albumSongsActivity = (AlbumSongsActivity) context;
        this.layout = layout;
        songs = Song.getSongs(context, albumSongsActivity.getAlbum(), MediaStore.Audio.AudioColumns.TRACK);
        //this.album = album;
        //songList = album.getSongs();
        //this.album.getAlbumSongs(context);
        //getAlbumSongs(album);
    }

    @Override
    public int getCount() {
        return getSongs().size();
    }

    @Override
    public Song getItem(int i) {
        return getSongs().get(i);
    }

    @Override
    public long getItemId(int i) {
        return getSongs().get(i).getAlbumId();
    }

    private class ViewHolder {
        TextView txtSongName, txtDuration;
        //ImageView ivPlay, ivPause;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        Log.d("Inside", "entered Song Adaptor getView....");
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) albumSongsActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView= layoutInflater.inflate(layout, null);
            viewHolder.txtSongName = (TextView) convertView.findViewById(R.id.song_name);
            viewHolder.txtDuration = (TextView) convertView.findViewById(R.id.song_duration);
           // viewHolder.ivPlay = (ImageView) convertView.findViewById(R.id.ivPlay);
           // viewHolder.ivPause = (ImageView) convertView.findViewById(R.id.ivPause);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Song song = (Song) getItem(position);

        double duration = song.getDuration();
        viewHolder.txtSongName.setText(String.format("%s. %s", song.getTrack(), song.getTitle()));
        if (song.getSheetMusicPath() != null && !song.getSheetMusicPath().isEmpty()) {
            //Log.d("Message", "Path is: " + song.getSheetMusicPath());
            viewHolder.txtSongName.setTextColor(viewHolder.txtSongName.getResources().getColor(R.color.colorAccentLight));
            //viewHolder.txtSongName.setSelected(true);
        }
        else {
            viewHolder.txtSongName.setTextColor(viewHolder.txtSongName.getResources().getColor(R.color.colorScreenLight));
        }
        long minutes = TimeUnit.MILLISECONDS.toMinutes((long) duration) % TimeUnit.HOURS.toMinutes(1);
        long seconds = TimeUnit.MILLISECONDS.toSeconds((long) duration) % TimeUnit.MINUTES.toSeconds(1);
    //    long seconds=(duration/1000)%60;
     //   long minutes =((duration-seconds)/1000)/60;

        viewHolder.txtDuration.setText(String.format("%d:%02d", minutes, seconds));
        // viewHolder.txtDuration.setText(Long.toString(minutes), Long.toString(seconds)));


       // convertView.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
         /*       String songpath = music.getSong();
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause ();
                }
                mediaPlayer = MediaPlayer.create(context, Uri.parse(songpath));
                mediaPlayer.start();*/

        //        Intent intent = new Intent(v.getContext(), ScrollActivity.class);
         //       intent.putExtra("songscroller_song", song);
        //        v.getContext().startActivity (intent);
        //     }
        //});

   /*     viewHolder.ivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String songpath = music.getSong();
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause ();
                    //viewHolder.ivPlay.setImageResource(R.drawable.ic_play);
                }
            }
        });*/

        return convertView;
    }
}
