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
    EditText editBeatsPerLine;
    EditText editStartLine;
    EditText editDuration;
    RadioButton radioLine;
    RadioButton radioConstant;
    ScoreData scoreData;
    ScrollActivity scrollActivity;
    boolean editing;

    public SongSettings(final ScrollActivity scrollActivity) {
        this.scrollActivity = scrollActivity;

        editBPM = (EditText) scrollActivity.findViewById(R.id.editBPM);
        editBeatsPerLine = (EditText) scrollActivity.findViewById(R.id.editBeatsPerLine);
        editBPMeasure = (EditText) scrollActivity.findViewById(R.id.editBPMeasure);
        editStartLine = (EditText) scrollActivity.findViewById(R.id.editStartLine);

        editStartLine.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    scrollActivity.getAutoScroll().getScoreData().setScrollOffset(Integer.parseInt(editable.toString().trim()));
                }
                catch (Exception e)
                {
                    Log.d("Edit BMP", "Invalid integer value");
                }

            }
        });

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
                    scrollActivity.getAutoScroll().scoreData.setBpm(Integer.parseInt(editable.toString().trim()));
                }
                catch (Exception e)
                {
                    Log.d("Edit BMP", "Invalid integer value");
                }
            }
        });

        ///// change time signature
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
                    scrollActivity.getAutoScroll().scoreData.setBeatsPerMeasure(Integer.parseInt(editable.toString().trim()));
                   // scrollActivity.getAutoScroll().getGroupArray().reset();
                }
                catch (Exception e)
                {
                    Log.d("Edit BMP", "Invalid integer value");
                }
            }
        });

        editBeatsPerLine.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    scrollActivity.getAutoScroll().scoreData.setBeatsPerLine(Integer.parseInt(editable.toString().trim()));
                    //scrollActivity.getAutoScroll().getGroupArray().reset();
                }
                catch (Exception e)
                {
                    Log.d("Edit BMP", "Invalid integer value");
                    editing = false;
                }
            }
        });

    }

    protected void update() {
        scoreData = scrollActivity.getAutoScroll().getScoreData();
        if (scoreData != null && !editing) {
            editBPM.setText(String.format("%d", scoreData.getBpm()));
            editBPMeasure.setText(String.format("%d", scoreData.getBeatsPerMeasure()));
            editBeatsPerLine.setText(String.format("%d", scoreData.getBeatsPerLine()));
            editStartLine.setText(String.format("%d", scoreData.getScrollOffset()));
        }
    }
}
