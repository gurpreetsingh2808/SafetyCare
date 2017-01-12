package com.example.gurpreetsingh.project;

import android.app.Activity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by Gurpreet on 09-01-2017.
 */
public class PlaystoreHelper {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    /**
     * Method to verify google play services on the device
     * */
    public boolean checkPlayServices(Activity activity) {

        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(activity.getBaseContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
               // SnackBarManager.renderFailureSnackBar(activity, "This device is not supported.");
            }
            return false;
        }
        return true;
    }
}
