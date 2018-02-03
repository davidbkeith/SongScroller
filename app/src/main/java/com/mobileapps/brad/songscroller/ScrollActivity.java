package com.mobileapps.brad.songscroller;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;

public class ScrollActivity extends AppCompatActivity implements ScrollViewListener {

    private Context context;
    private TextView textView;
    private ImageView ivPlay;
    private ImageView ivPause;
    private ImageView ivAlbumArt;
    private MediaPlayer mediaPlayer;
    private Song song;
    private android.os.Handler handler = new android.os.Handler();
    private ScrollViewExt scrollView;
    private double textVeiwHeight, offsetFraction, posOffset;
    private int screenHeight;
    private ArrayList<Point> chordPos;
    //private Boolean autoScroll;

    /**
     * The Move seek bar. Thread to move seekbar based on the current position
     * of the song
     */

    Runnable moveSeekBarThread = new Runnable() {
        public void run() {
            Log.e("Media", "%s: %s".format("<<<<enableScrolling value is:%s>>>>>>", scrollView.isEnableScrolling().toString()));
            if (mediaPlayer.isPlaying() && scrollView.isEnableScrolling()) {

                int mediaPos_new = mediaPlayer.getCurrentPosition();
                int mediaMax_new = mediaPlayer.getDuration();
                //seekBar.setMax(mediaMax_new);
                //seekBar.setProgress(mediaPos_new);

                //int duration = mediaPlayer.getDuration();
                double newPosition = (double) ((double) mediaPos_new / mediaMax_new) * textVeiwHeight;
                int scrollTo = (int) newPosition + (int) posOffset - (int) (offsetFraction * textVeiwHeight);


                if (scrollTo > (int) posOffset) {
                    scrollView.scrollTo(0, scrollTo);
                }

            }
            handler.postDelayed(this, 100); //Looping the thread after 0.1 second
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll);
        context = this.context;
        chordPos = new ArrayList<>();
        posOffset = 0;
        //autoScroll = true;

        textView = (TextView) findViewById(R.id.textView);
        //titleView = (TextView) findViewById(R.id.textTitle);
        scrollView = (ScrollViewExt) findViewById(R.id.scrollView);
        scrollView.setScrollViewListener(this);

       // scrollView.setScrollViewListener();

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
                //text = formatText(text);
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }

        //Find the view by its id
        text = formatText(text);
        textView.setText(text);
   //     int firstText = chordPos.get(0).y;
        int textLength = text.length();

  //      double percent = (double) firstText / (double) textLength;

        String beforeString = text.toString().substring(0, chordPos.get(0).x);
        int startLine = beforeString.split("\n").length;
        int totLines = text.toString().split("\n").length;
        offsetFraction = (double) startLine / (double) totLines;



        textView.measure(0,0);
        textVeiwHeight = textView.getMeasuredHeight();

        //// set title
        getSupportActionBar().setTitle(String.format("%s-%s", song.getArtist(), song.getTitle()));
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

               /*     posOffset = scrollView.getScrollY();
                    textVeiwHeight -= posOffset;
                    if (posOffset > 0) {
                        offsetFraction = 0.15;   /// scroll when music is farther down a bit
                    }*/

                    mediaPlayer.start();
                    ivPlay.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });
    }

    @Override
    public void onScrollChanged(ScrollViewExt scrollView, int x, int y, int oldx, int oldy) {
        // We take the last son in the scrollview
        View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
        //int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

        // if diff is zero, then the bottom has been reached
        //if (diff == 0) {
            posOffset = scrollView.getScrollY();
        //    textVeiwHeight -= posOffset;
        //    if (posOffset > 0) {
        //        offsetFraction = 0.15;   /// scroll when music is farther down a bit
        //    }
        //}
        //if (moveSeekBarThread == null) {
        //    createAutoScrollThread();
        //}
    }

    private SpannableStringBuilder formatText(SpannableStringBuilder sb) {

        Matcher matcher = java.util.regex.Pattern.compile("@!.+?\\n").matcher(sb.toString());
        int lastCommentEnd = 0;

        while (matcher.find()) {
            //final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(192, 192, 192));
            //sb.delete(matcher.start(), matcher.end());
            lastCommentEnd = matcher.end();
        }
        sb = new SpannableStringBuilder(sb.toString().substring(lastCommentEnd));

        matcher = java.util.regex.Pattern.compile("\\[(.*?)\\]").matcher(sb.toString());
        while (matcher.find()) {
            final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(120, 169, 255));
            sb.setSpan(fcs, matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }

        matcher = java.util.regex.Pattern.compile("\\((.*?)\\)").matcher(sb.toString());
        while (matcher.find()) {
            final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(192, 192, 192));
            sb.setSpan(fcs, matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }

        matcher = java.util.regex.Pattern.compile("(\\(*[CDEFGAB](?:b|bb)*(?:|#|##|add|sus|maj|min|aug|m|M|b|°|[0-9])*[\\(]?[\\d\\/-/+]*[\\)]?(?:[CDEFGAB](?:b|bb)*(?:#|##|add|sus|maj|min|aug|m|M|b|°|[0-9])*[\\d\\/]*)*\\)*)(?=[\\s|$])(?! [a-z])").matcher(sb.toString());
        while (matcher.find()) {
            final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(255, 0, 0));
            sb.setSpan(fcs, matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            chordPos.add (new Point(matcher.start(), matcher.end()));
        }

        return sb;
    }
}
