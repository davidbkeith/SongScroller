package com.mobileapps.brad.songscroller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import android.support.v7.app.ActionBar;
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
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewTreeObserver;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import android.support.constraint.ConstraintSet;
import android.support.constraint.ConstraintLayout;

public class ScrollActivity extends AppCompatActivity implements ScrollViewListener {

    private Context context;

    public int getTextVeiwHeight() {
        return textViewHeight;
    }
    public int getScrollVeiwHeight() { return scrollViewHeight; }

    private int textViewHeight; /// total height of text window (NOT scroll window)
    private int scrollViewHeight;   /// height of scroll window

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

    //public float getActualLineHeight() {
    //    return actualLineHeight;
    //}

    public int getLinesPerPage () { return (int) (scrollViewHeight / scrollView.getLineHeight()); }

    //private float actualLineHeight;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;
        newSeek = -1;
        expand = true;
        //startTime = System.currentTimeMillis();

        textView = (TextView) findViewById(R.id.textView);
        ivMute = (ImageView) findViewById(R.id.ivMute);
        ivBackground = (ImageView) findViewById(R.id.ivBackground);
        autoScroll = (AutoScrollCalculated) findViewById(R.id.seekBar);
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
                ScoreData scoreData = autoScroll.getScoreData();
                Intent intent = new Intent(context, SongSettingsActivity.class);
                intent.putExtra("songscroller_scoredata", autoScroll.getScoreData());
                intent.putExtra("songscroller_title", String.format("Edit-%s", song.getTitle()));
                startActivityForResult (intent, 1);
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
            autoScroll.initialize(this, autoScrollOld);
        }

        SpannableStringBuilder text = formatText(new SpannableStringBuilder(autoScroll.getText()));
        textView.setText(text);

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
        getSupportActionBar().setTitle(String.format("%s-%s", song.getArtist(), song.getTitle()));

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

        handler.removeCallbacks(moveSeekBarThread);
        handler.postDelayed(moveSeekBarThread, 100); //cal the thread after 100 milliseconds
    }

    public void findTextViewHeight () {
        textView.measure(0, 0);
        textViewHeight = textView.getMeasuredHeight();
        textViewWidth = textView.getMeasuredWidth();
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
  //      autoScroll.onScrollChanged(scrollView, x, y, oldx, oldy);
       // if (y != oldy) {
       //     scrollView.setEnableScrolling(false);
        /*
        * y = (int) (autoScroll.getGroupArray().getScrollLine(measure) * actualLineHeight + posOffset * actualLineHeight);
        * progress = measure
        */
            //setProgress(groupArray.getStartOfLineMeasures((int)(y/scrollView.getLineHeight()) - posOffset));
            //setProgress((int)(((y/scrollView.getLineHeight()) - posOffset)/3));
            //scrollActivity.setNewSeek(groupArray.getStartOfLineMeasures(getProgress()) * scoreData.getBeats() * BeatInterval);
            //int line = (int) ((float)y/scrollActivity.getScrollView().getLineHeight());
            //float scrollLinePos = y - (posOffset + scoreData.getScrollStart() * 3) * scrollActivity.getScrollView().getLineHeight();

        /*  scrollline = progressline + lineoffset - startline;
        *   y = progressline + lineoffset - startline;
        *   progressline = y - lineoffset + startline;
        *
        * */

            //float progressline = y - (posOffset + scoreData.getScrollStart()*3) * scrollView.getLineHeight();
         //   long curprog = getProgress();
         //   int max = scrollView.getMaxScrollAmount();
         //   int scrH = scrollView.getMeasuredHeight();
         //   int sh2 = scrollActivity.getTextVeiwHeight();


       /*     float startPos = (autoScroll.getPosOffset() - autoScroll.getScoreData().getScrollStart() * 3) * scrollView.getLineHeight();
            float adjustedHeight = getTextVeiwHeight() - startPos;

            float adjustedY = y - startPos;
            //adjustedY/adjustedHeight = progress/getMax();
            int progress = (int) (adjustedY / adjustedHeight * autoScroll.getMax());
            song.setStartPosition(progress * autoScroll.getTimePerMeasure());
            //autoScroll.setProgress(progress);
            //textCountdown.setText(String.format("%d", progress));
           // scrollView.invalidate ();
            //int newLine = autoScroll.getGroupArray().getLine(progress);
            // if (newLine != scrollView.)


            //long progress = groupArray.getStartOfLineMeasures(line - posOffset + scoreData.getScrollStart()*3) * getTimePerMeasure();

            //y/scrollView.getMaxScrollAmount() = progress/getMax()
            //int progressY =  y - posOffset + scoreData.getScrollStart()*3;
            //long progress = (long) (((float) progressY/scrollView.getMaxScrollAmount()) * getMax());

            //line = line % 3 > 1 ? line + 1 : line;
            getSong().setStartPosition(progress * autoScroll.getTimePerMeasure());
            //scrollView.onScrollChanged (x, 0, oldx, oldy);
        }*/
    }

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

    private SpannableStringBuilder formatText(SpannableStringBuilder sb) {

        Matcher matcher = java.util.regex.Pattern.compile("\\[(.*?)\\]").matcher(sb.toString());
        while (matcher.find()) {
            final ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.songannotation));
            sb.setSpan(fcs, matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }

        matcher = java.util.regex.Pattern.compile("\\((.*?)\\)").matcher(sb.toString());
        while (matcher.find()) {
            final ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.songlinemod));
            sb.setSpan(fcs, matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }

        matcher = java.util.regex.Pattern.compile("(\\(*[CDEFGAB](?:b|bb)*(?:|#|##|add|sus|maj|min|aug|m|M|b|°|[0-9])*[\\(]?[\\d\\/-/+]*[\\)]?(?:[CDEFGAB](?:b|bb)*(?:#|##|add|sus|maj|min|aug|m|M|b|°|[0-9])*[\\d\\/]*)*\\)*)(?=[\\s|$])(?! [a-z])").matcher(sb.toString());
        while (matcher.find()) {
            if (autoScroll.isChordLine(matcher.start())) {
                final ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.songchords));
                sb.setSpan(fcs, matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                //chordPos.add(new Point(matcher.start(), matcher.end()));
            }
        }

        return sb;
    }
}
