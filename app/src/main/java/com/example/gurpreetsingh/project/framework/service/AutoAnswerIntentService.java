/*
package com.example.gurpreetsingh.project;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
/*
public class AutoAnswerIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS

    public AutoAnswerIntentService() {
        super("AutoAnswerIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            // Let the phone ring for a set delay
            try {
                Thread.sleep(2 * 1000);
            } catch (InterruptedException e) {
                // We don't really care
            }

            // Make sure the phone is still ringing
            TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (tm.getCallState() != TelephonyManager.CALL_STATE_RINGING) {
                return;
            }

            // Answer the phone
            try {
                answerPhoneAidl();
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.d("Gingeroids", "Error trying to answer using telephony service.  Falling back to headset.");
                //answerPhoneHeadsethook(context);
            }
        }
    }

    private void answerPhoneAidl() throws Exception {
        // Set up communication with the telephony service (thanks to Tedd's Droid Tools!)
        TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(TELEPHONY_SERVICE);

        try {
            if (tm == null) {
                // this will be easier for debugging later on
                throw new NullPointerException("tm == null");
            }

            // do reflection magic
            tm.getClass().getMethod("answerRingingCall").invoke(tm);
        } catch (Exception e) {
            // we catch it all as the following things could happen:
            // NoSuchMethodException, if the answerRingingCall() is missing
            // SecurityException, if the security manager is not happy
            // IllegalAccessException, if the method is not accessible
            // IllegalArgumentException, if the method expected other arguments
            // InvocationTargetException, if the method threw itself
            // NullPointerException, if something was a null value along the way
            // ExceptionInInitializerError, if initialization failed
            // something more crazy, if anything else breaks

            // TODO decide how to handle this state
            // you probably want to set some failure state/go to fallback
            Log.e("auto", "Unable to use the Telephony Manager directly.", e);
        }
/*
        Class c = Class.forName(tm.getClass().getName());
        Method m = c.getDeclaredMethod("getITelephony");
        m.setAccessible(true);
        ITelephony telephonyService;
        telephonyService = (ITelephony)m.invoke(tm);

        // Silence the ringer and answer the call!
        telephonyService.silenceRinger();
        telephonyService.answerRingingCall();
        */
/*
    }

}
*/