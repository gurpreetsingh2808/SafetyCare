package com.example.gurpreetsingh.project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.gurpreetsingh.project.ui.activity.SignupActivity;

public class SplashScreen extends AppCompatActivity {
        //extends AwesomeSplash {

    public static final String DEFAULT = "N/A";
    String email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Set the status bar to dark-semi-transparentish
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkRegistraion();
                finish();
            }
        },1000);
    }

/*
    @Override
    public void initSplash(ConfigSplash configSplash) {

        //Customize Circular Reveal
        configSplash.setBackgroundColor(R.color.white); //any color you want form colors.xml

        //Customize Logo
        configSplash.setLogoSplash(R.drawable.app_logo_two); //or any other drawable
        configSplash.setAnimLogoSplashDuration(1000); //int ms
        configSplash.setAnimLogoSplashTechnique(Techniques.Bounce);
    }

    @Override
    public void animationsFinished() {
        Intent mIntent=new Intent(SplashScreen.this,NumberVerify.class);
        startActivity(mIntent);
        finish();
    }
*/

    void checkRegistraion() {

        //  getting previously stored registration data
        SharedPreferences sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("Email", DEFAULT);
        password = sharedPreferences.getString("Password", DEFAULT);

        //  checking the collected data and putting it to screen
        if (email.equals(DEFAULT) || password.equals(DEFAULT)) {
            Toast.makeText(getApplicationContext(), "You have not registered yet. Please register first.", Toast.LENGTH_SHORT).show();
            Intent mIntent=new Intent(SplashScreen.this,SignupActivity.class);
            startActivity(mIntent);
        } else {
            //Intent mIntent=new Intent(SplashScreen.this,AppSettings.class);
            Intent mIntent=new Intent(SplashScreen.this,MainMapScreen.class);
            startActivity(mIntent);
        }
    }
}
