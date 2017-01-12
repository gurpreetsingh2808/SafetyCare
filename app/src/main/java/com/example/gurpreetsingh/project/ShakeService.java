package com.example.gurpreetsingh.project;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ShakeService extends Service {

    private SensorManager mSensorManager;
    private ShakeEventListener mSensorListener;

    public ShakeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
        Log.d("test", "oncreate service started");
        final SharedPreferences sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new ShakeEventListener();

        mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {

            public void onShake() {
                Toast.makeText(getApplicationContext(), "Shake!", Toast.LENGTH_SHORT).show();
                PerformAction.message("121", "default text");
                for(int i=0; i<ReceiverInfo.contactCounter ; i++)  {
                    String contactNumber = sharedPreferences.getString("EmergencyContactNumber["+i+"]","121");
                    PerformAction.message(contactNumber, "I am  in trouble. Do contact me fast.");
                    Log.d("message", "message sent");
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("test", "onstart of service started");
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("test", "destroy service");
        mSensorManager.unregisterListener(mSensorListener);
    }

}