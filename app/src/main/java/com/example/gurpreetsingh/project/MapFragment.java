package com.example.gurpreetsingh.project;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationSource,
        com.google.android.gms.location.LocationListener {

    private static final String TAG = MapFragment.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 1;
    private ProgressDialog progressBar;
    // flag for network status
    public boolean isNetworkEnabled = false;
    public boolean canGetLocation = false;
    // The minimum distance to change Updates in meters
    public final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    public final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    public FloatingActionButton fabHelp, fabShare;
    public Marker mMarker;
    MapView mMapView;
    public GoogleMap mMap;
    public LocationManager locationManager;
    public Location mLastLocation;
    private double latitude;
    private double longitude;
    public String address;
    private GoogleApiClient mGoogleApiClient;

    private static final LocationRequest REQUEST = LocationRequest.create()
            .setFastestInterval(60000)   // in milliseconds
            .setInterval(180000)         // in milliseconds
            //    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            .setPriority(LocationRequest.PRIORITY_LOW_POWER);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_map, container, false);
        fabHelp = (FloatingActionButton) v.findViewById(R.id.fabHelp);
        fabShare = (FloatingActionButton) v.findViewById(R.id.fabShare);

        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            // First we need to check availability of play services
            if (new PlaystoreHelper().checkPlayServices(getActivity())) {
                // Building the GoogleApi client
                buildGoogleApiClient();
                mGoogleApiClient.connect();
            }
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
mMapView.getMapAsync(this);

        if (savedInstanceState == null) {
            Log.d("test", "first time on create view of map ");
        } else {
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
        progressBar.setMessage("Fetching current mLastLocation ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        Log.d("test", "after setupmap on activity created");
        fabHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test", "onclick");
                displayLocationUI();
                //PerformAction.textChooser(getActivity(), address);
                //PerformAction.message("121", address);
            }
        });

        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test", "onclick");
                PerformAction.textChooser(getActivity(), address);
                //PerformAction.message("121", address);
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
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
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

        //progressBar.show();
        final Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                /*if (mGoogleApiClient.isConnected()) {
                    displayLocationUI();
                }*/
            }
        }, 1500);

    }


    void displayLocationUI() {
        //making share fab invisible and helpfab visible
        fabShare.setVisibility(View.GONE);
        fabShare.animate().translationYBy(250);
        //fabHelp.setVisibility(View.VISIBLE);
        //getCurrentLocation();
        getLocation();

    }

    private void getLocation() {
        if (!isGPSEnabled()) {
            progressBar.dismiss();
            showLocationSettingsAlert();
            ///locationDialog = DialogManager.showLocationDialog(this);
        }
        //  check for run time permissions if gps is enabled

        else {
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_DENIED && ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {

                ActivityCompat.requestPermissions(getActivity(), new String[]
                                {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_REQUEST_CODE);
                Log.d(TAG, "onClick: should allow permission");

            } else {
                mLastLocation = LocationServices.FusedLocationApi
                        .getLastLocation(mGoogleApiClient);

                if (mLastLocation != null) {
                    latitude = mLastLocation.getLatitude();
                    longitude = mLastLocation.getLongitude();

                    Log.d(TAG, "getLocation: lat " + latitude);
                    Log.d(TAG, "getLocation: lng " + longitude);

                    address = getAddress(latitude, longitude);
                    updateMap();
/*

                    SharedPreferenceData.getInstance().saveLatitude(Double.valueOf(latitude).toString());
                    SharedPreferenceData.getInstance().saveLongitude(Double.valueOf(longitude).toString());
*/

                  //  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12));


///                    if (!ConnectivityUtil.isConnected(getContext())) {
/////                        SnackBarManager.renderNoConnectionSnackBar(NearbyOfferActivity.this);
///                    }


                    //UPDATE LOCATION IN TOOLBAR OR SOMEWHERE


                } else {
/*
                    Snackbar mSnackBar = SnackBarManager.getSnackbar(findViewById(android.R.id.content),
                            "Couldn't get the mLastLocation.", NearbyOfferActivity.this);
                    mSnackBar.setAction("TRY AGAIN", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mGoogleApiClient.connect();
                            getLocation();
                        }
                    });
                    mSnackBar.show();*/
                    Log.d(TAG, "getLocation: Couldn't get the mLastLocation. Make sure mLastLocation is enabled on the device");
                }
            }
        }
    }
/*

     void getCurrentLocation() {
        Log.d("test", "getcurrentloc");
        if (ContextCompat.checkSelfPermission(SafetyCareApplication.getAppContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        try {
            Log.d("test", "try");

            locationManager = (LocationManager) SafetyCareApplication.getAppContext().getSystemService(LOCATION_SERVICE);

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
*/
/*
            if(!isNetworkEnabled) {
                // no network provider is enabled
                //Toast.makeText(getActivity(), "Network and GPS both are disabled", Toast.LENGTH_SHORT).show();
                // can't get mLastLocation
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                progressBar.dismiss();
                showSettingsAlert();
            }
            else *//*

                if (!isConnectedToInternet() ){
                    canGetLocation=false;
                    progressBar.dismiss();
                    showSettingsAlert();
                }
                else if (!isGPSEnabled() && !isNetworkEnabled){
                    canGetLocation=false;
                    progressBar.dismiss();
                    showLocationSettingsAlert();
                }  else {
                    canGetLocation = true;
                    // First get mLastLocation from Network Provider
                    if (isNetworkEnabled) {

                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                mLocationListener);
                        Log.d("test", "Network");
                        if (locationManager != null) {
                            Log.d("test", "inside n/w enabled : mLastLocation manager not null");
                            mLastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (mLastLocation != null) {
                                Log.d("test", "mLastLocation not null");
                                latitude = mLastLocation.getLatitude();
                                longitude = mLastLocation.getLongitude();
                            }
                        }
                    }
                    // if GPS Enabled get lat/long using GPS Services
                    if (isGPSEnabled()) {
                        Log.d("test", "GPS Enabled");
                        if (mLastLocation == null) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                    mLocationListener);
                            if (locationManager != null) {
                                Log.d("test", "mLastLocation manager not null");
                                mLastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (mLastLocation != null) {
                                    //Toast.makeText(SafetyCareApplication.getAppContext(), "Displaying last known mLastLocation", Toast.LENGTH_SHORT).show();
                                    latitude = mLastLocation.getLatitude();
                                    longitude = mLastLocation.getLongitude();
                                }
                            }
                        }
                    }

                    if (latitude != 0.0 && longitude != 0.0) {
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
            Toast.makeText(getActivity(), "Unable to fetch the current mLastLocation. Please check your network connection " +
                    "and GPS and try again.", Toast.LENGTH_LONG).show();
            progressBar.dismiss();
            Log.d("MapFragment","exc : "+e);
            e.printStackTrace();
        }
    }
*/

    void updateMap() {
        //  removing previous markers
        if (mMap != null) {
            mMap.clear();

            Log.d("test", "add marker");
            mMarker = mMap.addMarker(new MarkerOptions().position(
                    new LatLng(latitude, longitude)).title("" + address));//

            Log.d("test", "set info window adapter");
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

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
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 20));

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
        else{
            Log.d(TAG, "updateMap: mmap is null");
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
            Toast.makeText(getActivity(), "Unable to fetch the current mLastLocation", Toast.LENGTH_SHORT).show();
        }
    }
*/

    private String getAddress(double lat, double log) {
        // TODO Auto-generated method stub
        Geocoder geocoder = new Geocoder(getActivity(), Locale.ENGLISH);
        String ret;
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, log, 1);
            if (addresses != null) {
                int countSpaces = 0;
                Address returnedAddress = addresses.get(0);
                Log.d(TAG, "getAddress: addresses "+returnedAddress);
                StringBuilder strReturnedAddress = new StringBuilder();
                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    if (returnedAddress.getAddressLine(i).equals(" "))
                        countSpaces++;
                    if (countSpaces > 3) {
                        strReturnedAddress.append(" "+returnedAddress.getAddressLine(i) + "\n");
                        countSpaces = 0;
                    } else {
                        strReturnedAddress.append(" "+returnedAddress.getAddressLine(i));
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
            ret = "Last known mLastLocation : Latitude : " + latitude + ", Longitude : " + longitude;
        }
        return ret;
    }

    public boolean isConnectedToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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

    public final LocationListener mLocationListener = new LocationListener() {
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(mMap == null) {
            Log.d(TAG, "onMapReady: mmap is null");
        }
        if(googleMap == null) {
            Log.d(TAG, "onMapReady: google map is null");
        }
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(getActivity(), new String[]
                            {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_REQUEST_CODE);
            return;
        }

        mMap.setLocationSource(this);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);
       /* if (mGoogleApiClient.isConnected()) {
            displayLocationUI();
        } else {
            mGoogleApiClient.connect();
        }*/

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {

    }

    @Override
    public void deactivate() {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_DENIED && ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {


            ActivityCompat.requestPermissions(getActivity(), new String[]
                            {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_REQUEST_CODE);

        }
        else {
            Log.d(TAG, "onConnected: google client connected and permission granted");
            displayLocationUI();
            /*LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient,
                    REQUEST,
                    this);  // LocationListener*/
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private Boolean isGPSEnabled() {
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return true;
        }
        return false;
    }

}

