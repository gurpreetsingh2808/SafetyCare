package com.example.gurpreetsingh.project;


import android.*;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.gurpreetsingh.project.component.dialog.DialogManager;
import com.example.gurpreetsingh.project.ui.fragment.MapFragment;
import com.example.gurpreetsingh.project.util.LocationUtils;
import com.example.gurpreetsingh.project.util.PlaystoreHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class NearbyHelpFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = NearbyHelpFragment.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 1;
    RequestQueue mRequestQueue = VolleySingleton.getInstance().getmRequestQueue();
    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    LocationManager locationManager;
    Location location;
    public Location mLastLocation;
    private ProgressDialog progressBar;
    public String address;

    public static Double latitude,longitude;
    GoogleMap googleMap;
    Marker mMarker;
    MapView mMapView;
    private GoogleApiClient mGoogleApiClient;
    private String url = null;


    public NearbyHelpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_nearby_help, container, false);
        Log.d("menu", "on create view");
        mMapView = (MapView) v.findViewById(R.id.mapViewNearby);
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
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {

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
                    sendJsonRequest(url);
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


    void updateUrl(String url) {
        this.url = url;
    }

    void sendJsonRequest(String url)  {

        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET,
                url,
                new Response.Listener<JSONObject>()  {
                    @Override
                    public void onResponse(JSONObject response) {
                        //parsing request
                        Log.d("menu", "parsing req");
                        parseJSONResponse(response);
                        Log.d("menu", "req parsed");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "There was an error retreiving the request", Toast.LENGTH_SHORT).show();
                    }
                });
        Log.d("menu", "json request created");
        mRequestQueue.add(request);
        Log.d("menu", "req added to queue");
    }

    private void parseJSONResponse(JSONObject response)   {

        Log.d("menu", "parse req");
        if(response==null || response.length()==0){
            return;
        }
        try {
            Log.d("menu", "try");
            StringBuilder data = new StringBuilder();
            JSONArray jsonArrayResult = response.getJSONArray("results");
            int len = jsonArrayResult.length();
            Double lat[] = new Double[len];
            Double lng[] = new Double[len];
            String name[] = new String[len];

            Log.d("menu", "for loop");
            for(int i=0; i<len; i++){
                JSONObject nearbyObject = jsonArrayResult.getJSONObject(i);
                JSONObject nearbyObjectLocation = nearbyObject.getJSONObject("geometry").getJSONObject("mLastLocation");
                //Log.d("menu", "objects ok");
                name[i] = nearbyObject.getString("name");
                lat[i] = Double.parseDouble(nearbyObjectLocation.getString("lat"));
                lng[i] = Double.parseDouble(nearbyObjectLocation.getString("lng"));

            }
            // displaying result in map after reading complete response
            addOnMap(len, name, lat, lng);

            Log.d("menu", "toast");
            Toast.makeText(getActivity(), "Data Updated", Toast.LENGTH_SHORT).show();
            Log.d("menu", data.toString());

        }
        catch (JSONException e){
            Log.d("menu", "json exception "+e);
        }

    }


void addOnMap(int len, String[] name, Double[] lat, Double[] lng)  {

    googleMap.clear();
    mMarker = googleMap.addMarker(new MarkerOptions().position(
            new LatLng(latitude, longitude)).title("You are here"));
    mMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
    mMarker.showInfoWindow();
    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 13));

    for (int i=0; i<len; i++) {
        try {
            googleMap.addMarker(new MarkerOptions().position(new LatLng(lat[i], lng[i])).title(""+name[i]));
            //mMarker.showInfoWindow();
        }
        catch (Exception e)  {
            Log.d("menu","exception "+e);
        }
    }
}


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
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

        //this.googleMap.setLocationSource(this);
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        this.googleMap.setMyLocationEnabled(true);


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
            getLocation();
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
