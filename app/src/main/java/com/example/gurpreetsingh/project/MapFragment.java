package com.example.gurpreetsingh.project;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment {

    private static ProgressDialog progressBar;

    // flag for GPS status
    public static boolean isGPSEnabled = false;

    // flag for network status
    public static boolean isNetworkEnabled = false;

    public static boolean canGetLocation = false;

    // The minimum distance to change Updates in meters
    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    public static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    public static FloatingActionButton fabHelp, fabShare;
    public static Marker mMarker;
    public static GoogleMap googleMap;
    public static LocationManager locationManager;
    public static Location location;

    public static Double longitude, latitude;
    public static String address;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_map, container, false);
        if (savedInstanceState == null) {
            Log.d("test", "first time on create view of map ");
        } else if (savedInstanceState != null) {
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


        progressBar = new ProgressDialog(getContext());
        //progressBar.setCancelable(true);
        progressBar.setMessage("Fetching current location ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //progressBar.setProgress(0);
        //progressBar.setMax(100);

        setUpMap();

        Log.d("test", "after setupmap on activity created");
        fabHelp = (FloatingActionButton) getView().findViewById(R.id.fabHelp);
        fabHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test", "onclick");
                displayLocationUI();
                //PerformAction.textChooser(getActivity(), address);
                //PerformAction.message("121", address);
            }
        });

        fabShare = (FloatingActionButton) getView().findViewById(R.id.fabShare);
        fabShare.setOnClickListener(new View.OnClickListener() {
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
        final Handler mHandler = new Handler();

        //if (!canGetLocation) {
        //    progressBar.dismiss();
        //    showSettingsAlert();
        //}
        progressBar.show();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                displayLocationUI();
            }
        }, 1500);

    }

    void setUpMap() {
        try {
            if (googleMap == null) {
                Log.d("test", "map is null");
                //googleMap = ((SupportMapFragment) mMapFragment.getChildFragmentManager().findFragmentById(R.id.map)).getMap();
                googleMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
                Log.d("test", "map instantiated");
            } else {
                Log.d("test", "map not null");
            }
        } catch (Exception e) {
            Log.d("test", e.toString());
        }
        //googleMap.setMyLocationEnabled(true);
        //googleMap.getUiSettings().setMyLocationButtonEnabled(true);
    }


    void displayLocationUI() {
        //making share fab invisible and helpfab visible
        fabShare.setVisibility(View.GONE);
        fabShare.animate().translationYBy(250);
        //fabHelp.setVisibility(View.VISIBLE);
        getCurrentLocation();

    }

     void getCurrentLocation() {
        Log.d("test", "getcurrentloc");
        if (ContextCompat.checkSelfPermission(MyApplication.getAppContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        try {
            Log.d("test", "try");

            locationManager = (LocationManager) MyApplication.getAppContext().getSystemService(MyApplication.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
/*
            if(!isNetworkEnabled) {
                // no network provider is enabled
                //Toast.makeText(getActivity(), "Network and GPS both are disabled", Toast.LENGTH_SHORT).show();
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                progressBar.dismiss();
                showSettingsAlert();
            }
            else */
                if (!isConnectedToInternet() ){
                    canGetLocation=false;
                    progressBar.dismiss();
                    showSettingsAlert();
                }
                else if (!isGPSEnabled && !isNetworkEnabled){
                    canGetLocation=false;
                    progressBar.dismiss();
                    showLocationSettingsAlert();
                }  else {
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
                            Log.d("test", "inside n/w enabled : location manager not null");
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                Log.d("test", "location not null");
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                    // if GPS Enabled get lat/long using GPS Services
                    if (isGPSEnabled) {
                        Log.d("test", "GPS Enabled");
                        if (location == null) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                    mLocationListener);
                            if (locationManager != null) {
                                Log.d("test", "location manager not null");
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    //Toast.makeText(MyApplication.getAppContext(), "Displaying last known location", Toast.LENGTH_SHORT).show();
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }
                    }

                    if (latitude != null && longitude != null) {
                        Log.d("MapFragment", "lat : " + latitude + "long : " + longitude);
                        address = GetAddress(latitude, longitude);
                        Log.d("test", "" + address);

                    } else {
                        getCurrentLocation();
                    }
                    progressBar.dismiss();
                    updateMap();
                }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Unable to fetch the current location. Please check your network connection " +
                    "and GPS and try again.", Toast.LENGTH_LONG).show();
            progressBar.dismiss();
            Log.d("MapFragment","exc : "+e);
            e.printStackTrace();
        }
    }

    void updateMap() {
        //  removing previous markers
        googleMap.clear();
        googleMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();

        Log.d("test", "add marker");
        mMarker = googleMap.addMarker(new MarkerOptions().position(
                new LatLng(latitude, longitude)).title("" + address));//

        Log.d("test", "set info window adapter");
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                //title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                //TextView snippet = new TextView(getContext());
                //snippet.setTextColor(Color.GRAY);
                //snippet.setText(marker.getSnippet());

                info.addView(title);
                info.setLayoutParams(new LinearLayout.LayoutParams
                        (500, LinearLayout.LayoutParams.WRAP_CONTENT));
                //info.addView(snippet);

                return info;
            }
        });

        mMarker.showInfoWindow();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 20));

        //making help fab invisible and share fab visible
        //fabHelp.setVisibility(View.GONE);
        //fabShare.setVisibility(View.VISIBLE);

        fabHelp.animate()
                .translationYBy(250)
                .setInterpolator(new AnticipateOvershootInterpolator())
                .setDuration(500)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        fabShare.setVisibility(View.VISIBLE);
                        fabShare.animate().translationY(0).setDuration(500).setInterpolator(new AnticipateOvershootInterpolator());
                    }
                });
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
        String ret;
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, log, 1);
            if (addresses != null) {
                int countSpaces = 0;
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    if (returnedAddress.getAddressLine(i).equals(" "))
                        countSpaces++;
                    if (countSpaces > 3) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i) + "\n");
                        countSpaces = 0;
                    } else {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i));
                    }
                }
                ret = strReturnedAddress.toString();
            } else {
                ret = "No Address returned!";
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(MyApplication.getAppContext(), "Can't get Address. Please check your internet connection.", Toast.LENGTH_LONG).show();
            ret = "Last known location : Latitude : "+latitude+ ", Longitude : "+longitude;
        }
        return ret;
    }

    public boolean isConnectedToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
        }
        return false;
    }

    /**
     * Function to show settings alert dialog
     */
    public void showLocationSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog.setTitle("Attention");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to Location settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
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

    /**
     * Function to show settings alert dialog
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Attention");
        alertDialog.setMessage("Internet is not enabled. Do you want to go to settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
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

    public static final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
            // Toast.makeText(getActivity(), "Location changed", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
           // Toast.makeText(getActivity(), "Status is changed to " + status, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
           // Toast.makeText(getActivity(), "Provider :" + provider + "is now enabled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
           // Toast.makeText(getActivity(), "Provider :" + provider + "is now disabled", Toast.LENGTH_SHORT).show();

        }
    };

}

