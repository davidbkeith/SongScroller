package com.mobileapps.brad.songscroller;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by brad on 2/10/18.
 */

public class FindFile {

    static public File getScoreDataDir (Context context) {
        File file = new File(context.getExternalFilesDir(null), "scoredata");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("Create data file", "Directory not created");
            }
        }
        return file;
    }

    static public String readTextFile (Context context, String filePath) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            File readFile = new File (filePath);
            reader = new BufferedReader(new InputStreamReader(context.getContentResolver().openInputStream (Uri.fromFile(readFile))));
            String line = "";

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }

    static public File find(String name, File file, boolean recurse)
    {
        File foundFile = new File(file, name);
        File[] list = file.listFiles();
        if(list!=null) {
            for (File fil : list) {
                if (recurse && fil.isDirectory()) {
                    find (name, fil, recurse);
                 } else if (name.equalsIgnoreCase(fil.getName())) {
                    foundFile = fil;
                    return fil;
                }
            }
        }
        return foundFile;
    }

    static public File[] findFilesWithExt (File parent, String ext) {
        final String extension = ext;
        final Pattern p;
        File[] flist = null;

        if (parent != null) {
            //// last one has to be file type
            flist = parent.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File file, String name) {
                    return name.endsWith(extension);
                }
            });
         }
        return flist;
    }

    static public File findFileWithExt (File parent, List<String> arrSubfolders, String ext) {

        final String folderName;
        final String extension = ext;
        final Pattern p;
        File[] flists = null;

        if (parent != null) {
            if (arrSubfolders != null && arrSubfolders.size() > 0) {
                folderName = arrSubfolders.get(0);
                arrSubfolders.remove(0);
                p = Pattern.compile(".*" + folderName + ".*", Pattern.CASE_INSENSITIVE);
                flists = parent.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                    return p.matcher(file.getName()).matches();
                    }
                });
                return findFileWithExt(flists[0], arrSubfolders, extension);
            } else {
                //// last one has to be file type
                flists = parent.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File file, String name) {
                    return name.endsWith(extension);
                    }
                });

                if (flists != null && flists.length > 0) {
                    return flists[0];
                }
            }
        }
        return new File("");
    }
}
