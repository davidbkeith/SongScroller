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
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScrollActivity extends AppCompatActivity implements ScrollViewListener {

    private Context context;

    //static public boolean isEditing;

    private int pause;
    private int currentScrollPos;
    private int Beats;
    private int lastSongPos;


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
    protected double scrollSensitivity;  /// how much is scrolled per finger movement


    static private final int NOEDIT = 0;
    static private final int EDITLINE = 1;
    static private final int EDITGROUP = 2;
    static private final int EDITSONG = 3;

    private File sheetMusicFile;

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

   // private MenuItem editLine;
   // private MenuItem duplicateGroup;
   // private MenuItem deleteGroup;

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

    public void setView (final boolean getPositions) {
         textView.post(new Runnable() {
            @Override
            public void run() {
                // actualNumLines = textView.getLineCount();
                //autoScroll.setWrappedLines();
                if (songLineSettings != null) {
                    songLineSettings.refresh();
                }

                if (getPositions) {
                    autoScroll.getGroupArray().setPositions();
                    setSb();
                }
                //songSettings.update();
            }
        });
        textView.setText(sb);

    }
    public void setView(String text, boolean getPositions) {
        this.sb = new SpannableStringBuilder(text);
        setView (getPositions);
    }

    public void setSb() {
       // this.sb = sb;
        this.sb = markSpans(sb.toString(), null);
        setView (false);
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

    public int getFirstVisibleLine () {
        int scrollY   = getScrollView().getScrollY();
        Layout layout = getTextView().getLayout();
        return layout.getLineForVertical(scrollY);
    }

    public int getLastVisibleLine () {
        int height    = getScrollView().getHeight();
        int scrollY   = getScrollView().getScrollY();
        Layout layout = getTextView().getLayout();
        return layout.getLineForVertical(scrollY+height);
    }

    public int getLinesPerPage () {
        int height    = getScrollView().getHeight();
        //int scrollY   = getTextView().getScrollY();
        Layout layout = getTextView().getLayout();

        //int firstVisibleLineNumber = layout.getLineForVertical(0);
        return layout.getLineForVertical(height);

       /* Layout layout = getTextView().getLayout();
        if (layout != null) {
            return layout.getLineCount();
            //return (int) (scrollView.getHeight() / scrollView.getLineHeight());
        }*/
        //return lastVisibleLineNumber;
    }

    public int getTotalLines () {
        Layout layout = getTextView().getLayout();
        if (layout != null) {
            return layout.getLineCount();
            //return (int) (scrollView.getHeight() / scrollView.getLineHeight());
        }
        return 0;
        //return sb.toString().split("\n").length;

        //    return get(size()-1).getChordsLineNumber() + get(size()-1).getGroupLineCount() + get(size()-1).getWrappedLines();
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

    public String getSongData () {
        try {
            File dataFile = new File(FindFile.getScoreDataDir(context), song.getScoreName() + ".dat");
            String data = FindFile.readTextFile(context, dataFile.getAbsolutePath());
            return data;
        }
        catch (Throwable t) {
            Toast.makeText(this, "Unable to save file: " + t.toString(), Toast.LENGTH_LONG).show();
        }
        return null;
    }


    public void saveSongData () {
        if (autoScroll.getScoreData() != null && autoScroll.getGroupArray() != null) {
            String data = autoScroll.getScoreData().getSerializedData() + autoScroll.getGroupArray().getSerializedData();
            try {
                //FileOutputStream fileOutputStream = new FileOutputStream (sheetMusicFile);
                File dataFile = new File(FindFile.getScoreDataDir(context), song.getScoreName() + ".dat");
                FileOutputStream fileOutputStream = new FileOutputStream(dataFile, false);
                fileOutputStream.write(data.getBytes());
                fileOutputStream.close();
            } catch (Throwable t) {
                Toast.makeText(this, "Unable to save file: " + t.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * The Move seek bar. Thread to move seekbar based on the current position
     * of the song
     */

    Runnable moveSeekBarThread = new Runnable() {
        public void run() {
            //if (!isEditScore()) {
            int beat = song.getBeat();
            if (isPlaying() && !isEditScore()) {
                autoScroll.setSeekBarProgress();
            }
            else if (isEditScore()) {
                songLineSettings.updateBeat(beat);
            }
            textCountdown.setText(String.format("%d.%d", song.getMeasure(), beat));
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
        inflater.inflate(R.menu.group_menu, menu);
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
            case R.id.menu_set_mp3:
                DirectoryChooserDialog directoryChooserDialog = new DirectoryChooserDialog(this, new DirectoryChooserDialog.ChosenDirectoryListener() {
                    @Override
                    public void onChosenDir(String chosenMp3) {
                        Song mp3song = song.getSong(context, chosenMp3);
                        if (mp3song != null) {
                            File score = new File(song.getSheetMusicPath());
                            String scoreName = mp3song.getScoreName();
                            String newScore = score.getParent() + "/" + scoreName;

                            //score.renameTo(new File(newScore));
                            song = mp3song;
                            song.setSheetMusicPath(newScore);
                            initializePlayer();
                        }
                        //song.setSheetMusicPath(chosenMp3);
                        //Toast.makeText(ScrollActivity.this, "Chosen directory: " + chosenMp3, Toast.LENGTH_LONG).show();
                    }
                });
                directoryChooserDialog.chooseDirectory (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath());
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
    public boolean onPrepareOptionsMenu (Menu menu) {
        MenuItem editLine = menu.getItem(0);
        MenuItem duplicateGroup = menu.getItem(1);
        MenuItem deleteGroup = menu.getItem(2);
        MenuItem setmp3 = menu.getItem(3);

        editLine.setVisible(false);
        duplicateGroup.setVisible(false);
        deleteGroup.setVisible(false);
        setmp3.setVisible(false);

        if (isEditSong()) {
            setmp3.setVisible(true);
        }
        else if (isEditScore()) {
            editLine.setVisible(true);
            duplicateGroup.setVisible(true);
            deleteGroup.setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void initializePlayer () {
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
        scrollSensitivity = 2.0;

        //startTime = System.currentTimeMillis();

        ivMute =        (ImageView) findViewById(R.id.ivMute);
        ivPlay =        (ImageView) findViewById(R.id.ivPlay);
        ivNext =        (ImageView) findViewById(R.id.ivNext);
        ivPrevious =    (ImageView) findViewById(R.id.ivPrevious);
        ivBackground =  (ImageView) findViewById(R.id.ivBackground);
        autoScroll =    (AutoScroll) findViewById(R.id.seekBar);
        textView =      (TextView) findViewById(R.id.textView);
        textCountdown = (TextView) findViewById(R.id.textCountdown);
        scrollView =    (ScrollViewExt) findViewById(R.id.scrollView);

        lineSettingsContainer = (ViewGroup) findViewById(R.id.lineSettingsContainer);
        songSettingsContainer = (ViewGroup) findViewById(R.id.songSettingsContainer);

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
            initializePlayer();
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
        sheetMusicFile = new File (song.getSheetMusicPath());

        //Read text from file
        //autoScroll = new AutoScrollCalculated(this, file);
        sb = new SpannableStringBuilder(autoScroll.create(this, sheetMusicFile));
      //  song.setDuration(autoScroll.getSongDuration() * autoScroll.getScoreData().getBeatsPerMeasure() * autoScroll.getBeatInterval());
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
        songSettings.update();

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
                    lastSongPos = song.getMeasure();
                    song.start();
                    ivPlay.setImageResource(android.R.drawable.ic_media_pause);
                    lastSongPos = song.getMeasure();
                }
            }
        });

        ivNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isEditScore()) {
                    if (!isPlaying()) {
                        int nextGroup = autoScroll.getProgress();
                        while (++nextGroup < autoScroll.getGroupArray().size() && autoScroll.getGroupArray().get(nextGroup).getMeasures() == 0);
                        autoScroll.setProgress(nextGroup);
                    }
                    else {
                        /////// set measures per group
                        int current_measure = song.getMeasure();
                        autoScroll.getGroupArray().get(autoScroll.getProgress()).setMeasures(current_measure - lastSongPos);
                        autoScroll.setProgress(autoScroll.getProgress() + 1);
                        lastSongPos = current_measure;
                    }
                }
                else {
                    song.setStartPosition(song.getPosition() + getAutoScroll().getScoreData().getBeatInterval());
                }
            }
        });
        ivPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (autoScroll.getProgress() == 0){
                if (!isEditing()) {
                    onBackPressed();
                }
            }
            else {
                if (isEditScore()) {
                    int prevGroup = autoScroll.getProgress();
                    while (--prevGroup >= 0 && autoScroll.getGroupArray().get(prevGroup).getMeasures() == 0);
                    autoScroll.setProgress(prevGroup);

                   // autoScroll.setProgress(autoScroll.getProgress() - 1);
                } else {
                    song.setStartPosition(song.getPosition() - getAutoScroll().getScoreData().getBeatInterval());
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
        saveSongData ();

        super.onBackPressed();
    }

    @Override
    public void onScrollChanged(ScrollViewExt scrollView, int x, int y, int oldx, int oldy) {}

   void expand (int viewId) {
        //ivBackground.setVisibility(expand ? View.GONE: View.VISIBLE);
        ActionBar actionBar = getSupportActionBar();

       if (viewId==0) {
           actionBar.hide();
         //  duplicateGroup.setVisible(false);
         //  deleteGroup.setVisible(false);
           //ivBackground.setVisibility(View.GONE);
           setMaxScroll(NOEDIT);
           saveSongData();
       }
       else {
           if (viewId == EDITLINE ) {
               songSettingsContainer.setVisibility(View.GONE);
               lineSettingsContainer.setVisibility(View.VISIBLE);
               setMaxScroll(lineEditMode);

              // duplicateGroup.setVisible(false);
             //  deleteGroup.setVisible(false);
           }
           else {
               lineSettingsContainer.setVisibility(View.GONE);
               songSettingsContainer.setVisibility(View.VISIBLE);

              // duplicateGroup.setVisible(true);
              // deleteGroup.setVisible(true);
               setMaxScroll(EDITSONG);
           }
           actionBar.show ();
           //ivBackground.setVisibility(View.VISIBLE);
       }
       //setMaxScroll(id);
   }

   public void updateSongAndSeekProgress (int progress) {
       int newProgress = getAutoScroll().getProgress() + progress;
       getAutoScroll().setProgress(newProgress);
       if (isEditLine()) {
           int group = getAutoScroll().getGroupArray().getGroupFromLine(progress);
           song.setStartPosition(getAutoScroll().getGroupArray().getBeatsToStartOfLine(group) * getAutoScroll().getScoreData().getBeatInterval());
       }
       else if (isEditGroup()) {
           song.setStartPosition(getAutoScroll().getGroupArray().getBeatsToStartOfLine(newProgress) * getAutoScroll().getScoreData().getBeatInterval());
       }
       else {
           setSongPosition (newProgress);
       }
   }

    public void setSongPosition (double scrollY) {
        double offset = scrollY*scrollSensitivity;
        int newmeasure = getAutoScroll().getProgress() + (int) (offset/getAutoScroll().getScrollYmin());
        getSong().setStartPosition(newmeasure * getAutoScroll().getScoreData().getBeatsPerMeasure() * getAutoScroll().getScoreData().getBeatInterval());
    }

   public void setMaxScroll(int newmode) {
        //isEditing = false;
        mode = newmode == -1 ? mode : newmode;
        autoScroll.setMax();
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
        chordline = chordline.concat("\n");
        //Pattern possiblechord = java.util.regex.Pattern.compile("[^\\s]+");
        //Pattern validChord = java.util.regex.Pattern.compile("[^x\\[\\]\\(\\)]+");
        Pattern validChord = java.util.regex.Pattern.compile("(\\(*(?<![A-Z])[CDEFGAB](?![A-Z])(?:b|bb)*(?:|#|##|add|sus|maj|min|aug|m|M|b|°|[0-9])*[\\(]?[\\d\\/-/+]*[\\)]?(?:[CDEFGAB](?:b|bb)*(?:#|##|add|sus|maj|min|aug|m|M|b|°|[0-9])*[\\d\\/]*)*\\)*)(?=[\\s|$])(?![a-z])");
        Pattern repeat = java.util.regex.Pattern.compile("\\d+[xX]");

        int numrepeat = 1;

        Matcher repeatMatcher = repeat.matcher(chordline);
        if (repeatMatcher.find()) {
            String multiple = chordline.substring(repeatMatcher.start(), repeatMatcher.end()-1);
            numrepeat = Integer.parseInt(multiple);
        }

        Matcher chordMatcher = validChord.matcher(chordline);
        int chordCount = 0;
        while (chordMatcher.find()) {
            chordCount++;
            final ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.songchords));
            if (gd.getMeasures() != 0) {
                spannableStringBuilder.setSpan(fcs, gd.getOffsetChords() + chordMatcher.start(), gd.getOffsetChords() + chordMatcher.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }

        if (gd.getMeasures() != 0) {
            if (autoScroll.getGroupArray().getClass() == GroupArrayGuess.class) {
                gd.setMeasures(gd.getMeasures() * numrepeat);
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
        setSb();
        autoScroll.setMax(song.getTotalMeasures());
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
        setView(false);
    }

    public void replaceText (int start, int end, String text) {
        if (end != -1) {
            sb.replace(start, end, text);
        }
        else {
            sb.insert(start, text);
        }
        setSb();
    }

    public void duplicateText (int start, int end, int group) {
        String score = sb.toString();
        if (end != -1) {
            sb.insert(start, score.substring(start, end));
        }
        else {
            sb.insert(start, score.substring(start));
        }
        setSb();
    }
}
