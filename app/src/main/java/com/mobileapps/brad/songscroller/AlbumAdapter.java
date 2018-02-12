package com.mobileapps.brad.songscroller;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by brad on 1/29/18.
 */

public class AlbumAdapter extends BaseAdapter {

    private MainActivity mainActivity;
    private int layout;
    private int mView;
    private ArrayList<Album> albumList;

    private class ViewHolder {
        TextView txtAlbum, txtArtist, txtSongCount;
        ImageView ivAlbumArt;
    }

    public AlbumAdapter(Context context, int layout, int View) {
        this.mainActivity = (MainActivity) context;
        this.layout = layout;
        mView = View;
        albumList = Album.getAlbumById(context, 0, mainActivity.getmAlbumView());
        sortAblumsBy(mView);
        SetView (mView);
    }

    class ArtistCompare implements Comparator<Album> {
        @Override
        public int compare(Album alb1, Album alb2) {
            return alb1.getArtist().compareTo(alb2.getArtist());
        }
    }
    class AlbumCompare implements Comparator<Album> {
        @Override
        public int compare(Album alb1, Album alb2) {
            return alb1.getAlbum().compareTo(alb2.getAlbum());
        }
    }
    public void sortAblumsBy (int sortOrder) {
        if (sortOrder == MainActivity.ARTIST) {
            Collections.sort(albumList, new ArtistCompare());
        }
        else {
            Collections.sort(albumList, new AlbumCompare());
        }
    }

    @Override
    public int getCount() {
        return albumList.size();
    }

    @Override
    public Album getItem(int i) {
        return (Album) albumList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void SetView (int View)  {
        mView = View;
        sortAblumsBy(mView);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Log.e("Media", "Entered Album Adaptor: getView");
        ///////// find the views
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView= layoutInflater.inflate(layout, null);
            viewHolder.txtAlbum = (TextView) convertView.findViewById(R.id.album_name);
            viewHolder.txtArtist = (TextView) convertView.findViewById(R.id.album_artist);
            viewHolder.txtSongCount = (TextView) convertView.findViewById(R.id.album_song_count);
            viewHolder.ivAlbumArt = (ImageView) convertView.findViewById(R.id.album_art);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ///////// set view values
        final Album album = albumList.get(position);
        //final Music music = new Music("","","","","");
        viewHolder.txtAlbum.setText(album.getAlbum());
        viewHolder.txtArtist.setText(album.getArtist());
        viewHolder.txtSongCount.setText(album.getNumberSongs() + " songs ");

        //File bkground = new File(sdcard, "/Music/" + album.getSinger() + "-" + music.getName() + ".jpg");
      //  String artpath = album.getArt();
     //   if (artpath != null && !artpath.isEmpty()) {
            Drawable albumimage = Drawable.createFromPath(album.getArt());
            viewHolder.ivAlbumArt.setImageDrawable(albumimage);
     //   }
     //   else {
     //       artpath = null;
     //   }
  /*      else {
            AssetManager assetManager = context.getAssets();
            try {
                String[] list = assetManager.list("./");
                for (String s : list)  {
                    Log.d("File:", s);
                }
            }
            catch (Exception e) {

            }

        }*/

        ///////// event listeners
       /* convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
            Intent intent = new Intent(context, AlbumSongsActivity.class);
            intent.putExtra("songscroller_album", album);
            context.startActivity (intent);


        /*       String songpath = music.getSong();
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause ();
                }
                mediaPlayer = MediaPlayer.create(context, Uri.parse(songpath));
                mediaPlayer.start();*/

                //Intent intent = new Intent(v.getContext(), ScrollActivity.class);
                //intent.putExtra("ScrollSong", music);
                //v.getContext().startActivity (intent);
           // }
       // });

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
