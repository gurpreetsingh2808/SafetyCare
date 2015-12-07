/*
package com.example.gurpreetsingh.project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class ButtonsReceiver extends BroadcastReceiver {
    public ButtonsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d("broadcast", "working");

        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);                         // 3
            Log.d("broadcast", "state: "+state);

            String msg = "Phone state changed to " + state;
            Log.d("broadcast", "message: "+msg);


            if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {                                   // 4
                String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);  // 5
                msg += ". Incoming number is " + incomingNumber;
                Log.d("broadcast", "incoming: "+incomingNumber);
                Log.d("broadcast", "msg: "+msg);

                // Call a service, since this could take a few seconds
                context.startService(new Intent(context, AutoAnswerIntentService.class));


                // TODO This would be a good place to "Do something when the phone rings" <img src="http://androidlabs.org/wp-includes/images/smilies/icon_wink.gif" alt=";-)" class="wp-smiley">

            }
            Log.d("broadcast", "msg: "+msg);

            //Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
        catch (Exception e)  {
            Log.d("broadcast", "exception "+e);
        }



        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (KeyEvent.KEYCODE_VOLUME_DOWN == event.getKeyCode()) {
                // Handle key press.
                Toast.makeText(context, "volume down broadcast working",Toast.LENGTH_SHORT).show();
            }
        }


        throw new UnsupportedOperationException("Not yet implemented");
    }
}
*/