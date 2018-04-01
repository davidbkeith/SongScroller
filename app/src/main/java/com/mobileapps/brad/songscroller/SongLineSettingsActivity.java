package com.mobileapps.brad.songscroller;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class SongLineSettingsActivity extends AppCompatActivity {

    EditText editMeasures;
    EditText editRepeat;
    EditText editChords;
    EditText editLyrics;
    Button buttonSave, buttonCancel;
    GroupData groupData, groupDataPrevious;
    GroupArray groupArray;
    int groupIndex;

    ScrollActivity scrollActivity;
    AutoScroll autoScroll;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_line);
        scrollActivity = (ScrollActivity) getParent();
        autoScroll = scrollActivity.getAutoScroll();
        groupArray = autoScroll.getGroupArray();
        text = autoScroll.getText();
        groupIndex = groupArray.getGroupIndex();

        groupData = groupArray.get(groupIndex);
        groupDataPrevious = groupIndex > 0 ? groupArray.get(groupIndex-1) : new GroupData();

        //groupData = (GroupData) getIntent().getSerializableExtra("songscroller_groupdata");
        String title = (String) getIntent().getSerializableExtra("songscroller_title");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //// set title
        getSupportActionBar().setTitle(String.format(title));

        editMeasures = (EditText) findViewById(R.id.editMeasures);
        editRepeat = (EditText) findViewById(R.id.editRepeat);
        editChords = (EditText) findViewById(R.id.editChords);
        editLyrics = (EditText) findViewById(R.id.editDuration);
        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);

        if (groupData != null) {
            editMeasures.setText(String.format("%d", groupData.getMeasuresToEndofLine() - groupDataPrevious.getMeasuresToEndofLine()));
            editRepeat.setText(String.format("%d", groupData.getRepeat()));
            editChords.setText(String.format("%d", groupData.getChords().toString()));
            editLyrics.setText(String.format("%d", groupData.getLyrics(text)));
        }

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupData.setMeasuresToEndofLine(groupDataPrevious.getMeasuresToEndofLine() + Integer.parseInt(editMeasures.toString()));
                groupData.setRepeat(Integer.parseInt(editRepeat.toString()));
                groupData.setChords(editChords.toString().split(","));
                autoScroll.setText(groupData.setLyrics(editLyrics.toString(), text));
            }
        });

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
