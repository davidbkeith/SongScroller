package com.mobileapps.brad.songscroller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScrollActivity extends AppCompatActivity implements ScrollViewListener {

    private Context context;

    //static public boolean isEditing;

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
    private int swipeCount;

    static private final int NOEDIT = 0;
    static private final int EDITLINE = 1;
    static private final int EDITGROUP = 2;
    static private final int EDITSONG = 3;

    static private int mode;
    static private int lineEditMode;

    private ImageView ivPlay;
    private ImageView ivNext;
    private ImageView ivPrevious;
    private ImageView ivPause;
    private ImageView ivAlbumArt;
    private ImageView ivMute;
    private ImageView ivBackground;
    private ImageView[] imageTapTempo;

    private View controls;
    private ViewGroup songSettingsContainer;
    private ViewGroup lineSettingsContainer;

    private TextView textView;
    private TextView textCountdown;

    private MenuItem editMode;

    static public boolean isEditing() {
        return isEditScore() || isEditSong() ;
    }

    static public boolean isEditScore() {
        return mode == EDITLINE || mode == EDITGROUP;
    }

    static public boolean isEditSong() {
        return mode == EDITSONG;
    }

    static public boolean isEditLine() {
        return mode == EDITLINE;
    }

    static public boolean isEditGroup() {
        return mode == EDITGROUP;
    }

    public SpannableStringBuilder getSb() {
        return sb;
    }

    public void setView () {
         textView.post(new Runnable() {
            @Override
            public void run() {
                // actualNumLines = textView.getLineCount();
                //autoScroll.setWrappedLines();
                if (songLineSettings != null) {
                    songLineSettings.refresh();
                }
                //songSettings.update();
            }
        });
        textView.setText(sb);

    }
    public void setSb(String score) {
       // this.sb = sb;
        this.sb = markSpans(sb.toString(), null);
        setView ();
    }

    public int getTotalLines () {
        return sb.toString().split("\n").length;
        //    return get(size()-1).getChordsLineNumber() + get(size()-1).getGroupLineCount() + get(size()-1).getWrappedLines();
    }

    private SpannableStringBuilder sb;

    private Toolbar toolbar;
    private Toolbar lineSettings;
    //private Toolbar songSettings;

    public SongLineSettings getSongLineSettings() {
        return songLineSettings;
    }
    private SongLineSettings songLineSettings;
    private SongSettings songSettings;

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

    public int getLinesPerPage () {
        Layout layout = getTextView().getLayout();
        if (layout != null) {
            return layout.getLineCount();
            //return (int) (scrollView.getHeight() / scrollView.getLineHeight());
        }
        return 0;
    }

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
            if (!isEditing()) {
                autoScroll.setSeekBarProgress();
            }
            textCountdown.setText(String.format("%d",autoScroll.getProgress()+1));
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
            case R.id.menu_edit:
                if (item.isChecked()) {
                    item.setChecked(false);
                    lineEditMode = EDITGROUP;
                    setMaxScroll(EDITGROUP);
                }
                else {
                    item.setChecked(true);
                    lineEditMode = EDITLINE;
                    setMaxScroll(EDITLINE);
                }
                return true;
            case R.id.menu_add_group:
                autoScroll.getGroupArray().duplicateGroup (-1);
                setMaxScroll(-1);
                return true;
            case R.id.menu_delete_group:
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle (R.string.menu_delete_group);
                adb.setIcon (android.R.drawable.ic_dialog_alert);

                adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        autoScroll.getGroupArray().deleteGroup(-1);
                        setMaxScroll(-1);
                    }
                });

                adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                adb.show();
                return true;
            case R.id.menu_back:
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
       // toolbar = (Toolbar) findViewById(R.id.app_bar);
        lineSettings = (Toolbar) findViewById(R.id.editSongLine);
        //songSettings = (Toolbar) findViewById(R.id.editSong);

        setSupportActionBar(lineSettings);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        songLineSettings = new SongLineSettings(this);
        songSettings = new SongSettings(this);

        //isEditing = true;
        context = this;
        newSeek = -1;
        expand = true;
        //startTime = System.currentTimeMillis();

        textView = (TextView) findViewById(R.id.textView);
        ivMute = (ImageView) findViewById(R.id.ivMute);
        ivPlay = (ImageView) findViewById(R.id.ivPlay);
        ivNext = (ImageView) findViewById(R.id.ivNext);
        ivPrevious = (ImageView) findViewById(R.id.ivPrevious);
        ivBackground = (ImageView) findViewById(R.id.ivBackground);
        autoScroll = (AutoScroll) findViewById(R.id.seekBar);
        textCountdown = (TextView) findViewById(R.id.textCountdown);
        scrollView = (ScrollViewExt) findViewById(R.id.scrollView);
        editMode = (MenuItem) findViewById(R.id.menu_edit);

        lineSettingsContainer = (ViewGroup) findViewById(R.id.lineSettingsContainer);
        songSettingsContainer = (ViewGroup) findViewById(R.id.songSettingsContainer);

       // controls = findViewById(R.id.controls);

        scrollView.setScrollViewListener(this);
        autoScroll.setOnSeekBarChangeListener(autoScroll);
        //mode = 0;
        lineEditMode = EDITGROUP;
        expand(NOEDIT);


        scrollView.setOnTouchListener(new OnSwipeTouchListener(ScrollActivity.this) {
            public void onSwipeTop() {
                //Toast.makeText(ScrollActivity.this, "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
                expand(Math.abs(++swipeCount % 3));
            }
            public void onSwipeLeft() {
                expand(Math.abs(--swipeCount % 3));
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
            //song.setDuration(mediaPlayer.getDuration());
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
        sb = new SpannableStringBuilder(autoScroll.create(this, file));
        song.setDuration(autoScroll.getSongDuration() * autoScroll.getBeatInterval());
        /*if (!autoScroll.isValid()) {
            AutoScroll autoScrollOld = autoScroll;
            autoScroll = (AutoScrollGuess) findViewById(R.id.seekBarGuess);
            //autoScroll = new AutoScrollGuess(context, autoScroll);
            //autoScroll.setOnSeekBarChangeListener(autoScroll);
            autoScroll.create(this, autoScrollOld);
        }*/

        //SpannableStringBuilder text = autoScroll.findChords();
        //textView.setText(sb);
        setSpans();

        //final int actualNumLines;
    /*    textView.post(new Runnable() {
            @Override
            public void run() {
               // actualNumLines = textView.getLineCount();
                autoScroll.setWrappedLines();
            }
        });*/

        //int textLength = text.length();
        //totLines = autoScroll.getNumLines();

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

        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (isEditScore()) {
                autoScroll.setProgress(autoScroll.getProgress() + 1);
            }
            else {
                song.setStartPosition(song.getPosition() + getAutoScroll().getTimePerBeat());
            }
            }
        });
        ivPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (autoScroll.getProgress() == 0){
                onBackPressed();
            }
            else {
                if (isEditScore()) {
                    autoScroll.setProgress(autoScroll.getProgress() - 1);
                } else {
                    song.setStartPosition(song.getPosition() - getAutoScroll().getTimePerBeat());
                }
            }
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

   void expand (int viewId) {
        ivBackground.setVisibility(expand ? View.GONE: View.VISIBLE);
        ActionBar actionBar = getSupportActionBar();

       if (viewId==0) {
           actionBar.hide();
           ivBackground.setVisibility(View.GONE);
           setMaxScroll(NOEDIT);
       }
       else {
           if (viewId == EDITLINE ) {
               songSettingsContainer.setVisibility(View.GONE);
               lineSettingsContainer.setVisibility(View.VISIBLE);
               setMaxScroll(lineEditMode);
           }
           else {
               lineSettingsContainer.setVisibility(View.GONE);
               songSettingsContainer.setVisibility(View.VISIBLE);
               setMaxScroll(EDITSONG);
           }
           actionBar.show ();
           ivBackground.setVisibility(View.VISIBLE);
       }
       //setMaxScroll(id);
   }

   public void setMaxScroll(int newmode) {
        //isEditing = false;
        mode = newmode == -1 ? mode : newmode;
        if (autoScroll.getGroupArray() != null) {
            if (mode == EDITLINE) {
                autoScroll.setMax(getTextView().getLineCount());
            } else if (mode == EDITGROUP) {
                autoScroll.setMax(autoScroll.getGroupArray().size()-1);
            } else {
                autoScroll.setMax(autoScroll.getGroupArray().getTotalBeats());
            }
        }
   }

    /*void showEditLine () {
        if (isEditing) {
            ivBackground.setVisibility(expand ? View.GONE: View.VISIBLE);
        }
        isEditing = !isEditing;
    }*/

   /* void expand () {
        controls.setVisibility(expand ? View.GONE: View.VISIBLE);
        ConstraintSet constraintSet = new ConstraintSet();
        ConstraintLayout constraintLayout = new ConstraintLayout(scrollView.getContext());
     //   constraintSet.clone(constraintLayout);
        ActionBar actionBar = getSupportActionBar();

        if (expand) {
            constraintSet.connect(R.id.textView,ConstraintSet.BOTTOM,  R.id.scrollActivity, ConstraintSet.BOTTOM,0);
            // constraintSet.connect(R.id.scrollView,ConstraintSet.TOP,  R.id.app_bar, ConstraintSet.TOP,0);
          //  constraintSet.connect(R.id.scrollView,ConstraintSet.BOTTOM,  R.id.ivBackground, ConstraintSet.BOTTOM,0);
           // constraintSet.connect(R.id.textView,ConstraintSet.BOTTOM,  R.id.scrollActivity, ConstraintSet.BOTTOM,0);
           // constraintSet.connect(R.id.controls,ConstraintSet.TOP,  R.id.scrollActivity, ConstraintSet.BOTTOM,0);
           // constraintSet.connect(R.id.ivBackground,ConstraintSet.TOP,  R.id.scrollActivity, ConstraintSet.BOTTOM,0);
            actionBar.hide();
            //controls.setVisibility(View.INVISIBLE);
           // controls.setAlpha((float) 0.1);
          //  ivBackground.setVisibility(View.INVISIBLE);
        }
        else {
          //  constraintSet.connect(R.id.scrollView,ConstraintSet.TOP,  R.id.app_bar, ConstraintSet.BOTTOM,0);
          //  constraintSet.connect(R.id.scrollView,ConstraintSet.BOTTOM,  R.id.ivBackground, ConstraintSet.TOP,0);
            actionBar.show();
          //  controls.setVisibility(View.VISIBLE);
            ivBackground.setVisibility(View.VISIBLE);
        }
        constraintSet.applyTo(constraintLayout);
        expand = !expand;
    }
*/
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

    public void markAllLineSpans (String line, GroupData gd, SpannableStringBuilder sb) {

    }

    public void markChordSpans (String chordline, GroupData gd, SpannableStringBuilder spannableStringBuilder) {
        Pattern possiblechord = java.util.regex.Pattern.compile("[^\\s]+");
        Pattern validChord = java.util.regex.Pattern.compile("[^x\\[\\]\\(\\)]+");
        Pattern repeat = java.util.regex.Pattern.compile(".*\\d[xX]");

        Matcher matcher = possiblechord.matcher(chordline);

        int chordCount = 0;
        int numrepeat = 1;
        while (matcher.find()) {
            String chord = chordline.substring(matcher.start(), matcher.end());
            if (validChord.matcher(chord).matches()) {
                chordCount++;
                final ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.songchords));
                if (gd.getBeats() != 0) {
                    spannableStringBuilder.setSpan(fcs, gd.getOffsetChords() + matcher.start(), gd.getOffsetChords() + matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }
            }
            else if (repeat.matcher(chord).matches()) {
                String[] multiple = chord.toLowerCase().split("x");
                if (multiple.length > 0) {
                    numrepeat = Integer.parseInt(multiple[0]);
                }
            }
        }
        if (gd.getBeats() != 0) {
            if (autoScroll.getGroupArray().getClass() == GroupArrayGuess.class) {
                gd.setBeats(chordCount * numrepeat);
            }
        }
    }

    public SpannableStringBuilder markSpans (String text, GroupData groupData) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        Pattern annotation = java.util.regex.Pattern.compile("\\[(.*?)\\]");
        Pattern linemod = java.util.regex.Pattern.compile("\\((.*?)\\)");

        Matcher matcher = annotation.matcher(text);
        while (matcher.find()) {
            final ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.songannotation));
            spannableStringBuilder.setSpan(fcs, matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }

        matcher = linemod.matcher(text);
        while (matcher.find()) {
            final ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.songlinemod));
            spannableStringBuilder.setSpan(fcs, matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }

        if (groupData == null) {
            String score = text;
            for (GroupData gd : autoScroll.getGroupArray()) {
                String chordline = gd.getLyrics(score);
                markChordSpans(chordline, gd, spannableStringBuilder);
            }
        }
        else {
            markChordSpans(text.split("\n")[0], groupData, spannableStringBuilder);
        }
        return spannableStringBuilder;
    }

    public void setSpans () {
        //cmarkSpans(sb.toString(), null);
        setSb(sb.toString());
        autoScroll.setMax(autoScroll.getSongDuration());
    }

    public String getLyrics () {
        String text = sb.toString();
        if (lineEditMode != EDITLINE) {
            int group = getAutoScroll().getGroupArray().getCurrentGroup();
            if (group != -1) {
                return getAutoScroll().getGroupArray().getText(group, text);
            }
            return "";
        }
        else {
            int lineSartPos = getScrollView().getChordsPos();
            if (lineSartPos != -1) {
                int endpos = text.indexOf("\n", lineSartPos);
                if (endpos != -1) {
                    return text.substring(lineSartPos, endpos);
                } else {
                    return text.substring(lineSartPos);
                }
            }
            return "";
        }
    }

    public void removeText (int start, int end) {
        if (end != -1) {
            sb.delete(start, end);
        }
        else {
            sb.delete(start, sb.length());
        }
        setView();
    }

    public void replaceText (int start, int end, String text) {
        if (end != -1) {
            sb.replace(start, end, text);
        }
        else {
            sb.insert(start, text);
        }
        setSb(sb.toString());
    }

    public void duplicateText (int start, int end) {
        String score = sb.toString();
        if (end != -1) {
            sb.insert(start, score.substring(start, end));
        }
        else {
            sb.insert(start, score.substring(start));
        }
        setSb(sb.toString());
    }
}
