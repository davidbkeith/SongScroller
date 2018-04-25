package com.mobileapps.brad.songscroller;

import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.Arrays;

public class SongLineSettings  {

    private int lineStart;
    private EditText editBeats;
   // EditText editRepeat;
    private CheckBox checkSongStart;
    private EditText editLyrics;
    private Button buttonSave, buttonCancel;
    private GroupData groupDataOriginal, groupData;
    private int groupIndex;

    private ScrollActivity scrollActivity;
    private AutoScroll autoScroll;
    //private String text;
    private boolean enableUpdates;

    public SongLineSettings(final ScrollActivity scrollActivity) {
        this.scrollActivity = scrollActivity;

        editBeats = (EditText) scrollActivity.findViewById(R.id.editBeats);
        checkSongStart = (CheckBox) scrollActivity.findViewById(R.id.checkSongStart);
        editLyrics = (EditText) scrollActivity.findViewById(R.id.editLyrics);

        checkSongStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSongStart.isChecked()) {
                    scrollActivity.getAutoScroll().getGroupArray().setSongStart(groupIndex);
                }
            }
        });

        editBeats.addTextChangedListener(new TextWatcher() {
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
                        GroupData groupData = autoScroll.getGroupArray().get(groupIndex);
                        groupData.setBeats(Integer.parseInt(editable.toString().trim()));
                        //autoScroll.getGroupArray().setLineMeasuresCount(groupData, Integer.parseInt(editable.toString().trim()));
                    }
                    catch (Exception e) {

                    }
                }
            }
        });

      /*  editChords.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (enableUpdates) {
                    GroupData groupData = autoScroll.getGroupArray().get(groupIndex);
                    enableUpdates = false;

                    String[] arrchords = editable.toString().split(",");
                    //groupData.setChords(arrchords, groupDataOriginal.getChords());

                    editBeats.setText(String.format("%d", groupData.getBeats()));

                    scrollActivity.setSpans();
                    enableUpdates = true;
                }
            }
        });*/

        editLyrics.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (enableUpdates) {
                    autoScroll.getGroupArray().setGroupText(groupIndex, editable.toString());
                }
            }
        });
    }

    protected void refresh () {
        if (groupData != null) {
            editBeats.setText(String.format("%d", groupData.getBeats()));
        }
    }

    protected void update() {
        enableUpdates = false;
        autoScroll = scrollActivity.getAutoScroll();
        int progress = autoScroll.getProgress();
        groupIndex = autoScroll.getGroupArray().getCurrentGroup();

        if (groupIndex == -1) {
            groupData = new GroupData();
            editBeats.setText("0");
        }
        else {
            groupData = autoScroll.getGroupArray().get(groupIndex);
            checkSongStart.setChecked(autoScroll.getGroupArray().isSongStart(groupIndex));
            editBeats.setText(String.format("%d", groupData.getBeats()));
        }

        groupDataOriginal = autoScroll.getGroupArrayOriginal().getGroupFromBeats(progress);


       // editChords.setText(String.format("%s", Arrays.toString(groupData.getChordsStartPositions()).replaceAll("\\[|\\]", "")));
        editLyrics.setText(scrollActivity.getLyrics());
        enableUpdates = true;
    }
}
