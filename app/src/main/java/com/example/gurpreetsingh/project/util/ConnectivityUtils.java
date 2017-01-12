package com.example.gurpreetsingh.project.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.gurpreetsingh.project.SafetyCareApplication;

/**
 * Created by Gurpreet on 12-01-2017.
 */

public class ConnectivityUtils {

    public static boolean isConnectedToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) SafetyCareApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

}
