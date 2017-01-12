package com.example.gurpreetsingh.project.util;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import com.example.gurpreetsingh.project.SafetyCareApplication;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Gurpreet on 12-01-2017.
 */

public class LocationUtils {

    public static Boolean isGPSEnabled(Activity activity) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return true;
        }
        return false;
    }

    public static String getAddress(double lat, double lng) {
        // TODO Auto-generated method stub
        Geocoder geocoder = new Geocoder(SafetyCareApplication.getAppContext(), Locale.ENGLISH);
        String ret;
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null) {
                int countSpaces = 0;
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();
                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    if (returnedAddress.getAddressLine(i).equals(" "))
                        countSpaces++;
                    if (countSpaces > 3) {
                        strReturnedAddress.append(" " + returnedAddress.getAddressLine(i) + "\n");
                        countSpaces = 0;
                    } else {
                        strReturnedAddress.append(" " + returnedAddress.getAddressLine(i));
                    }
                }
                ret = strReturnedAddress.toString();
            } else {
                ret = "No Address returned!";
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(SafetyCareApplication.getAppContext(), "Can't get Address. Please check your internet connection.", Toast.LENGTH_LONG).show();
            ret = "Last known mLastLocation : Latitude : " + lat + ", Longitude : " + lng;
        }
        return ret;
    }
}
