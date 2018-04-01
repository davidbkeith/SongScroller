package com.mobileapps.brad.songscroller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import android.support.constraint.ConstraintSet;
import android.support.constraint.ConstraintLayout;

public class ScrollActivity extends AppCompatActivity implements ScrollViewListener {

    private Context context;

    private int pause;
    private int currentScrollPos;
    private int Beats;

    private int newSeek;
    private int measuresPerLine;
    private int wrapLength;
    private int totLines;
    private long elapsedTime, songStartTime;
    private int beatPos;
    private int scrollSegmentPos;
    private int actualNumLines;
    private int textViewWidth;
    private int textViewHeight; /// total height of text window (NOT scroll window)
    private int scrollViewHeight;   /// height of scroll window

    private ImageView ivPlay;
    private ImageView ivNext;
    private ImageView ivPrevious;
    private ImageView ivPause;
    private ImageView ivAlbumArt;
    private ImageView ivMute;
    private ImageView ivBackground;
    private ImageView[] imageTapTempo;

    private TextView textView;
    private TextView textCountdown;

    public int getTextVeiwHeight() {
        return textViewHeight;
    }

    public int getScrollVeiwHeight() { return scrollViewHeight; }

    public int getTextViewWidth() {
        return textViewWidth;
    }

    public int getPause() {
        return pause;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public int getLinesPerPage () { return (int) (scrollViewHeight / scrollView.getLineHeight()); }

    public ImageView getIvPlay() {
        return ivPlay;
    }

    public ImageView getIvMute() {
        return ivMute;
    }

    public ScrollViewExt getScrollView() {
        return scrollView;
    }

    private ScrollViewExt scrollView;

    public TextView getTextView() {
        return textView;
    }

    public AutoScroll getAutoScroll() {
        return autoScroll;
    }

    private AutoScroll autoScroll;

    private android.os.Handler handler = new android.os.Handler();

    public Song getSong() {
        return song;
    }

    private Song song;

    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    static MediaPlayer mediaPlayer;
    static private Song playingSong;
    private boolean expand;

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
                elapsedTime = 0;
            }
            else {
                elapsedTime +=  100;
            }

            scrollView.invalidate ();
            handler.postDelayed(this, 100); //Looping the thread after 0.1 second
        }
    };

    @Override
    protected void onResume () {
        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.score_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(context, SongSettingsActivity.class);
                intent.putExtra("songscroller_scoredata", autoScroll.getScoreData());
                intent.putExtra("songscroller_title", String.format("Edit-%s", song.getTitle()));
                startActivityForResult(intent, 1);
                return true;
            case R.id.menu_line_options:
                intent = new Intent(context, SongLineSettingsActivity.class);
                intent.putExtra("songscroller_groupdata", autoScroll.getGroupArray().getCurrentGroup());
                intent.putExtra("songscroller_measures", autoScroll.getGroupArray().getLineMeasuresFromTotalMeasures(autoScroll.getProgress()));
                intent.putExtra("songscroller_title", String.format("Edit-%s", song.getTitle()));
                startActivityForResult(intent, 1);
                return true;
            case android.R.id.home:
                // click on 'up' button in the action bar, handle it here
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;
        newSeek = -1;
        expand = true;
        //startTime = System.currentTimeMillis();

        textView = (TextView) findViewById(R.id.textView);
        ivMute = (ImageView) findViewById(R.id.ivMute);
        ivBackground = (ImageView) findViewById(R.id.ivBackground);
        autoScroll = (AutoScroll) findViewById(R.id.seekBar);
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
                if (getAutoScroll().getProgress() == 0) {
                    //ScoreData scoreData = autoScroll.getScoreData();
                    Intent intent = new Intent(context, SongSettingsActivity.class);
                    intent.putExtra("songscroller_scoredata", autoScroll.getScoreData());
                    intent.putExtra("songscroller_title", String.format("Edit-%s", song.getTitle()));
                    startActivityForResult(intent, 1);
                }
                else {
                    Intent intent = new Intent(context, SongLineSettingsActivity.class);
                  //  intent.putExtra("songscroller_groupdata", autoScroll.getGroupArray().getCurrentGroup());
                  //  intent.putExtra("songscroller_measures", autoScroll.getGroupArray().getCurrentGroup());
                    intent.putExtra("songscroller_title", String.format("Edit-%s", song.getTitle()));
                    startActivityForResult(intent, 1);
                }
            }
            public void onSwipeLeft() {
                //Toast.makeText(ScrollActivity.this, "left", Toast.LENGTH_SHORT).show();

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

     //   final LinearLayout layout = (LinearLayout)findViewById(R.id.scrollView);
        ViewTreeObserver vto = scrollView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    scrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                //int width  = layout.getMeasuredWidth();
                scrollViewHeight = scrollView.getMeasuredHeight();

            }
        });

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
            } else if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
            // seekBar.setMax((int)song.getDuration());
        }

        File sdcard = Environment.getExternalStorageDirectory();
        //File file = new File(sdcard, "/Music/" + song.getArtist() + "-" + song.getTitle() + ".txt");
        File file = new File (song.getSheetMusicPath());

        //Read text from file
        //autoScroll = new AutoScrollCalculated(this, file);
        SpannableStringBuilder sb = autoScroll.initialize (this, file);
        /*if (!autoScroll.isValid()) {
            AutoScroll autoScrollOld = autoScroll;
            autoScroll = (AutoScrollGuess) findViewById(R.id.seekBarGuess);
            //autoScroll = new AutoScrollGuess(context, autoScroll);
            //autoScroll.setOnSeekBarChangeListener(autoScroll);
            autoScroll.initialize(this, autoScrollOld);
        }*/

        //SpannableStringBuilder text = autoScroll.formatText();
        textView.setText(sb);

        //final int actualNumLines;
        textView.post(new Runnable() {
            @Override
            public void run() {
               // actualNumLines = textView.getLineCount();
                autoScroll.setWrappedLines();
            }
        });

        //int textLength = text.length();
        totLines = autoScroll.getNumLines();

        //float calculatedLineHeight = textVeiwHeight/(float) totLines;
        findTextViewHeight();

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
        //getSupportActionBar().setTitle(String.format("%s-%s", song.getArtist(), song.getTitle()));

        ivPlay = findViewById(R.id.ivPlay);
        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (song.isPlaying()) {
                    song.pause();
                    ivPlay.setImageResource(android.R.drawable.ic_media_play);
                }
                else {
                    song.start();
                    ivPlay.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });
        ivNext = findViewById(R.id.ivNext);
        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                song.setStartPosition(song.getPosition()+getAutoScroll().getTimePerMeasure());
            }
        });
        ivPrevious = findViewById(R.id.ivPrevious);
        ivPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                song.setStartPosition(song.getPosition()-getAutoScroll().getTimePerMeasure());
            }
        });

        handler.removeCallbacks(moveSeekBarThread);
        handler.postDelayed(moveSeekBarThread, 100); //cal the thread after 100 milliseconds
    }

    public void findTextViewHeight () {
        textView.measure(0, 0);
        textViewHeight = textView.getMeasuredHeight();
        textViewWidth = textView.getMeasuredWidth();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), AlbumSongsActivity.class);
        setResult(Activity.RESULT_OK, intent);
        finishActivity(1);
        super.onBackPressed();
    }

    @Override
    public void onScrollChanged(ScrollViewExt scrollView, int x, int y, int oldx, int oldy) {}

    void expand () {
        ivBackground.setVisibility(expand ? View.GONE: View.VISIBLE);
        ConstraintSet constraintSet = new ConstraintSet();
        ConstraintLayout constraintLayout = new ConstraintLayout(scrollView.getContext());
        ActionBar actionBar = getSupportActionBar();

        if (expand) {
            constraintSet.connect(R.id.scrollView,ConstraintSet.BOTTOM,  R.id.scrollActivity, ConstraintSet.BOTTOM,0);
            actionBar.hide();
        }
        else {
            constraintSet.connect(R.id.scrollView,ConstraintSet.BOTTOM,  R.id.ivBackground, ConstraintSet.TOP,0);
            actionBar.show();
        }
        constraintSet.applyTo(constraintLayout);
        expand = !expand;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int avgTapSpeed = 0;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //Toast.makeText(ScrollActivity.this, "down", Toast.LENGTH_SHORT).show();
                break;

        }
        return super.onTouchEvent(ev);
    }
}
