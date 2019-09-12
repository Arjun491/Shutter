package com.example.shutter.Utils;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by User on 7/24/2017.
 */

public class Filesearch {

    /**
     * Search a directory and return a list of all **directories** contained inside
     * @param directory
     * @return
     */
    public static ArrayList<String> getDirectoryPaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        Log.d("directory", "getDirectoryPaths: "+directory);
        File[] listfiles = file.listFiles();
        for(int i = 0; i < listfiles.length; i++){
            if(listfiles[i].isDirectory()){
                pathArray.add(listfiles[i].getAbsolutePath());
            }
        }
        return pathArray;
    }

    /**
     * Search a directory and return a list of all **files** contained inside
     * @param directory
     * @return
     */
    public static ArrayList<String> getFilePaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();
        for(int i = 0; i < listfiles.length; i++){
            if(listfiles[i].isFile()){
                Log.d("listfiles", "getFilePaths: "+listfiles[i]);
                pathArray.add(listfiles[i].getAbsolutePath());
            }
        }
        return pathArray;
    }
}