package com.example.gurpreetsingh.project.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.example.gurpreetsingh.project.MainMapScreen;
import com.example.gurpreetsingh.project.R;
import com.example.gurpreetsingh.project.framework.service.ShakeService;
import com.example.gurpreetsingh.project.framework.service.VolumeService;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

public class AppSettings extends AppCompatActivity {
    Button continueButton;
    Button myFab;
    Switch switchFake, switchAuto, switchShake, switchQuick;
    Intent intentVolumeService, intentShakeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);

        Toolbar toolbar4 = (Toolbar) findViewById(R.id.toolbarSettings);
        setSupportActionBar(toolbar4);
        toolbar4.setTitle(getString(R.string.action_settings));
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //switchAuto=(Switch)findViewById(R.id.switchAuto);
        //switchFake=(Switch)findViewById(R.id.switchFake);
        switchShake=(Switch)findViewById(R.id.switchShake);
        switchQuick=(Switch)findViewById(R.id.switchQuick);

        switchQuick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                intentVolumeService = new Intent(AppSettings.this,VolumeService.class);
                if(switchQuick.isChecked())  {
                    editor.putString("QuickStartState", "checked");
                    Log.d("AppSettings","checked");
                    startService(intentVolumeService);
                }
                else {
                    editor.putString("QuickStartState", "unchecked");
                    stopService(intentVolumeService);
                    Log.d("AppSettings"," not checked");
                }
                editor.apply();
            }
        });

        switchShake.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                intentShakeService = new Intent(AppSettings.this, ShakeService.class);
                if (switchShake.isChecked()) {
                    Log.d("AppSettings", "checked");
                    editor.putString("ShakeState", "checked");
                    startService(intentShakeService);
                } else {
                    editor.putString("ShakeState", "unchecked");
                    stopService(intentShakeService);
                    Log.d("AppSettings", " not checked");
                }
                editor.apply();
            }
        });

        myFab = (Button) findViewById(R.id.fab4);
        myFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AppSettings.this,MainMapScreen.class);
                startActivity(i);
            }
        });
    }
}
