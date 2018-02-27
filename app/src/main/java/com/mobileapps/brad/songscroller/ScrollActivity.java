package com.mobileapps.brad.songscroller;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;

import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

public class ScrollActivity extends AppCompatActivity implements ScrollViewListener {

    private Context context;

    private int textVeiwHeight, posOffset;
    private int pause;
    private int currentScrollPos;
    //private int BeatInterval;
    private int Beats;
    private int newSeek;
    private int measuresPerLine;
    private int wrapLength;
    private int totLines;
    private int measure, beatPos;
    private long elapsedTime;

    private float actualLineHeight;

    private ImageView ivPlay;
    private ImageView ivPause;
    private ImageView ivAlbumArt;
    private ImageView ivMute;
    private ImageView ivBackground;
    private ImageView imageTapTempo, imageTapTempo2, imageTapTempo3, imageTapTempo4;
    private ScrollViewExt scrollView;
    private TextView textView;
    private TextView textCountdown;
    private AutoScroll autoScroll;

    private android.os.Handler handler = new android.os.Handler();
    private SeekBar seekBar;
    private Song song;

    static MediaPlayer mediaPlayer;
    static private Song playingSong;

    /**
     * The Move seek bar. Thread to move seekbar based on the current position
     * of the song
     */

    Runnable moveSeekBarThread = new Runnable() {
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying() && scrollView.isEnableScrolling()) {
                int currentPos;
                if (newSeek != -1) {
                    mediaPlayer.seekTo(newSeek);
                    currentPos = newSeek;
                    newSeek = -1;
                }
                else {
                    currentPos = mediaPlayer.getCurrentPosition();
                }
                seekBar.setProgress(currentPos);
                //handler.postDelayed(this, 100); //Looping the thread after 0.1 second
            }

            if (autoScroll.getBeatInterval() > 0) {
                elapsedTime +=  100;
                elapsedTime = 100 * autoScroll.getBeatInterval() == elapsedTime ? 0 : elapsedTime;
                int beatpos = mediaPlayer != null && mediaPlayer.isPlaying() ? measure : (int) elapsedTime / autoScroll.getBeatInterval();
               // textCountdown.setText(String.format("%d",elapsedTime));

                switch (beatpos % 4) {
                    case 0:
              //          if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        imageTapTempo4.setBackgroundColor(getResources().getColor(R.color.colorAccentDark));
              //          }
                        imageTapTempo3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        imageTapTempo2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        imageTapTempo.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        break;
                    case 1:
                        imageTapTempo3.setBackgroundColor(getResources().getColor(R.color.colorAccentDark));
                        break;
                    case 2:
                        imageTapTempo2.setBackgroundColor(getResources().getColor(R.color.colorAccentDark));
                        break;
                    case 3:
                        imageTapTempo.setBackgroundColor(getResources().getColor(R.color.colorAccentDark));
                        break;
                }
            }

            handler.postDelayed(this, 100); //Looping the thread after 0.1 second
        }
    };

    private void updateView(int currentSongPos) {
      //  int timeLeft = (int) (song.getDuration() - currentSongPos);
      //  long minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeft) % TimeUnit.HOURS.toMinutes(1);
      //  long seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeft) % TimeUnit.MINUTES.toSeconds(1);

        int scrollSegmentPos;

     //   int measure = 0;
        if (autoScroll.isValid()) {
            scrollSegmentPos = (int) (autoScroll.getGroupArray().getScrollLine(measure) * actualLineHeight  + posOffset);
            textCountdown.setText(String.format("%d",measure));
            //scrollSegmentPos = getActualScrollPos(measure);
        }
        else {
            scrollSegmentPos = (int) (((currentSongPos-pause) /(double) song.getDuration()) * textVeiwHeight  + posOffset);
        }
        ///Log.d("Scrollpos", Integer.toString(scrollSegmentPos));

        //currentScrollPos = currentScrollPos + (scrollSegmentPos - currentScrollPos)/20;  //// allows smooth transition
       // textCountdown.setText(String.format("%d", currentScrollPos));
        //if (partialScrollPos > offset && scrollSegmentPos != partialScrollPos) {
        scrollView.scrollTo(0, scrollSegmentPos);
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

        context = this.context;
        newSeek = -1;
        //startTime = System.currentTimeMillis();

        textView = (TextView) findViewById(R.id.textView);
        imageTapTempo = (ImageView) findViewById(R.id.imageTapTempo);
        imageTapTempo2 = (ImageView) findViewById(R.id.imageTapTempo2);
        imageTapTempo3 = (ImageView) findViewById(R.id.imageTapTempo3);
        imageTapTempo4 = (ImageView) findViewById(R.id.imageTapTempo4);
        ivMute = (ImageView) findViewById(R.id.ivMute);
        ivBackground = (ImageView) findViewById(R.id.ivBackground);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        textCountdown = (TextView) findViewById(R.id.textCountdown);

        scrollView = (ScrollViewExt) findViewById(R.id.scrollView);
        scrollView.setScrollViewListener(this);

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
        File file = new File(sdcard, "/Music/" + song.getArtist() + "-" + song.getTitle() + ".txt");

        //Read text from file
        autoScroll = new AutoScroll(this, file);
        SpannableStringBuilder text = formatText(new SpannableStringBuilder(autoScroll.getText()));
        textView.setText(text);

        //int textLength = text.length();
        totLines = autoScroll.getNumLines();

        //float calculatedLineHeight = textVeiwHeight/(float) totLines;
        textView.measure(0, 0);
        textVeiwHeight = textView.getMeasuredHeight();
        actualLineHeight = textView.getPaint().getFontMetrics().bottom - textView.getPaint().getFontMetrics().top;

        int actualNumLines = (int) (textVeiwHeight / actualLineHeight);

        if (autoScroll.isValid()) {
            autoScroll.getGroupArray().setPriorWrappedLines(totLines - actualNumLines);
        }
        actualLineHeight -= actualLineHeight / 9;

        //// set title
        getSupportActionBar().setTitle(String.format("%s-%s", song.getArtist(), song.getTitle()));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //mediaPlayer.seekTo(seekBar.getProgress());
                int newPos = seekBar.getProgress();
                int timeLeft = seekBar.getMax() - newPos;
                long minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeft) % TimeUnit.HOURS.toMinutes(1);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(timeLeft) % TimeUnit.MINUTES.toSeconds(1);
               // textCountdown.setText(String.format("%d:%02d", minutes, seconds));

                //beatPos = newPos / autoScroll.getBeatInterval();
                measure = newPos / (autoScroll.getBeatInterval() * autoScroll.getScoreData().getBeats());
                updateView (newPos);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (progress == song.getDuration()) {
                    mediaPlayer.pause();
                    ivPlay.setImageResource(android.R.drawable.ic_media_play);
                }
                else {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        ivPlay = findViewById(R.id.ivPlay);
        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String songpath = song.getPath();
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        ivPlay.setImageResource(android.R.drawable.ic_media_play);
                    }
                    else {
                        //songStartHeight = textVeiwHeight - posOffset;
                        mediaPlayer.start();
                        ivPlay.setImageResource(android.R.drawable.ic_media_pause);
                    }
                } else {
                    if (mediaPlayer == null) {
                        mediaPlayer = MediaPlayer.create(context, Uri.parse(songpath));
                        song.setDuration(mediaPlayer.getDuration());
                      //  estBPM = (mediaPlayer.getDuration()/playLines)/16;
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                ivPlay.setImageResource(android.R.drawable.ic_media_play);
                                seekBar.setProgress(0);
                                //handler.removeCallbacks(moveSeekBarThread);

                                imageTapTempo.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                imageTapTempo2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                imageTapTempo3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                imageTapTempo4.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            }
                        });
                        //textVeiwHeight -= posOffset;
                    }

                    mediaPlayer.start();
                    ivPlay.setImageResource(android.R.drawable.ic_media_pause);
                    seekBar.setMax((int)song.getDuration());
                    playingSong = song;
                   // songStartHeight = textVeiwHeight - posOffset;
                }
            }
        });

        if (mediaPlayer != null && song != null) {
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
        // We take the last son in the scrollview

        if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
            posOffset = y;
     //       textVeiwHeight -= posOffset;
        }
        else {
            int mediaPos_new = mediaPlayer.getCurrentPosition();
            if (autoScroll.isValid()) {
            /*
            * y = (int) (autoScroll.getGroupArray().getScrollLine(measure / 4) * actualLineHeight + posOffset);
            * */
                posOffset = (int) (y -  (int) (autoScroll.getGroupArray().getScrollLine(measure) * actualLineHeight));

            }
            else {
                if (posOffset == posOffset || ivMute.isActivated()) {
                    ///////// how it is calulated for moving scroller
                    //scrollY = calculatedPos + posOffet
                    //posOffest = scrollY - calculatedPos;
                    ///////// this code allows user to shift position of vertical scroll while mp3 is playing
                    posOffset = y - ((int) (((double) (mediaPos_new - pause) / song.getDuration()) * textVeiwHeight));
                } else {
                    ///////// how it is calulated for new song seek position
                    //scrollY = calculatedPos + posOffet (from that equation)
                    ///////// this code sets the mp3 current position to match the vertical scroll position
                    newSeek = (int) ((song.getDuration() * (y - (posOffset)) / (double) textVeiwHeight) + pause);
                }
            }
        }
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
        //int lastCommentEnd = 0;
        String next;

        String noMetadata = sb.toString();
        while (matcher.find()) {
            next = matcher.group(0);
            String[] data = next.split("bpm");
            if (data.length == 2) {
                autoScroll.setBeatInterval((int) (60000 / Double.parseDouble(data[1])));
               // textEditBPM.setText(data[1]);
                //continue;
            }
            //lastCommentEnd = matcher.end();
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
            if (autoScroll.getScoreData() == null || autoScroll.getGroupArray().isChordLine(matcher.start())) {
                final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(255, 0, 0));
                sb.setSpan(fcs, matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                //chordPos.add(new Point(matcher.start(), matcher.end()));
            }
        }

        return sb;
    }
}
