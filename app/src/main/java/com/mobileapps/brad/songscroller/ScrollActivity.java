package com.mobileapps.brad.songscroller;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ScrollActivity extends AppCompatActivity {

    private Context context;
    private TextView textView;
    private ImageView ivPlay;
    private ImageView ivPause;
    private MediaPlayer mediaPlayer;
    private Music music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll);
        context = this.context;

        textView = (TextView) findViewById(R.id.textView);
        music = (Music) getIntent().getSerializableExtra("ScrollSong");

        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, "/Music/" + music.getSinger() + "-" + music.getName() + ".txt");

        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }

//Find the view by its id
        textView.setText(text);
        ivPlay = findViewById(R.id.ivPlay);

        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String songpath = music.getSong();
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause ();
                ivPlay.setImageResource(android.R.drawable.ic_media_play);
            }
            else {
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer.create(context, Uri.parse(songpath));
                }
                mediaPlayer.start();
                ivPlay.setImageResource(android.R.drawable.ic_media_pause);
            }
             }
        });

       /* viewHolder.ivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String songpath = music.getSong();
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause ();
                    //viewHolder.ivPlay.setImageResource(R.drawable.ic_play);
                }
            }
        });*/


    }
}
