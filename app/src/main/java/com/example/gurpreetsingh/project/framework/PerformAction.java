package com.example.gurpreetsingh.project.framework;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Gurpreet Singh on 10/16/2015.
 */
public class PerformAction{

    public static void textChooser(Activity callingActivity,String msg){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, msg);
        callingActivity.startActivity(Intent.createChooser(i,"Share With"));
    }

    // for sending text message
    public static void message(String Recipient, String Msg){
        Log.d("message", "inside message");
        SmsManager smsManager=SmsManager.getDefault();
        Log.d("message", "smsmanager");
        smsManager.sendTextMessage(Recipient, null, Msg, null, null);
        Log.d("message", "sent text msg");

    }

    //for making call
    public static void call(Activity callingActivity, String number){
        Intent i=new Intent(Intent.ACTION_CALL);
        i.setData(Uri.parse("tel:" + number));
        try{
            callingActivity.startActivity(i);
        }
        catch (Exception e){
            Toast.makeText(callingActivity,"Call can't be done",Toast.LENGTH_SHORT).show();
        }
    }

}
