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
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.NodeList;

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
    private android.os.Handler tempoHandler = new android.os.Handler();
    private ScrollViewExt scrollView;
    private int textVeiwHeight, posOffset, calculatedPos;
    private double offsetFraction;
    private int screenHeight;
    private ArrayList<Point> chordPos;
    private double playRate;
    private double elapsedTime;
    private int tempoIndicator;
    private int bpmShow;
    private ImageView imageTapTempo, imageTapTempo2, imageTapTempo3, imageTapTempo4;
    private int BeatInterval;
    private int Beats;
    private EditText textEditBPM;
    private ArrayList<String> songData;

    /**
     * The Move seek bar. Thread to move seekbar based on the current position
     * of the song
     */

    Runnable moveSeekBarThread = new Runnable() {
        public void run() {
            int mediaPos_new;
            if (mediaPlayer.isPlaying() && scrollView.isEnableScrolling()) {

                mediaPos_new = mediaPlayer.getCurrentPosition();
                int mediaMax_new = mediaPlayer.getDuration();

                int calculatedPos = (int) ((double) ((double) mediaPos_new / mediaMax_new) * textVeiwHeight) + posOffset - (int) (offsetFraction * textVeiwHeight);
                if (calculatedPos > posOffset) {
                    scrollView.scrollTo(0, calculatedPos);
                }

                int measure = mediaPos_new / BeatInterval;
                switch (measure % 4) {
                    case 0:
                        imageTapTempo.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        imageTapTempo2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        imageTapTempo3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        imageTapTempo4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        break;
                    case 1:
                        imageTapTempo.setBackgroundColor(getResources().getColor(R.color.colorAccentDark));
                        break;
                    case 2:
                        imageTapTempo2.setBackgroundColor(getResources().getColor(R.color.colorAccentDark));
                        break;
                    case 3:
                        imageTapTempo3.setBackgroundColor(getResources().getColor(R.color.colorAccentDark));
                        break;

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
        tempoIndicator = 0;
        bpmShow = 0;


        textView = (TextView) findViewById(R.id.textView);
        imageTapTempo = (ImageView) findViewById(R.id.imageTapTempo);
        imageTapTempo2 = (ImageView) findViewById(R.id.imageTapTempo2);
        imageTapTempo3 = (ImageView) findViewById(R.id.imageTapTempo3);
        imageTapTempo4 = (ImageView) findViewById(R.id.imageTapTempo4);

        textEditBPM = (EditText) findViewById(R.id.textEdit);

        //titleView = (TextView) findViewById(R.id.textTitle);
        scrollView = (ScrollViewExt) findViewById(R.id.scrollView);
        scrollView.setScrollViewListener(this);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenHeight = size.y;

        song = (Song) getIntent().getSerializableExtra("songscroller_song");

        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, "/Music/" + song.getArtist() + "-" + song.getTitle() + ".txt");

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
        textView.setText(text);
        int textLength = text.length();

        String beforeString = text.toString().substring(0, chordPos.get(0).x);
        int startLine = beforeString.split("\n").length;
        final int totLines = text.toString().split("\n").length;
        final int playLines = text.toString().substring(chordPos.get(0).x).split("\n").length;
        offsetFraction = (double) startLine / (double) totLines;

        textView.measure(0,0);
        textVeiwHeight = textView.getMeasuredHeight();

        //// set title
        getSupportActionBar().setTitle(String.format("%s-%s", song.getArtist(), song.getTitle()));

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
                      //  estBPM = (mediaPlayer.getDuration()/playLines)/16;
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                ivPlay.setImageResource(android.R.drawable.ic_media_play);
                                handler.removeCallbacks(moveSeekBarThread);

                                imageTapTempo.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                imageTapTempo2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                imageTapTempo3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                imageTapTempo4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            }
                        });
                    }


                    mediaPlayer.start();
                    ivPlay.setImageResource(android.R.drawable.ic_media_pause);

                    handler.removeCallbacks(moveSeekBarThread);
                    handler.postDelayed(moveSeekBarThread, 100); //cal the thread after 100 milliseconds
                }
            }
        });


        textEditBPM.addTextChangedListener(new TextWatcher () {

            @Override
            public void afterTextChanged (Editable s) {
                BeatInterval = (int) (60000 / Double.parseDouble(s.toString()));
            }
            @Override
            public void beforeTextChanged (CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged (CharSequence s, int start, int before, int count){
                if (!s.equals("")) {

                }
            }
        });
    }


    @Override
    public void onScrollChanged(ScrollViewExt scrollView, int x, int y, int oldx, int oldy) {
        // We take the last son in the scrollview
        View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);

        if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
            posOffset = scrollView.getScrollY();

        }
        else {
            int mediaPos_new = mediaPlayer.getCurrentPosition();
            int mediaMax_new = mediaPlayer.getDuration();

            ///////// how it is calulated for moving scroller
            //scrollY = calculatedPos + posOffet
            //posOffest = scrollY - calculatedPos;
            posOffset = scrollView.getScrollY() - ((int) ((double) ((double) mediaPos_new / mediaMax_new) * textVeiwHeight)- (int) (offsetFraction * textVeiwHeight)) ;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int avgTapSpeed = 0;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                int tapSpeed = (int) scrollView.getAvgTapSpeed();
                if (tapSpeed > 0) {
                    BeatInterval = tapSpeed;
                    String bpmtext = String.format("%d", (60000 / BeatInterval));
                    textEditBPM.setText(bpmtext);
                }
        }
        return super.onTouchEvent(ev);
    }



    private SpannableStringBuilder formatText(SpannableStringBuilder sb) {

        Matcher matcher = java.util.regex.Pattern.compile("(.+?)songdata>").matcher(sb.toString());
        String songText;
        if (matcher.find()) {
            String xml = matcher.group(0);
            try {
                XMLParser xmlParser = new XMLParser(xml);
                BeatInterval = Integer.parseInt(xmlParser.getSingleItem("bpm"));
                Beats = Integer.parseInt(xmlParser.getSingleItem("beats"));
                songText = xmlParser.getSingleItem("song");
                if (songText.length() > 0) {
                    sb = new SpannableStringBuilder(songText);
                }
            }
            catch (Exception e) {
                Log.e("Error", e.toString());
            }
        }

        matcher = java.util.regex.Pattern.compile("@!.+?\\n").matcher(sb.toString());
        int lastCommentEnd = 0;
        String next;

        while (matcher.find()) {
            next = matcher.group(0);
            String[] data = next.split("bpm");
            if (data.length == 2) {
                BeatInterval = (int) (60000 / Double.parseDouble(data[1]));
                textEditBPM.setText(data[1]);
                continue;
            }
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
