package com.example.gurpreetsingh.project;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class VolumeService extends Service {
    SettingsContentObserver mSettingsContentObserver;
    MediaPlayer mediaPlayer;

    public VolumeService() {
        //Toast.makeText(this, "constructor service", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.d("test", "oncreate service started");
        //mediaPlayer= MediaPlayer.create(getApplicationContext(), R.raw.point1sec);
        //mediaPlayer.start();

        mSettingsContentObserver = new SettingsContentObserver(getApplicationContext() ,new Handler());
        getApplicationContext().getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, mSettingsContentObserver);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("test", "onstart of service started");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("test", "destroy service");
        //mediaPlayer.stop();

        getApplicationContext().getContentResolver().unregisterContentObserver(mSettingsContentObserver);
    }
}
