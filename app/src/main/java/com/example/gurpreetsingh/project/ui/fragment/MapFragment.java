package com.example.gurpreetsingh.project.ui.fragment;

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

import com.example.gurpreetsingh.project.component.dialog.DialogManager;
import com.example.gurpreetsingh.project.framework.PerformAction;
import com.example.gurpreetsingh.project.util.LocationUtils;
import com.example.gurpreetsingh.project.util.PlaystoreHelper;
import com.example.gurpreetsingh.project.R;
import com.example.gurpreetsingh.project.SafetyCareApplication;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

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
        fabHelp.setVisibility(View.VISIBLE);
        getLocation();

    }

    private void getLocation() {
        if (!LocationUtils.isGPSEnabled(getActivity())) {
            progressBar.dismiss();
            DialogManager.showLocationSettingsAlert(getActivity());
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

                    address = LocationUtils.getAddress(latitude, longitude);
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
            fabHelp.setVisibility(View.GONE);
            fabShare.setVisibility(View.VISIBLE);

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
        } else {
            Log.d(TAG, "updateMap: mmap is null");
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap == null) {
            Log.d(TAG, "onMapReady: mmap is null");
        }
        if (googleMap == null) {
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

        //mMap.setLocationSource(this);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_DENIED && ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {


            ActivityCompat.requestPermissions(getActivity(), new String[]
                            {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_REQUEST_CODE);

        } else {
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

}

