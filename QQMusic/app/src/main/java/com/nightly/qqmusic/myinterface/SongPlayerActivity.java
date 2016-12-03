package com.nightly.qqmusic.myinterface;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.nightly.qqmusic.service.MediaService;

/**
 * Created by Nightly on 2016/10/13.
 */

public class SongPlayerActivity extends AppCompatActivity {
    protected MediaService mediaService;
    private Intent intent;
    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            MediaService.MyBinder myMinder= (MediaService.MyBinder) binder;
            mediaService = myMinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = new Intent(this, MediaService.class);
        startService(intent);
        boolean bindSuccess = bindService(intent, connection, BIND_AUTO_CREATE);
        if(bindSuccess){
            Toast.makeText(this, "服务绑定成功。", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "服务绑定失败。", Toast.LENGTH_SHORT).show();
        }
    }
}
