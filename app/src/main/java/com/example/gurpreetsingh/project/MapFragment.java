package com.example.gurpreetsingh.project;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment {

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    boolean canGetLocation = false;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    FloatingActionButton fabHelp;
    Marker mMarker;
    GoogleMap googleMap;
    LocationManager locationManager;
    Location location;

    public static Double longitude,latitude;
    public static String address;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_map, container, false);
        if (savedInstanceState == null) {
            Log.d("test", "first time on create view of map ");
        }
        else if (savedInstanceState != null) {
            Log.d("test", "save instance on create view");
            //mMapFragment = getSupportFragmentManager().getFragment(savedInstanceState,"MY_FRAGMENT");
            //setFragment();
        }
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("test", "on activity created");

        //if(mMarker!=null) {
        //    mMarker.remove();
        //}
        setUpMap();
        /*
        getCurrentLocation();
        if(!canGetLocation){
            showSettingsAlert();
        }
        */

        Log.d("test", "after setupmap on activity created");
        fabHelp = (FloatingActionButton) getView().findViewById(R.id.fabHelp);
        fabHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test", "onclick");
                PerformAction.textChooser(getActivity(), address);
                //PerformAction.message("121", address);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("test", "on pause");
        //googleMap=null;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("test", "onresume");
        //if(mMarker!=null) {
        //    mMarker.remove();
        //}
        getCurrentLocation();
        if(!canGetLocation){
            showSettingsAlert();
        }
    }

    void setUpMap()  {
        try {
            if (googleMap == null) {
                Log.d("test", "map is null");
                //googleMap = ((SupportMapFragment) mMapFragment.getChildFragmentManager().findFragmentById(R.id.map)).getMap();
                googleMap = ((SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map)).getMap();
                Log.d("test", "map instantiated");
            }
            else{
                Log.d("test", "map not null");
            }
        } catch (Exception e) {
            Log.d("test", e.toString());
        }
        //googleMap.setMyLocationEnabled(true);
        //googleMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    void getCurrentLocation()   {
Log.d("test","getcurrentloc");
        if ( ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        try {
            Log.d("test","try");

            locationManager = (LocationManager) MyApplication.getAppContext().getSystemService(MyApplication.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                //Toast.makeText(getActivity(), "No network provider is enabled", Toast.LENGTH_SHORT).show();
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings

            }
            else {
                canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,
                            mLocationListener);
                    Log.d("test", "Network");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                mLocationListener);
                        Log.d("test", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
                Log.d("test","try done");
                address=GetAddress(latitude, longitude);
                Log.d("test",""+address);
                mMarker=googleMap.addMarker(new MarkerOptions().position(
                        new LatLng(latitude, longitude)).title("" + address));//
                mMarker.showInfoWindow();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 20));
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Unable to fetch the current location", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
/*
    void getCurrentLocation() {
        Location myLocation = googleMap.getMyLocation();
        Log.d("test", "getlocation "+myLocation);
        if (myLocation != null) {
            double dLatitude = myLocation.getLatitude();
            double dLongitude = myLocation.getLongitude();
            Log.d("test", " : " + dLatitude);
            Log.d("test", " : " + dLongitude);
              address=GetAddress(dLatitude,dLongitude);
            Log.d("test",""+address);
            googleMap.addMarker(new MarkerOptions().position(
                    new LatLng(dLatitude, dLongitude)).title("" + address));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dLatitude, dLongitude), 20));

        } else {
            Toast.makeText(getActivity(), "Unable to fetch the current location", Toast.LENGTH_SHORT).show();
        }
    }
*/

     String GetAddress(double lat, double log) {
        // TODO Auto-generated method stub
        Geocoder geocoder = new Geocoder(getActivity(), Locale.ENGLISH);
        String ret = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, log, 1);
            if(addresses != null) {
                int countSpaces=0;
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("Address: ");
                for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
                    if(returnedAddress.getAddressLine(i)==" ")
                        countSpaces++;
                    if(countSpaces>3){
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)+"\n");
                        countSpaces=0;
                    }
                    else{
                        strReturnedAddress.append(returnedAddress.getAddressLine(i));
                    }
                }
                ret = strReturnedAddress.toString();
            }
            else{
                ret = "No Address returned!";
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ret = "Can't get Address!";
        }
        return ret;
    }

    /**
     * Function to show settings alert dialog
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog.setTitle("Attention");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

private final LocationListener mLocationListener = new LocationListener() {
    @Override
    public void onLocationChanged(final Location location) {
        //your code here
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
};
}

