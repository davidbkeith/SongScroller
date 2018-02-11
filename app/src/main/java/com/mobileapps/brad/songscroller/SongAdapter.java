package com.mobileapps.brad.songscroller;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

/**
 * Created by brad on 2/10/18.
 */

public class SongAdapter extends BaseAdapter {
    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public ArrayList<Song> getSongList() {
        return songList;
    }

    public void setSongList(ArrayList<Song> songList) {
        this.songList = songList;
    }

    private MainActivity mainActivity;
    private int layout;
    //private Song song;
    private ArrayList<Song> songList;
    private ArrayList<Integer> scoreIndices;
    //private MediaPlayer mediaPlayer;

    public SongAdapter(Context context, int layout) {
        mainActivity = (MainActivity) context;
        this.layout = layout;
        setSongList(Song.getSongs(context, 0, MediaStore.Audio.AudioColumns.TITLE));
    }

    class SongCompare implements Comparator<Song> {
        @Override
        public int compare(Song song1, Song song2) {
            return song1.getTitle().compareTo(song2.getTitle());
        }
    }
    class ArtistCompare implements Comparator<Song> {
        @Override
        public int compare(Song song1, Song song2) {
            return song1.getArtist().compareTo(song2.getArtist());
        }
    }
    public void sortAblumsBy (int sortOrder) {
        if (sortOrder == MainActivity.ARTIST) {
            Collections.sort(songList, new ArtistCompare());
        }
        else {
            Collections.sort(songList, new SongCompare());
        }
    }
    public void getScores () {
        scoreIndices = new ArrayList<>();
        int i=0;
        for (Song song : songList) {
            if(song.getSheetMusicPath() != null) {
                scoreIndices.add(i);
            }
            i++;
        }
    }

    @Override
    public int getCount() {
        if (mainActivity.getmSongView() == mainActivity.SCORE) {
            return scoreIndices.size();
        }
        else {
            return songList.size();
        }
    }

    @Override
    public Object getItem(int i) {
        if (mainActivity.getmSongView() == mainActivity.SCORE) {
            return songList.get(scoreIndices.get(i));
        }
        else {
            return songList.get(i);
        }
    }

    @Override
    public long getItemId(int i) {
        return songList.get(i).getAlbumId();
    }

    private class ViewHolder {
        TextView txtTitle, txtSubtitle, txtDuration;
        ImageView ivAlbumArt;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final SongAdapter.ViewHolder viewHolder;
        //Log.d("Inside", "entered Song Adaptor getView....");
        final Song song = (Song) getItem(position);

        //return inflater.inflate(R.layout.null_item, null);

        if (convertView == null) {
            viewHolder = new SongAdapter.ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) getMainActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView= layoutInflater.inflate(layout, null);

           // if (mainActivity.getmSongView() == mainActivity.ARTIST) {
           //     Log.d("View Set To: ", "ARTIST");
          //      viewHolder.txtSongName = (TextView) convertView.findViewById(R.id.subtitle);
          //      viewHolder.txtArtist = (TextView) convertView.findViewById(R.id.title);
           // }
           // else {
                viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.title);
                viewHolder.txtSubtitle = (TextView) convertView.findViewById(R.id.subtitle);
           // }
           // viewHolder.ivAlbumArt = (ImageView) convertView.findViewById(R.id.album_art);
            viewHolder.txtDuration = (TextView) convertView.findViewById(R.id.song_duration);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (SongAdapter.ViewHolder) convertView.getTag();
        }

     /*   if (mainActivity.getmView() == mainActivity.SCORE && song.getSheetMusicPath() == null) {
            convertView = new View(mainActivity);
            return convertView;
        }*/

       // Log.d("Art Path", song.getArt());
       // Drawable albumimage = Drawable.createFromPath(song.getArt());
      //  viewHolder.ivAlbumArt.setImageDrawable(albumimage);

        long duration = song.getDuration();

        if (mainActivity.getmSongView() == mainActivity.ARTIST) {
            viewHolder.txtSubtitle.setText(song.getTitle());
            viewHolder.txtTitle.setText(song.getArtist());
        }
        else {
            viewHolder.txtTitle.setText(song.getTitle());
            viewHolder.txtSubtitle.setText(song.getArtist());
        }

    /*    if (song.getSheetMusicPath() != null && !song.getSheetMusicPath().isEmpty()) {
            //Log.d("Message", "Path is: " + song.getSheetMusicPath());
            viewHolder.txtSongName.setTextColor(viewHolder.txtSongName.getResources().getColor(R.color.colorAccentLight));
            //viewHolder.txtSongName.setSelected(true);
        }
        else {
            viewHolder.txtSongName.setTextColor(viewHolder.txtSongName.getResources().getColor(R.color.colorScreenLight));
        }*/
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1);

        viewHolder.txtDuration.setText(String.format("%d:%02d", minutes, seconds));

      /*  if (mainActivity.getmSongView() == mainActivity.SCORE && song.getSheetMusicPath() == null) {
            convertView.setVisibility(View.INVISIBLE);
        }
        else {
            convertView.setVisibility(View.VISIBLE);
        }*/
        return convertView;
    }

}
