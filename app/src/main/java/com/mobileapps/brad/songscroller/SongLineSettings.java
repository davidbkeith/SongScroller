package com.mobileapps.brad.songscroller;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class SongLineSettings  {

    EditText editMeasures;
   // EditText editRepeat;
    EditText editChords;
    EditText editLyrics;
    Button buttonSave, buttonCancel;
    GroupData groupData, groupDataOriginal;
    int groupIndex;

    ScrollActivity scrollActivity;
    AutoScroll autoScroll;
    String text;
    boolean enableUpdates;

    public SongLineSettings(final ScrollActivity scrollActivity) {
        this.scrollActivity = scrollActivity;

        editMeasures = (EditText) scrollActivity.findViewById(R.id.editMeasures);
        editChords = (EditText) scrollActivity.findViewById(R.id.editChords);
        editLyrics = (EditText) scrollActivity.findViewById(R.id.editLyrics);

        editMeasures.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (enableUpdates) {
                    try {
                        //groupData.g
                        autoScroll.getGroupArray().setLineMeasuresCount(groupData, Integer.parseInt(editable.toString().trim()));
                    }
                    catch (Exception e) {

                    }
                }
            }
        });

        editChords.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (enableUpdates) {
                    enableUpdates = false;

                    String[] arrchords = editable.toString().split(",");
                    groupData.setChords(arrchords, groupDataOriginal.getChords());
                    autoScroll.getGroupArray().setLineMeasuresCount(groupData, (groupData.getChords().length/2)*autoScroll.getGroupArray().getMeasuresPerChord());

                    int measures = autoScroll.getGroupArray().getLineMeasuresFromTotalMeasures(autoScroll.getProgress());
                    editMeasures.setText(String.format("%d", measures));

                    scrollActivity.setSpans();
                    enableUpdates = true;
                }
            }
        });
    }

    protected void update() {
        enableUpdates = false;
        autoScroll = scrollActivity.getAutoScroll();
        int progress = autoScroll.getProgress();

        groupData = autoScroll.getGroupArray().getGroupFromMeasure(progress);
        groupDataOriginal = autoScroll.getGroupArrayOriginal().getGroupFromMeasure(progress);

        int measures = autoScroll.getGroupArray().getLineMeasuresFromTotalMeasures(progress);;
        text = autoScroll.getText();

        editMeasures.setText(String.format("%d", measures));
       // editRepeat.setText(String.format("%d", groupData.getRepeat()-1));
        editChords.setText(String.format("%s", Arrays.toString(groupData.getChordsStartPositions()).replaceAll("\\[|\\]","")));

        String lineLyrics = groupData.getLyrics(text);
        editLyrics.setText(lineLyrics);
        enableUpdates = true;
    }
}
