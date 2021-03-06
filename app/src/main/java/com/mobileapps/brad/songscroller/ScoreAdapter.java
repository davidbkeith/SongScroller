package com.mobileapps.brad.songscroller;

import android.content.ContentUris;
import android.content.Context;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by brad on 2/20/18.
 */

public class ScoreAdapter extends SongAdapter {
   // MainActivity mainActivity;
   // Context context;
    private ArrayList<Song> scores;

    public ScoreAdapter(Context context, int layout, String sortBy) {
        super(context, layout, sortBy);
        if (scores == null) {
            scores = new ArrayList<>();
            for (Song song : songList) {
                if (song.getSheetMusicPath() != null) {
                    scores.add(song);
                }
            }
            ArrayList<Song> textScores = Song.getScores(context, null, sortBy);
            for (Song textScore: textScores ) {
                if (!songList.contains(textScore)) {
                    scores.add(textScore);
                }
            }
        }

        //songList.addAll(scores);
        sortSongsBy(sortBy);
       // if (getMainActivity().getmSongView() == MainActivity.ARTIST) {
       //     sortSongsBy(MainActivity.ARTIST);
       // }
    }

    public void sortSongsBy (String sortOrder) {
        if (MediaStore.Audio.AudioColumns.ARTIST.compareTo(sortOrder) == 0) {
            Collections.sort(scores, new ArtistCompare());
        }
        else {
            Collections.sort(scores, new SongCompare());
        }
    }

    @Override
    public int getCount() {
        return scores.size();
    }

    @Override
    public Song getItem(int i) {
        return scores.get(i);
    }

    @Override
    public long getItemId(int i) {
        return scores.get(i).getAlbumId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView (position, convertView, parent);

        if (MediaStore.Audio.AudioColumns.ARTIST.compareTo(getMainActivity().getmScoreView()) == 0) {
            viewHolder.txtTitle.setText(scores.get(position).getArtist());
            viewHolder.txtSubtitle.setText(scores.get(position).getTitle());
         }
        else {
            viewHolder.txtTitle.setText(scores.get(position).getTitle());
            viewHolder.txtSubtitle.setText(scores.get(position).getArtist());
        }
        return view;
    }
}
