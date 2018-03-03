package com.mobileapps.brad.songscroller;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class SongSettingsActivity extends AppCompatActivity {
    EditText editBPM;
    EditText editBPMeasure;
    EditText editMPLine;
    RadioButton radioLine;
    RadioButton radioConstant;
    ScoreData scoreData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_settings);

        scoreData = (ScoreData) getIntent().getSerializableExtra("songscroller_scoredata");
        String title = (String) getIntent().getSerializableExtra("songscroller_title");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //// set title
        getSupportActionBar().setTitle(String.format(title));

        editBPM = (EditText) findViewById(R.id.editBPM);
        editBPMeasure = (EditText) findViewById(R.id.editBPMeasure);
        editMPLine = (EditText) findViewById(R.id.editMeasuresPerLine);
        radioLine = (RadioButton) findViewById(R.id.radioLine);
        radioConstant = (RadioButton) findViewById(R.id.radioConstant);

        if (scoreData != null) {
            editBPM.setText(String.format("%d", scoreData.getBpm()));
            editBPMeasure.setText(String.format("%d", scoreData.getBeats()));
            editMPLine.setText(String.format("%d", scoreData.getMeasuresPerLine()));

            if (scoreData.getMeasuresPerLine() > 0) {
                radioLine.setChecked(true);
            } else {
                radioConstant.setChecked(true);
            }
        }
        else {
            radioConstant.setChecked(true);
        }

        /*this.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeTop() {
                //Toast.makeText(ScrollActivity.this, "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
                //Toast.makeText(ScrollActivity.this, "right", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ScrollActivity.class);
                setResult(Activity.RESULT_OK, intent);
                finishActivity(1);
            }
            public void onSwipeLeft() {
                //Toast.makeText(ScrollActivity.this, "left", Toast.LENGTH_SHORT).show();
                //ScoreData scoreData = autoScroll.getScoreData();

            }
            public void onSwipeBottom() {
                //Toast.makeText(ScrollActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }
        });*/

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
        Intent intent = new Intent(getApplicationContext(), ScrollActivity.class);
        setResult(Activity.RESULT_OK, intent);
        finishActivity(1);
        super.onBackPressed();
    }
}
