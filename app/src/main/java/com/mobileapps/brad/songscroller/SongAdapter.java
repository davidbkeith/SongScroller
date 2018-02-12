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
   /* public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }*/

    public ArrayList<Song> getSongList() {
        return songList;
    }

    public void setSongList(ArrayList<Song> songList) {
        this.songList = songList;
    }

    private Context context;
    private int layout;
    private int mView;
    //private Song song;
    private ArrayList<Song> songList;
    private ArrayList<Integer> scoreIndices;
    //private MediaPlayer mediaPlayer;

    public SongAdapter(Context context, int layout, int View) {
        //mainActivity = (MainActivity) context;
        mView = View;
        this.context = context;
        this.layout = layout;
        setSongList(Song.getSongs(context, 0, MediaStore.Audio.AudioColumns.TITLE));
        SetView (View);
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
        if (sortOrder == MainActivity.ARTIST || sortOrder == MainActivity.SCORE_ARTIST) {
            Collections.sort(songList, new ArtistCompare());
        }
        else {
            Collections.sort(songList, new SongCompare());
        }
    }

    public void getScores () {
       // if (scoreIndices == null) {
            scoreIndices = new ArrayList<>();
            int i = 0;
            for (Song song : songList) {
                if (song.getSheetMusicPath() != null) {
                    scoreIndices.add(i);
                }
                i++;
            }
       // }
    }

    public void SetView (int View)  {
        mView = View;
        sortAblumsBy(mView);
        switch (mView) {
            case MainActivity.SONG:
             //   scoreIndices = null;
                break;
            case MainActivity.ARTIST:
                break;
            case MainActivity.SCORE_SONG:
                getScores();
                break;
            case MainActivity.SCORE_ARTIST:
                getScores();
                break;
        }
    }

    @Override
    public int getCount() {
        if (mView == MainActivity.SCORE_SONG || mView == MainActivity.SCORE_ARTIST) {
            return scoreIndices.size();
        }
        else {
            return songList.size();
        }
    }

    @Override
    public Object getItem(int i) {
        if (mView == MainActivity.SCORE_SONG || mView == MainActivity.SCORE_ARTIST) {
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

        if (convertView == null) {
            viewHolder = new SongAdapter.ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView= layoutInflater.inflate(layout, null);
            viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.title);
            viewHolder.txtSubtitle = (TextView) convertView.findViewById(R.id.subtitle);
            viewHolder.txtDuration = (TextView) convertView.findViewById(R.id.song_duration);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (SongAdapter.ViewHolder) convertView.getTag();
        }

        long duration = song.getDuration();

        if (mView == MainActivity.ARTIST || mView == MainActivity.SCORE_ARTIST) {
            viewHolder.txtSubtitle.setText(song.getTitle());
            viewHolder.txtTitle.setText(song.getArtist());
        }
        else {
            viewHolder.txtTitle.setText(song.getTitle());
            viewHolder.txtSubtitle.setText(song.getArtist());
        }

        if (song.getSheetMusicPath() != null && !song.getSheetMusicPath().isEmpty()) {
            //Log.d("Message", "Path is: " + song.getSheetMusicPath());
            //viewHolder.txtSongName.setTextColor(viewHolder.txtSongName.getResources().getColor(R.color.colorAccentLight));
            viewHolder.txtDuration.setTextColor(viewHolder.txtDuration.getResources().getColor(R.color.colorAccentLight));
            //viewHolder.txtSongName.setSelected(true);
        }
        else {
            viewHolder.txtDuration.setTextColor(viewHolder.txtDuration.getResources().getColor(R.color.colorMenuBarLight));
            //viewHolder.txtSongName.setSelected(true);
            //viewHolder.txtSongName.setTextColor(viewHolder.txtSongName.getResources().getColor(R.color.colorScreenLight));
        }

        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1);

        viewHolder.txtDuration.setText(String.format("%d:%02d", minutes, seconds));

        return convertView;
    }

}
