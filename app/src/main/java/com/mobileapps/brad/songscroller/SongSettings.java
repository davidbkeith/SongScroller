package com.mobileapps.brad.songscroller;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.support.v7.widget.Toolbar;

public class SongSettings {
    EditText editBPM;
    EditText editBPMeasure;
    EditText editMPLine;
    EditText editDuration;
    RadioButton radioLine;
    RadioButton radioConstant;
    ScoreData scoreData;
    ScrollActivity scrollActivity;

    public SongSettings(final ScrollActivity scrollActivity) {
        this.scrollActivity = scrollActivity;

        editBPM = (EditText) scrollActivity.findViewById(R.id.editBPM);
        editBPMeasure = (EditText) scrollActivity.findViewById(R.id.editBPMeasure);
        editMPLine = (EditText) scrollActivity.findViewById(R.id.editMeasuresPerLine);

        editBPM.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    scrollActivity.getAutoScroll().getScoreData().setBpm(Integer.parseInt(editable.toString().trim()));
                }
                catch (Exception e)
                {
                    Log.d("Edit BMP", "Invalid integer value");
                }
            }
        });

        editBPMeasure.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    scrollActivity.getAutoScroll().getScoreData().setBeats(Integer.parseInt(editable.toString().trim()));
                }
                catch (Exception e)
                {
                    Log.d("Edit BMP", "Invalid integer value");
                }
            }
        });

        editMPLine.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    scrollActivity.getAutoScroll().getGroupArray().resetLineMeasures(Integer.parseInt(editable.toString().trim()));
                }
                catch (Exception e)
                {
                    Log.d("Edit BMP", "Invalid integer value");
                }
            }
        });
    }

    protected void update() {
        scoreData = scrollActivity.getAutoScroll().getScoreData();
        if (scoreData != null) {
            editBPM.setText(String.format("%d", scoreData.getBpm()));
            editBPMeasure.setText(String.format("%d", scoreData.getBeats()));
            editMPLine.setText(String.format("%d", scoreData.getMeasuresPerLine()));
        }
    }
}
