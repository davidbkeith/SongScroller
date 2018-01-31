package com.mobileapps.brad.songscroller;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;

public class ScrollActivity extends AppCompatActivity {

    private Context context;
    private TextView textView, titleView;
    private ImageView ivPlay;
    private ImageView ivPause;
    private ImageView ivAlbumArt;
    private MediaPlayer mediaPlayer;
    private Song song;
    private android.os.Handler handler = new android.os.Handler();
    private ScrollView scrollView;
    private double textVeiwHeight;
    private int screenHeight;

    /**
     * The Move seek bar. Thread to move seekbar based on the current position
     * of the song
     */
    protected Runnable moveSeekBarThread = new Runnable() {
        public void run() {
            if(mediaPlayer.isPlaying()){

                int mediaPos_new = mediaPlayer.getCurrentPosition();
                //int mediaMax_new = mediaPlayer.getDuration();
                //seekBar.setMax(mediaMax_new);
                //seekBar.setProgress(mediaPos_new);
                handler.postDelayed(this, 100); //Looping the thread after 0.1 second

                //int duration = mediaPlayer.getDuration();
                double newPosition = (double)( (double) mediaPos_new/song.getDuration()) * textVeiwHeight;
                scrollView.scrollTo(0, (int) newPosition - screenHeight/2);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll);
        context = this.context;

        textView = (TextView) findViewById(R.id.textView);
        titleView = (TextView) findViewById(R.id.textTitle);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenHeight = size.y;

        song = (Song) getIntent().getSerializableExtra("songscroller_song");

        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, "/Music/" + song.getArtist() + "-" + song.getTitle() + ".txt");
        //File bkground = new File(sdcard, "/Music/" + song.getArtist() + "-" + song.getTitle() + ".jpg");

        //Read text from file
        SpannableStringBuilder text = new SpannableStringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }

        //Find the view by its id
        text = formatText(text);
        titleView.setText(song.getTitle());
        textView.setText(text);
        textView.measure(0,0);
        textVeiwHeight = textView.getMeasuredHeight();

        //// set background

        //ivAlbumArt = (ImageView) findViewById(R.id.album_art);
        //Drawable albumimage = Drawable.createFromPath(song.getArt());
        //ivAlbumArt.setImageDrawable(albumimage);
        //Drawable bgimage = Drawable.createFromPath(bkground.getAbsolutePath());
        //bgimage.setAlpha(80);
        //scrollView.setBackgroundDrawable(bgimage);

        ivPlay = findViewById(R.id.ivPlay);
        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String songpath = song.getPath();
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    ivPlay.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    if (mediaPlayer == null) {
                        mediaPlayer = MediaPlayer.create(context, Uri.parse(songpath));
                    }

                    handler.removeCallbacks(moveSeekBarThread);
                    handler.postDelayed(moveSeekBarThread, 100); //cal the thread after 1000 milliseconds

                    mediaPlayer.start();
                    ivPlay.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });
    }

    private SpannableStringBuilder formatText(SpannableStringBuilder sb) {

        Matcher matcher = java.util.regex.Pattern.compile("\\[(.*?)\\]").matcher(sb.toString());
        while (matcher.find()) {
            final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(120, 169, 255));
            sb.setSpan(fcs, matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }

        matcher = java.util.regex.Pattern.compile("\\((.*?)\\)").matcher(sb.toString());
        while (matcher.find()) {
            final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(192, 192, 192));
            sb.setSpan(fcs, matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }

        matcher = java.util.regex.Pattern.compile("(\\(*[CDEFGAB](?:b|bb)*(?:#|##|add|sus|maj|min|aug|m|M|°|[0-9])*[\\(]?[\\d\\/-]*[\\)]?(?:[CDEFGAB](?:b|bb)*(?:#|##|add|sus|maj|min|aug|m|M|°|[0-9])*[\\d\\/]*)*\\)*)(?=[\\s|$])(?! [a-z])").matcher(sb.toString());
        while (matcher.find()) {
            final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(255, 0, 0));
            sb.setSpan(fcs, matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }

        return sb;
    }
}
