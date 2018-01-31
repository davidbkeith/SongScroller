package com.mobileapps.brad.songscroller;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
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

/**
 * Created by brad on 1/29/18.
 */

public class AlbumAdapter extends BaseAdapter {

    private Context context;
    private int layout;
  //  private Album album;
  //  private File sdcard;
    private ArrayList<Album> albumList;


    private class ViewHolder {
        TextView txtAlbum, txtArtist, txtSongCount;
        ImageView ivAlbumArt;
    }

    public AlbumAdapter(Context context, int layout) {
        this.context = context;
        this.layout = layout;
       //  sdcard = Environment.getExternalStorageDirectory();

        getAlbums();
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


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Log.e("Media", "Entered Album Adaptor: getView");
        ///////// find the views
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
        viewHolder.txtSongCount.setText(album.getArtist());
        viewHolder.txtArtist.setText(album.getNumberSongs() + " songs");

        //File bkground = new File(sdcard, "/Music/" + album.getSinger() + "-" + music.getName() + ".jpg");
        //String path =
        Drawable albumimage = Drawable.createFromPath(album.getArt());
        viewHolder.ivAlbumArt.setImageDrawable(albumimage);

        ///////// event listeners
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

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

    public void getAlbums () {
        albumList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        MediaStore.Audio.Albums albums = new MediaStore.Audio.Albums();
        Uri songUri = albums.EXTERNAL_CONTENT_URI;
        String selection = "is_music != 0";
        String[] projection = new String[] {albums._ID, albums.ALBUM, albums.ARTIST, albums.ALBUM_ART, albums.NUMBER_OF_SONGS};
        //String sortOrder = MediaStore.Audio.Media.ALBUM + "ASC";
        Cursor songCursor = contentResolver.query(songUri, projection,null,null,null);
        //Cursor songCursor = context.getContentResolver().query(songUri, projection,null,null,null);

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
                long id = songCursor.getLong(songAlbumId);
                String album = songCursor.getString(songAlbum);
                String artist = songCursor.getString(songArtist);
                String art = songCursor.getString(songArt);
                String numberSongs = songCursor.getString(songNumberOfSongs);

               // if (!"<unknown>".equals(artist)) {
                    albumList.add(new Album (id, album, art, artist, numberSongs));
               // }
            } while (songCursor.moveToNext());

            //Collections.sort(arrayList);
        }

    }

}
