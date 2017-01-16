package com.example.gurpreetsingh.project.framework.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.example.gurpreetsingh.project.framework.PerformAction;
import com.example.gurpreetsingh.project.framework.ShakeEventListener;
import com.example.gurpreetsingh.project.framework.data.DataStore;
import com.example.gurpreetsingh.project.ui.activity.AddContactsActivity;
import com.lacronicus.easydatastorelib.DatastoreBuilder;

public class ShakeService extends Service {

    private SensorManager mSensorManager;
    private ShakeEventListener mSensorListener;
    private DataStore datastore;

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

        //  initialize data store
        datastore = new DatastoreBuilder(PreferenceManager.getDefaultSharedPreferences(this))
                .create(DataStore.class);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new ShakeEventListener();

       /* mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {

            public void onShake() {
                Toast.makeText(getApplicationContext(), "Shake!", Toast.LENGTH_SHORT).show();
                PerformAction.message("121", "default text");
                for(int i = 0; i< AddContactsActivity.contactCounter ; i++)  {
                    String contactNumber = sharedPreferences.getString("EmergencyContactNumber["+i+"]","121");
                    PerformAction.message(contactNumber, "I am  in trouble. Do contact me fast.");
                    Log.d("message", "message sent");
                }
            }
        });*/
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
