package com.mobileapps.brad.songscroller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Context;
import android.os.Environment;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by brad on 5/1/18.
 */

public class DirectoryChooserDialog {
    private boolean m_isNewFolderEnabled = false;
    private String m_showFiles = ".+\\.mp3";
    private String m_sdcardDirectory;
    private Context m_context;
    private TextView m_titleView;

    private String m_dir;
    private String m_fileName;
    private List<String> m_subdirs = null;
    private ChosenDirectoryListener m_chosenDirectoryListener = null;
    private ArrayAdapter<String> m_listAdapter = null;

    /// callback interface for selected directory
    public interface ChosenDirectoryListener
    {
        public void onChosenDir(String chosenDir);
    }

    private boolean showFiles() {
        return !m_showFiles.isEmpty();
    }

    public DirectoryChooserDialog (Context context, ChosenDirectoryListener chosenDirectoryListener) {
        m_context = context;
        m_sdcardDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        m_chosenDirectoryListener = chosenDirectoryListener;

        try {
            m_sdcardDirectory = new File(m_sdcardDirectory).getCanonicalPath();
        }
        catch (IOException ioe) {
            Log.d("Create sd card dir:", ioe.toString());
        }
    }

    /// enable/disable new folder button
    public void setNewFolderEnabled (boolean isNewFolderEnabled) {
        m_isNewFolderEnabled = isNewFolderEnabled;
    }

    public boolean isNewFolderEnabled() {
        return m_isNewFolderEnabled;
    }

    /// load directory chooser dialog for initial default sdcard directory
    public void chooseDirectory () {
        chooseDirectory(m_sdcardDirectory);
    }

    /// load directory chooser dialog for initial inpout 'dir' directory
    public void chooseDirectory (String dir) {
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            dir = m_sdcardDirectory;
        }
        if (dirFile.isFile()) {
            m_fileName = dirFile.getName();
            dir = dirFile.getParent();
        }

        try {
            dir = new File(dir).getCanonicalPath();
        }
        catch (IOException ioe) {
            return;
        }

        m_dir = dir;
        m_subdirs = getDirectories (dir);

        class DirectoryOnClickListener implements DialogInterface.OnClickListener {
            public void onClick (DialogInterface dialog, int item) {
                /// navigate to the sub-directory
                String nextFolder = (String) ((AlertDialog) dialog).getListView().getAdapter().getItem(item);

                if (nextFolder.equals("..") || m_dir.matches(m_showFiles)) {
                    int firstSlash = m_dir.indexOf('/');
                    int lastSlash = m_dir.lastIndexOf('/');
                    if (firstSlash >= 0 && lastSlash > firstSlash) {
                        m_dir = m_dir.substring(0, lastSlash);
                    }

                    if (nextFolder.matches(m_showFiles)) {
                        m_dir += "/" + nextFolder;
                    }
                }
                else {
                    m_dir += "/" + nextFolder;
                }
                updateDirectory();
            }
        }

        AlertDialog.Builder dialogBuilder = createDirectoryChooserDialog (dir, m_subdirs, new DirectoryOnClickListener());

        dialogBuilder.setPositiveButton("OK", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                /// Current directory chosen
                if (m_chosenDirectoryListener != null) {
                    /// call registered listener supplied with the chosen directory
                    //if ((new File(m_dir)).isDirectory()) {
                    m_chosenDirectoryListener.onChosenDir(m_dir);
                    //}
                }
            }
        }).setNegativeButton("Cancel", null);

        final AlertDialog dirsDialog = dialogBuilder.create();

        dirsDialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    /// back button pressed
                    if (m_dir.equals(m_sdcardDirectory)) {
                        /// the very top level directory, do nothing
                        return false;
                    }
                    else {
                        /// navigate back to an uppder directory
                        m_dir = new File(m_dir).getParent();
                        updateDirectory();
                    }
                    return true;
                }
                return false;
            }
        });

        dirsDialog.show();
    }

    private boolean creatSubDir (String newDir) {
        File newDirFile = new File (newDir);
        if (!newDirFile.exists()) {
            return newDirFile.mkdir();
        }
        return false;
    }

    private List<String> getDirectories(String dir) {
        List<String> dirs = new ArrayList<>();

        try {

            File dirFile = new File(dir);

            dirs.add("..");
            if (!dirFile.exists() || !dirFile.isDirectory()) {
                return dirs;
            }

            for (File file : dirFile.listFiles()) {
                if (file.isDirectory() || (showFiles() && file.getName().matches(m_showFiles))) {
                    dirs.add(file.getName());
                }
            }

        }
        catch (Exception e) {
            Log.d("Error get directories:", e.toString());
        }

        Collections.sort(dirs, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });

        return dirs;
    }

    private AlertDialog.Builder createDirectoryChooserDialog (String title, List<String> listItems, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(m_context);

        /// Create custom view for AlertDialog title containing
        /// current directory TextView and possible 'New folder' button.
        /// Current directory TextView allows long directory path to be wrapped
        LinearLayout titleLayout = new LinearLayout(m_context);
        titleLayout.setOrientation(LinearLayout.VERTICAL);

        m_titleView = new TextView(m_context);
        m_titleView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        m_titleView.setTextAppearance(m_context, android.R.style.TextAppearance_Large);
        m_titleView.setTextColor(m_context.getResources().getColor(android.R.color.white));
        m_titleView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        m_titleView.setText(title);

        Button newDirButton = new Button(m_context);
        newDirButton.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        newDirButton.setText("New Folder");
        newDirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText input = new EditText(m_context);

                /// Show new folder name input dialog
                new AlertDialog.Builder(m_context).setTitle("New folder name").setView(input).setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    public void onClick (DialogInterface dialog, int whichButton) {
                        Editable newDir = input.getText();
                        String newDirName = newDir.toString();
                        /// Create new directory
                        if (creatSubDir(m_dir + "/" + newDirName)) {
                            /// navigate into the new directory
                            m_dir += "/" + newDirName;
                            updateDirectory ();
                        }
                        else {
                            Toast.makeText(m_context, "Failed to create '" + newDirName + "' folder", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("Cancel", null).show();
            }
        });

        if (!m_isNewFolderEnabled) {
            newDirButton.setVisibility(View.GONE);
        }

        titleLayout.addView(m_titleView);
        titleLayout.addView(newDirButton);

        dialogBuilder.setCustomTitle(titleLayout);
        m_listAdapter = createListAdapter(listItems);

        dialogBuilder.setSingleChoiceItems(m_listAdapter, -1, onClickListener);
        dialogBuilder.setCancelable(false);
        return dialogBuilder;
    }

    private void updateDirectory() {

        if (new File(m_dir).isDirectory()) {
            m_subdirs.clear();
            m_subdirs.addAll(getDirectories(m_dir));
            m_listAdapter.notifyDataSetChanged();
        }
        m_titleView.setText(m_dir);
    }

    private ArrayAdapter<String> createListAdapter(List<String> items) {
        return new ArrayAdapter<String>(m_context, android.R.layout.select_dialog_item, android.R.id.text1, items) {
            @Override
            public View getView (int position, View convertView, ViewGroup parent) {
                View v = super.getView (position, convertView, parent);

                if (v instanceof TextView) {
                    /// Enable list item (directory) text wrapping
                    TextView tv = (TextView) v;
                    tv.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
                    tv.setEllipsize(null);
                }

                return v;
            }
        };
    }
}




