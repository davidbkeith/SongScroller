package com.mobileapps.brad.songscroller;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

public class SongSettings implements AdapterView.OnItemSelectedListener{
    EditText editBPM;
    EditText editBPMeasure;
    CheckBox checkSongStart;

    public Spinner getTimeSigs() {
        return timeSigs;
    }

    Spinner timeSigs;
    //EditText editStartLine;
    EditText editDuration;
    RadioButton radioLine;
    RadioButton radioConstant;
    ScoreData scoreData;
    ScrollActivity scrollActivity;
    boolean editing;

    public SongSettings(final ScrollActivity scrollActivity) {
        this.scrollActivity = scrollActivity;

        editBPM = (EditText) scrollActivity.findViewById(R.id.editBPM);
        //checkSongStart = (CheckBox) scrollActivity.findViewById(R.id.checkSongStart);
        editBPMeasure = (EditText) scrollActivity.findViewById(R.id.editBPMeasure);
        editDuration = (EditText) scrollActivity.findViewById(R.id.editDuration);
        timeSigs = (Spinner) scrollActivity.findViewById(R.id.timesig_spinner);
       // editStartLine = (EditText) scrollActivity.findViewById(R.id.editStartLine);

 /*       editStartLine.addTextChangedListener(new TextWatcher() {
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
        });*/

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
                    scrollActivity.getAutoScroll().scoreData.setMeasures(Integer.parseInt(editable.toString().trim()));
                   // scrollActivity.getAutoScroll().getGroupArray().reset();
                }
                catch (Exception e)
                {
                    Log.d("Edit BMP", "Invalid integer value");
                }
            }
        });

        editDuration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    scrollActivity.getSong().setDuration(Integer.parseInt(editable.toString().trim())*1000);
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

    @Override
    public void onItemSelected (AdapterView<?> parent, View view, int pos, long id) {
        String timeSig = (String) getTimeSigs().getSelectedItem();
        scrollActivity.getAutoScroll().getScoreData().setTimesignature(Integer.parseInt(timeSig.split("/")[0]));
    }

    @Override
    public void onNothingSelected (AdapterView<?> parent) {
    }

    protected void update() {
        scoreData = scrollActivity.getAutoScroll().getScoreData();
        if (scoreData != null && !editing) {
            editBPM.setText(String.format("%d", scoreData.getBpm()));
            editBPMeasure.setText(String.format("%d", scoreData.getMeasures()));
            editDuration.setText(String.format("%d", scrollActivity.getSong().getDuration() / 1000));

            ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(scrollActivity, R.array.timesig_spinner, android.R.layout.simple_spinner_dropdown_item);
            timeSigs.setAdapter(adapterSpinner);
            switch (scrollActivity.getAutoScroll().getScoreData().getTimesignature()) {
                case 2:
                    timeSigs.setSelection(0);
                    break;
                case 3:
                    timeSigs.setSelection(1);
                    break;
                case 4:
                    timeSigs.setSelection(2);
                    break;
                case 6:
                    timeSigs.setSelection(3);

            }
            //timeSigs.setSelection(2);
            timeSigs.setOnItemSelectedListener(this);


            //editBeatsPerLine.setText(String.format("%d", scoreData.getBeatsPerLine()));
            //editStartLine.setText(String.format("%d", scoreData.getScrollOffset()));
        }
    }
}
