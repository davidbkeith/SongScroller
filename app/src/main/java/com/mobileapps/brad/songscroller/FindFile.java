package com.mobileapps.brad.songscroller;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by brad on 2/10/18.
 */

public class FindFile {

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
