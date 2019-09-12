package com.example.shutter.Utils;

import android.os.Environment;


public class Filepath {


    //"storage/emulated/0"
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();

    public String PICTURES = ROOT_DIR ;
    public String CAMERA = ROOT_DIR + "/DCIM/Camera";

    public String FIREBASE_IMAGE_STORAGE = "photos/users/";
}
