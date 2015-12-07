package com.example.gurpreetsingh.project;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * Created by Gurpreet Singh on 10/22/2015.
 */
public class SettingsContentObserver extends ContentObserver {
    int previousVolume;
    Context context;

    public SettingsContentObserver(Context c, Handler handler) {
        super(handler);
        context=c;


        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //previousVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        previousVolume = audio.getStreamVolume(AudioManager.STREAM_RING);
        Log.d("test", "setting content observer" + previousVolume);

    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        Log.d("test", "on change");

        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_RING);


        int delta=previousVolume-currentVolume;
        Log.d("test", "" + previousVolume);
        Log.d("test", "" + currentVolume);
        Log.d("test", "" + delta);

        if(delta>2 || delta<-2)
        {
            Log.d("test", "Decreased");
            Toast.makeText(context, "Vol down pressed", Toast.LENGTH_SHORT).show();
            previousVolume=currentVolume;

            Intent update = new Intent(context, MainMapScreen.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, update, 0);
            //PendingIntent pendingIntent = PendingIntent.getService(context, 0, update, 0);

            try {
                pendingIntent.send();
            } catch (PendingIntent.CanceledException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //new Launch();
        }

    }
}



