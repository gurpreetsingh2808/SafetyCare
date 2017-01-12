package com.example.gurpreetsingh.project;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Set the status bar to dark-semi-transparentish
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public void signUpUsingGoogle(View view) {
    }

    public void signUpUsingFacebook(View view) {
    }

    public void signUpUsingTruecaller(View view) {
        moveToNextActivity();
    }

    private void moveToNextActivity() {
        Intent i = new Intent(SignupActivity.this, ReceiverInfo.class);
        startActivity(i);
        finish();
    }
}
