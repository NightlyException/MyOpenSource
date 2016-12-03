package com.nightly.qqmusic;

import android.os.Environment;

/**
 * Created by Nightly on 2016/10/12.
 */

public class Config {

    public static final String DOWNLOAD_DIR_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();
}
