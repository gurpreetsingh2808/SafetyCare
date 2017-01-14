package com.example.gurpreetsingh.project.ui.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.example.gurpreetsingh.project.R;


public class SignupActivity extends AppCompatActivity {

    AnimationDrawable anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Set the status bar to dark-semi-transparentish
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
/*
        RelativeLayout container = (RelativeLayout) findViewById(R.id.rlSignup);

        anim = (AnimationDrawable) container.getBackground();
        anim.setEnterFadeDuration(6000);
        anim.setExitFadeDuration(2000);*/
    }
/*

    @Override
    protected void onResume() {
        super.onResume();
        if (anim != null && !anim.isRunning())
            anim.start();
    }

    // Stopping animation:- stop the animation on onPause.
    @Override
    protected void onPause() {
        super.onPause();
        if (anim != null && anim.isRunning())
            anim.stop();
    }
*/

    public void signUpUsingGoogle(View view) {
        moveToNextActivity();
    }

    public void signUpUsingFacebook(View view) {
        moveToNextActivity();
    }

    private void moveToNextActivity() {
        Intent i = new Intent(SignupActivity.this, AddContactsActivity.class);
        startActivity(i);
        finish();
    }
}
