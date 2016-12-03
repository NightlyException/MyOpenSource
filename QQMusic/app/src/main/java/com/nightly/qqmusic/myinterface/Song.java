package com.nightly.qqmusic.myinterface;

/**
 * Created by Nightly on 2016/10/12.
 */

public interface Song {
    String getIDownloadUrl();

    boolean isIDownloaded();

    String getIFilePath();

    String getISongName();

    String getISingerName();

    String getIAlbumpicSmall();

    String getIAlbumpicBig();

    int getISongID();

    //在线试听url
    String getIUrl();

}
