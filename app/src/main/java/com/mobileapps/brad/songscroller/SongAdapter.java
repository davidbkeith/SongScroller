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

public class SongAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private Album album;
    //private ArrayList<Song> songList;
    //private MediaPlayer mediaPlayer;

    public SongAdapter(Context context, int layout, Album album) {
        this.context = context;
        this.layout = layout;
        album.getAlbumSongs(context);
        this.album = album;
        //songList = album.getSongs();
        this.album.getAlbumSongs(context);
        //getAlbumSongs(album);
    }

    @Override
    public int getCount() {
        return album.getSongs().size();
    }

    @Override
    public Object getItem(int i) {
        return album.getSongs().get(i);
    }

    @Override
    public long getItemId(int i) {
        return album.getSongs().get(i).getAlbumId();
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
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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

        long duration = song.getDuration();
        viewHolder.txtSongName.setText(song.getTrack() + ". " + song.getTitle());
        if (song.getSheetMusicPath() != null && !song.getSheetMusicPath().isEmpty()) {
            Log.d("Message", "Path is: " + song.getSheetMusicPath());
            viewHolder.txtSongName.setTextColor(viewHolder.txtSongName.getResources().getColor(R.color.colorAccentLight));
            //viewHolder.txtSongName.setSelected(true);
        }
        else {
            viewHolder.txtSongName.setTextColor(viewHolder.txtSongName.getResources().getColor(R.color.colorScreenLight));
        }
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1);
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

  /*  public void getAlbumSongs (Album album) {
        songList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        MediaStore.Audio.Media media = new MediaStore.Audio.Media();
        String selection = "is_music != 0";

        if (album.getId() > 0) {
            selection = selection + " and album_id = " + album.getId();
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
                    song.setDispayName((songCursor.getString(3)));
                    song.setDuration(songCursor.getLong(4));
                    song.setAlbumId(songCursor.getInt(5));
                    song.setTrack(songCursor.getString(6));
                    song.setPosition(position);
                    song.setArt(album.getArt());
                    songList.add(song);

                    songCursor.moveToNext();
                }

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
