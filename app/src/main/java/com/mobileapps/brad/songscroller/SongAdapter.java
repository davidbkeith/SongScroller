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

    private Context context;
    protected int layout;
    static protected ArrayList<Song> songList;
    private MainActivity mainActivity;
    private int numScores;
    protected SongAdapter.ViewHolder viewHolder;

    public SongAdapter () {}

    public SongAdapter(Context context, int layout, String sortBy) {
        mainActivity = (MainActivity) context;
        this.context = context;
        this.layout = layout;

        if (songList == null) {
            setSongList(Song.getSongs(context, null, sortBy));
        }
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

    public void sortSongsBy (String sortOrder) {
        if (MediaStore.Audio.AudioColumns.ARTIST.compareTo(sortOrder) == 0) {
            Collections.sort(songList, new ArtistCompare());
        }
        else {
            Collections.sort(songList, new SongCompare());
        }
    }

    @Override
    public int getCount() {
        return songList.size();
    }

    @Override
    public Song getItem(int i) {
        return songList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return songList.get(i).getAlbumId();
    }

    protected class ViewHolder {
        TextView txtTitle, txtSubtitle, txtDuration;
        ImageView ivAlbumArt;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
        viewHolder.txtTitle.setText(song.getTitle());
        viewHolder.txtSubtitle.setText(song.getArtist());

        if (song.getSheetMusicPath() != null && !song.getSheetMusicPath().isEmpty()) {
             viewHolder.txtDuration.setTextColor(viewHolder.txtDuration.getResources().getColor(R.color.colorAccentLight));
        }
        else {
            viewHolder.txtDuration.setTextColor(viewHolder.txtDuration.getResources().getColor(R.color.colorMenuBarLight));
        }

        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1);
        viewHolder.txtDuration.setText(String.format("%d:%02d", minutes, seconds));
        return convertView;
    }

}
