package com.mobileapps.brad.songscroller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

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
}
