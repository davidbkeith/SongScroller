package com.mobileapps.brad.songscroller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DialogTitle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

public class ScrollActivity extends AppCompatActivity implements ScrollViewListener {

    private Context context;

    public int getTextVeiwHeight() {
        return textViewHeight;
    }

    private int textViewHeight;

    public int getTextViewWidth() {
        return textViewWidth;
    }

    private int textViewWidth;

    public int getPause() {
        return pause;
    }

    private int pause;
    private int currentScrollPos;
    //private int BeatInterval;
    private int Beats;

    public void setNewSeek(int newSeek) {
        this.newSeek = newSeek;
    }

    private int newSeek;
    private int measuresPerLine;
    private int wrapLength;
    private int totLines;

    private int beatPos;
    private int scrollSegmentPos;

    private int actualNumLines;

    public long getElapsedTime() {
        return elapsedTime;
    }

    private long elapsedTime, songStartTime;

    public float getActualLineHeight() {
        return actualLineHeight;
    }

    private float actualLineHeight;

    public ImageView getIvPlay() {
        return ivPlay;
    }

    private ImageView ivPlay;
    private ImageView ivPause;
    private ImageView ivAlbumArt;

    public ImageView getIvMute() {
        return ivMute;
    }

    private ImageView ivMute;
    private ImageView ivBackground;
    private ImageView[] imageTapTempo;

    public ScrollViewExt getScrollView() {
        return scrollView;
    }

    private ScrollViewExt scrollView;

    public TextView getTextView() {
        return textView;
    }

    private TextView textView;
    private TextView textCountdown;

    public AutoScroll getAutoScroll() {
        return autoScroll;
    }

    private AutoScroll autoScroll;
    GestureDetector gestureDetector;

    private android.os.Handler handler = new android.os.Handler();
    //private SeekBar seekBar;

    public Song getSong() {
        return song;
    }

    private Song song;

    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    static MediaPlayer mediaPlayer;
    static private Song playingSong;

    boolean isPlaying () {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }
    /**
     * The Move seek bar. Thread to move seekbar based on the current position
     * of the song
     */

    Runnable moveSeekBarThread = new Runnable() {
        public void run() {
            autoScroll.setSeekBarProgress();
            textCountdown.setText(String.format("%d",autoScroll.getProgress()));

            if (isPlaying() && scrollView.isEnableScrolling()) {
                //int currentPos;
               /* if (newSeek != -1) {
                    mediaPlayer.seekTo(newSeek);
                    song.seekTo (newSeek);
                    ///currentPos = newSeek;
                    newSeek = -1;
                }*/
                //else {
                    /// set seekbar position to match song progress
               // autoScroll.setSongProgress();
               // }
              /*  else {
                    currentPos = mediaPlayer.getCurrentPosition();
                }*/

                elapsedTime = 0;
           }
            else {
                elapsedTime +=  100;
                //elapsedTime = 100 * autoScroll.getBeatInterval() == elapsedTime ? 0 : elapsedTime;
            }

            scrollView.invalidate ();
            handler.postDelayed(this, 100); //Looping the thread after 0.1 second
        }
    };

    private void updateView() {
      //  int timeLeft = (int) (song.getDuration() - currentSongPos);
      //  long minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeft) % TimeUnit.HOURS.toMinutes(1);
      //  long seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeft) % TimeUnit.MINUTES.toSeconds(1);

     //   int scrollSegmentPos;

     //   int measure = 0;
     /*   if (autoScroll.isValid()) {
            scrollView.setScrollLinePos((int) ((autoScroll.getScrollLine(measure) + posOffset)));
            currentScrollPos = (int) (scrollView.getScrollLinePos() * scrollView.getLineHeight());
            textCountdown.setText(String.format("%d",measure));

            //scrollSegmentPos = getActualScrollPos(measure);
            //currentScrollPos = currentScrollPos + (scrollView.getScrollLinePos() - currentScrollPos)/5;  //// allows smooth transition
        }
        else {
            currentScrollPos = ((int) (((currentSongPos-pause) /(double) song.getDuration()) * textViewHeight  + posOffset));
        }
        ///Log.d("Scrollpos", Integer.toString(scrollSegmentPos));

       // textCountdown.setText(String.format("%d", currentScrollPos));
       // if (scrollSegmentPos != partialScrollPos) {
        scrollView.scrollTo(0, currentScrollPos);
        scrollView.invalidate();*/
    }

    @Override
    protected void onResume () {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;
        newSeek = -1;
        //startTime = System.currentTimeMillis();

        textView = (TextView) findViewById(R.id.textView);
        ivMute = (ImageView) findViewById(R.id.ivMute);
        ivBackground = (ImageView) findViewById(R.id.ivBackground);
        autoScroll = (AutoScrollCalculated) findViewById(R.id.seekBar);

        //autoScroll.setOnSeekBarChangeListener(autoScroll);

        textCountdown = (TextView) findViewById(R.id.textCountdown);
        scrollView = (ScrollViewExt) findViewById(R.id.scrollView);

        scrollView.setScrollViewListener(this);
        autoScroll.setOnSeekBarChangeListener(autoScroll);

        scrollView.setOnTouchListener(new OnSwipeTouchListener(ScrollActivity.this) {
            public void onSwipeTop() {
                //Toast.makeText(ScrollActivity.this, "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
                //Toast.makeText(ScrollActivity.this, "right", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeLeft() {
                //Toast.makeText(ScrollActivity.this, "left", Toast.LENGTH_SHORT).show();
                ScoreData scoreData = autoScroll.getScoreData();
                Intent intent = new Intent(context, SongSettingsActivity.class);
                intent.putExtra("songscroller_scoredata", autoScroll.getScoreData());
                intent.putExtra("songscroller_title", String.format("Edit-%s", song.getTitle()));
                startActivityForResult (intent, 1);
            }
            public void onSwipeBottom() {
                //Toast.makeText(ScrollActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }
        });

       /* getSupportActionBar().getCustomView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScoreData scoreData = autoScroll.getScoreData();
                Intent intent = new Intent(context, SongSettingsActivity.class);
                //intent.putExtra("songscroller_scoredata", scoreData);
                startActivity (intent);
            }
        });*/

        /*ViewTreeObserver vto = ivBackground.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                ivBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });*/

        song = (Song) getIntent().getSerializableExtra("songscroller_song");
        /*if (song.getArt() == null) {
            File albumFile = new File(song.getPath());
            File artFile = FindFile.findFileWithExt(albumFile.getParentFile(), null, ".jsp");
            if (artFile.exists()) {
                Uri artworkUri = Uri.parse("content://media/external/albumart");
                ContentValues contentValues = new ContentValues();
                contentValues.put("album_id", song.getAlbumId());
                contentValues.put("_data", artFile.getAbsolutePath());
                getContentResolver().insert (artworkUri, contentValues);
            }
        }*/

        File sdcard = Environment.getExternalStorageDirectory();
        //File file = new File(sdcard, "/Music/" + song.getArtist() + "-" + song.getTitle() + ".txt");
        File file = new File (song.getSheetMusicPath());

        //Read text from file
        //autoScroll = new AutoScrollCalculated(this, file);
        autoScroll.initialize (this, file);
        if (!autoScroll.isValid()) {
            AutoScroll autoScrollOld = autoScroll;
            autoScroll = (AutoScrollGuess) findViewById(R.id.seekBarGuess);
            //autoScroll = new AutoScrollGuess(context, autoScroll);
            //autoScroll.setOnSeekBarChangeListener(autoScroll);
            autoScroll.initialize(autoScrollOld);
        }
        SpannableStringBuilder text = formatText(new SpannableStringBuilder(autoScroll.getText()));
        textView.setText(text);

        //final int actualNumLines;
        textView.post(new Runnable() {
            @Override
            public void run() {
               // actualNumLines = textView.getLineCount();
                autoScroll.setPriorWrappedLines();
            }
        });

        //int textLength = text.length();
        totLines = autoScroll.getNumLines();

        //float calculatedLineHeight = textVeiwHeight/(float) totLines;
        textView.measure(0, 0);
        textViewHeight = textView.getMeasuredHeight();
        textViewWidth = textView.getMeasuredWidth();

      /*  textView.setOnTouchListener(new OnSwipeTouchListener(ScrollActivity.this) {
            @Override
            public void onSwipeLeft() {
                // Whatever
                ScoreData scoreData = autoScroll.getScoreData();
                Intent intent = new Intent(context, SongSettingsActivity.class);
                //intent.putExtra("songscroller_scoredata", scoreData);
                startActivity (intent);
                //Intent i = new Intent(SongSettingsActivity.this,ScoreData.class);
                //startActivity(i);
            }
        });*/

        float actualLineHeight = textView.getPaint().getFontMetrics().bottom - textView.getPaint().getFontMetrics().top;
        actualLineHeight -= actualLineHeight / 9;
        scrollView.setLineHeight(actualLineHeight);

        //// set title
        getSupportActionBar().setTitle(String.format("%s-%s", song.getArtist(), song.getTitle()));

        ivPlay = findViewById(R.id.ivPlay);
        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    song.pause();
                    ivPlay.setImageResource(android.R.drawable.ic_media_play);
                }
                else {
                    song.start();
                    ivPlay.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });

        if (mediaPlayer == null && song.getPath() != null) {
            mediaPlayer = MediaPlayer.create(context, Uri.parse(song.getPath()));
            song.setDuration(mediaPlayer.getDuration());
            playingSong = song;
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    ivPlay.setImageResource(android.R.drawable.ic_media_play);
                    autoScroll.setProgress(0);
                }
            });
            //textVeiwHeight -= posOffset;
        }
        else if (song != null) {
            if (song.equals(playingSong)) {
                ivPlay.setImageResource(android.R.drawable.ic_media_pause);
            } else {
                mediaPlayer.release();
                mediaPlayer = null;
            }
           // seekBar.setMax((int)song.getDuration());
        }

        handler.removeCallbacks(moveSeekBarThread);
        handler.postDelayed(moveSeekBarThread, 100); //cal the thread after 100 milliseconds
    }

   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // click on 'up' button in the action bar, handle it here
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), AlbumSongsActivity.class);
        setResult(Activity.RESULT_OK, intent);
        finishActivity(1);
        super.onBackPressed();
    }

    @Override
    public void onScrollChanged(ScrollViewExt scrollView, int x, int y, int oldx, int oldy) {
        autoScroll.onScrollChanged(scrollView, x, y, oldx, oldy);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int avgTapSpeed = 0;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                int tapSpeed = (int) scrollView.getAvgTapSpeed();
                if (tapSpeed > 0) {
                    autoScroll.setBeatInterval(tapSpeed);
                   // String bpmtext = String.format("%d", (60000 / BeatInterval));
                   // textEditBPM.setText(bpmtext);
                }
                else {

                }
        }
        return super.onTouchEvent(ev);
    }

    private SpannableStringBuilder formatText(SpannableStringBuilder sb) {

        Matcher matcher = java.util.regex.Pattern.compile("@!.+?\\n").matcher(sb.toString());
        String next;

        String noMetadata = sb.toString();
        while (matcher.find()) {
            next = matcher.group(0);
            String[] data = next.split("bpm");
            if (data.length == 2) {
                ScoreData scoreData = new ScoreData();
                scoreData.setBpm((int) Double.parseDouble(data[1]));
                autoScroll.setScoreData(scoreData);
                autoScroll.setBeatInterval((int) (60000 / Double.parseDouble(data[1])));
            }
            noMetadata = noMetadata.replace(next, "");
        }

        sb = new SpannableStringBuilder(noMetadata);

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
            if (autoScroll.isChordLine(matcher.start())) {
                final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(255, 0, 0));
                sb.setSpan(fcs, matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                //chordPos.add(new Point(matcher.start(), matcher.end()));
            }
        }

        return sb;
    }
}
