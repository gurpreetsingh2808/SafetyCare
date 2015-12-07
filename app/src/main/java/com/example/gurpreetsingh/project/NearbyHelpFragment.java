package com.example.gurpreetsingh.project;


import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class NearbyHelpFragment extends Fragment {

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

    public static Double latitude,longitude;
    GoogleMap googleMap;
    SupportMapFragment supportMapFragment;
    Marker mMarker;
    String address;

    public NearbyHelpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_nearby_help, container, false);
        Log.d("menu", "on create view");
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("menu", "on resume");
        setUpMap();

        latitude = MapFragment.latitude;
        longitude = MapFragment.longitude;
        Log.d("menu", "on resume nearby " + latitude + " " + longitude);

        // if location was not retrieved successfully in mapfragment due to disabled location
        if(latitude==null && longitude==null)  {
            getCurrentLocation();
            Log.d("menu", "on resume nearby " + latitude + " " + longitude);
        }
        else {
            // not working due to non static-made it static check
            //address = MapFragment.getInstance().GetAddress(latitude, longitude);
            //Log.d("menu", "" + address);
            mMarker = googleMap.addMarker(new MarkerOptions().position(
                    new LatLng(latitude, longitude)).title("You are here"));//
            mMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            mMarker.showInfoWindow();

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 14));
        }

              //  apply parsed data
        //String url = "https://maps.googleapis.com/maps/api/place/search/json?location=28.729007,%2077.129470&rankby=distance&types=police&sensor=false&key=AIzaSyD-RMFX5nVH5riaxpHGr4uwNEb2hiernDE";

        //sendJsonRequest("Police Station", url);
    }

    void setUpMap()  {
        try {
            if (googleMap == null) {
                Log.d("menu", "map is null");
                googleMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.nearbyMap)).getMap();

                Log.d("menu", "map instantiated");
            }
        } catch (Exception e) {
            Log.d("menu", "map exception "+e);

        }
        //googleMap.setMyLocationEnabled(true);
        //googleMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    void getCurrentLocation()   {

        if ( ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        try {
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
                //address=GetAddress(latitude, longitude);
                //Log.d("test",""+address);
                mMarker=googleMap.addMarker(new MarkerOptions().position(
                        new LatLng(latitude, longitude)).title("" + address));//
                mMarker.showInfoWindow();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 16));
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "Unable to fetch the current location", Toast.LENGTH_SHORT).show();
            Log.d("menu","map exception "+e);
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
            Toast.makeText(getActivity(), "Location changed", Toast.LENGTH_SHORT).show();
            getCurrentLocation();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Toast.makeText(getActivity(), "Status changed to "+status, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getActivity(), "Provider is now enabled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getActivity(), "Provider is now disabled", Toast.LENGTH_SHORT).show();
        }
    };


RequestQueue mRequestQueue = VolleySingleton.getInstance().getmRequestQueue();

    void sendJsonRequest(final String choice, String url)  {

        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET,
                url,
                new Response.Listener<JSONObject>()  {
                    @Override
                    public void onResponse(JSONObject response) {
                        //parsing request
                        Log.d("menu", "parsing req");
                        parseJSONResponse(response, choice);
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

    private void parseJSONResponse(JSONObject response, String choice)   {

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
                if(choice.equals("Police Station")) {
                    Log.d("menu", "choice is police");

                    JSONObject nearbyObject = jsonArrayResult.getJSONObject(i);
                    JSONObject nearbyObjectLocation = nearbyObject.getJSONObject("geometry").getJSONObject("location");
                    //Log.d("menu", "objects ok");
                    name[i] = nearbyObject.getString("name");
                    lat[i] = Double.parseDouble(nearbyObjectLocation.getString("lat"));
                    lng[i] = Double.parseDouble(nearbyObjectLocation.getString("lng"));


                    //data.append(lat + " " +lng + "\n");
                    //Log.d("menu", "data appended");
                }
                else if(choice.equals("Train Station")) {

                    JSONObject nearbyObject = jsonArrayResult.getJSONObject(i);
                    JSONObject nearbyObjectLocation = nearbyObject.getJSONObject("geometry").getJSONObject("location");
                    //Log.d("menu", "objects ok");
                    name[i] = nearbyObject.getString("name");
                    lat[i] = Double.parseDouble(nearbyObjectLocation.getString("lat"));
                    lng[i] = Double.parseDouble(nearbyObjectLocation.getString("lng"));
                    //JSONObject nearbyTrainStation = jsonArrayResult.getJSONObject(i);
                    //String name = nearbyTrainStation.getString("name");
                    //data.append(name + "\n");
                }
                else if(choice.equals("Subway Station")) {
                    JSONObject nearbyObject = jsonArrayResult.getJSONObject(i);
                    JSONObject nearbyObjectLocation = nearbyObject.getJSONObject("geometry").getJSONObject("location");
                    //Log.d("menu", "objects ok");
                    name[i] = nearbyObject.getString("name");
                    lat[i] = Double.parseDouble(nearbyObjectLocation.getString("lat"));
                    lng[i] = Double.parseDouble(nearbyObjectLocation.getString("lng"));
                    //JSONObject nearbySubwayStation = jsonArrayResult.getJSONObject(i);
                    //String name = nearbySubwayStation.getString("name");
                    //data.append(name + "\n");
                }
                else if(choice.equals("Bus Station")) {
                    JSONObject nearbyObject = jsonArrayResult.getJSONObject(i);
                    JSONObject nearbyObjectLocation = nearbyObject.getJSONObject("geometry").getJSONObject("location");
                    //Log.d("menu", "objects ok");
                    name[i] = nearbyObject.getString("name");
                    lat[i] = Double.parseDouble(nearbyObjectLocation.getString("lat"));
                    lng[i] = Double.parseDouble(nearbyObjectLocation.getString("lng"));
                    //JSONObject nearbyBusStation = jsonArrayResult.getJSONObject(i);
                    //String name = nearbyBusStation.getString("name");
                    //data.append(name + "\n");
                }
                else if(choice.equals("Gas Station")) {
                    JSONObject nearbyObject = jsonArrayResult.getJSONObject(i);
                    JSONObject nearbyObjectLocation = nearbyObject.getJSONObject("geometry").getJSONObject("location");
                    //Log.d("menu", "objects ok");
                    name[i] = nearbyObject.getString("name");
                    lat[i] = Double.parseDouble(nearbyObjectLocation.getString("lat"));
                    lng[i] = Double.parseDouble(nearbyObjectLocation.getString("lng"));
                    //JSONObject nearbyGasStation = jsonArrayResult.getJSONObject(i);
                    //String name = nearbyGasStation.getString("name");
                    //data.append(name + "\n");
                }
                else if(choice.equals("Hospital")) {
                    JSONObject nearbyObject = jsonArrayResult.getJSONObject(i);
                    JSONObject nearbyObjectLocation = nearbyObject.getJSONObject("geometry").getJSONObject("location");
                    //Log.d("menu", "objects ok");
                    name[i] = nearbyObject.getString("name");
                    lat[i] = Double.parseDouble(nearbyObjectLocation.getString("lat"));
                    lng[i] = Double.parseDouble(nearbyObjectLocation.getString("lng"));
                    //JSONObject nearbyHospital = jsonArrayResult.getJSONObject(i);
                    //String name = nearbyHospital.getString("name");
                    //data.append(name + "\n");
                }
                else if(choice.equals("Doctor")) {
                    JSONObject nearbyObject = jsonArrayResult.getJSONObject(i);
                    JSONObject nearbyObjectLocation = nearbyObject.getJSONObject("geometry").getJSONObject("location");
                    //Log.d("menu", "objects ok");
                    name[i] = nearbyObject.getString("name");
                    lat[i] = Double.parseDouble(nearbyObjectLocation.getString("lat"));
                    lng[i] = Double.parseDouble(nearbyObjectLocation.getString("lng"));
                    //JSONObject nearbyDoctor = jsonArrayResult.getJSONObject(i);
                    //String name = nearbyDoctor.getString("name");
                    //data.append(name + "\n");
                }
                else if(choice.equals("Taxi Stand")) {
                    JSONObject nearbyObject = jsonArrayResult.getJSONObject(i);
                    JSONObject nearbyObjectLocation = nearbyObject.getJSONObject("geometry").getJSONObject("location");
                    //Log.d("menu", "objects ok");
                    name[i] = nearbyObject.getString("name");
                    lat[i] = Double.parseDouble(nearbyObjectLocation.getString("lat"));
                    lng[i] = Double.parseDouble(nearbyObjectLocation.getString("lng"));
                    //JSONObject nearbyTaxiStand = jsonArrayResult.getJSONObject(i);
                    //String name = nearbyTaxiStand.getString("name");
                    data.append(name + "\n");
                }
                /*JSONObject nearbyObject = jsonArrayResult.getJSONObject(i);
                JSONObject nearbyObjectLocation = nearbyObject.getJSONObject("geometry").getJSONObject("location");
                //Log.d("menu", "objects ok");
                name[i] = nearbyObject.getString("name");
                lat[i] = Double.parseDouble(nearbyObjectLocation.getString("lat"));
                lng[i] = Double.parseDouble(nearbyObjectLocation.getString("lng"));
                */
            }
            // displaying result in map after reading complete response
            addOnMap(len, name, lat, lng);

            Log.d("menu", "toast");
            //Toast.makeText(getActivity(), data.toString(),Toast.LENGTH_SHORT).show();
            Log.d("menu", data.toString());

        }
        catch (JSONException e){
            Log.d("menu", "json exception "+e);
        }

    }


void addOnMap(int len, String[] name, Double[] lat, Double[] lng)  {
    for (int i=0; i<len; i++)  {
        try {
            googleMap.addMarker(new MarkerOptions().position(new LatLng(lat[i], lng[i])).title(""+name[i]));
            mMarker.showInfoWindow();
        }
        catch (Exception e)  {
            Log.d("menu","exception "+e);
        }
    }
}


}
