package com.mobileapps.brad.songscroller;

import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import java.util.Arrays;

public class SongLineSettings  {

    private int lineStart;
    private EditText editBeats;
   // EditText editRepeat;
    private EditText editChords;
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
        editChords = (EditText) scrollActivity.findViewById(R.id.editChords);
        editLyrics = (EditText) scrollActivity.findViewById(R.id.editLyrics);

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
                    GroupData groupData = autoScroll.getGroupArray().get(groupIndex);
                    enableUpdates = false;

                    String[] arrchords = editable.toString().split(",");
                    //groupData.setChords(arrchords, groupDataOriginal.getChords());

                    editBeats.setText(String.format("%d", groupData.getBeats()));

                    scrollActivity.setSpans();
                    enableUpdates = true;
                }
            }
        });

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
                    groupData.setLyrics(editable.toString(), lineStart, scrollActivity);
                    //line += editable.toString().split ("\n").length - 1;
                }
            }
        });
    }

    protected void update() {
        enableUpdates = false;
        autoScroll = scrollActivity.getAutoScroll();
        int progress = autoScroll.getProgress();
        groupIndex = autoScroll.getGroupArray().getCurrentGroup();

        if (groupIndex == -1) {
            groupData = new GroupData();
            //groupData.setOffsetChords(scrollActivity.getScrollView().getLyricsPos());
        }
        else {
            groupData = autoScroll.getGroupArray().get(groupIndex);
        }

        groupDataOriginal = autoScroll.getGroupArrayOriginal().getGroupFromBeats(progress);

        editBeats.setText(String.format("%d", groupData.getBeats()));
        //editChords.setText(String.format("%s", Arrays.toString(groupData.getChordsStartPositions()).replaceAll("\\[|\\]", "")));
        editLyrics.setText(scrollActivity.getLyrics());
        enableUpdates = true;
    }
}
