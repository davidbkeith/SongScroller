package com.mobileapps.brad.songscroller;

import java.io.File;
import java.util.Scanner;

/**
 * Created by brad on 2/10/18.
 */

public class FindFile {

    static public File find(String name,File file)
    {
        File foundFile;
        File[] list = file.listFiles();
        if(list!=null) {
            for (File fil : list) {
                if (fil.isDirectory()) {
                    return find(name, fil);
                } else if (name.equalsIgnoreCase(fil.getName())) {
                    return fil;
                }
            }
        }
        return new File("");
    }
}
