package com.example.gurpreetsingh.project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gurpreetsingh.project.ui.fragment.AppSettingsFragment;
import com.example.gurpreetsingh.project.ui.fragment.HelplineNumbers;
import com.example.gurpreetsingh.project.ui.fragment.MapFragment;
import com.example.gurpreetsingh.project.ui.fragment.SharePicture;
import com.example.gurpreetsingh.project.ui.fragment.VoiceCommandsFragment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class MainMapScreen extends AppCompatActivity {

    public static final String DEFAULT = "N/A";
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    private int mNavItemId;
    String title="Ask for help";
    Fragment selectedFragment=null;
    Toolbar mToolbar;
    ImageView pic;
    TextView tvName, tvEmail;
    String email, name, url, choice=null;
    private static MainMapScreen mainMapScreenInstance;
    private static final String PLACES_BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";

    public static AppCompatActivity getActivity(){
          return mainMapScreenInstance;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map_screen);

        mainMapScreenInstance=this;

        // unlock screen if it is locked
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        loadData();

        Log.d("mainmapscreen","image xml found");

        // for orientation changes
        if (savedInstanceState == null) {
            setUpToolbar();
            //  creating fragment object first time
            setFragment();
        } else if (savedInstanceState != null) {
            title=savedInstanceState.getString("TOOLBAR_TITLE","Ask for help");
            Log.d("save",title);
            setUpToolbar();
            selectedFragment = getSupportFragmentManager().getFragment(savedInstanceState,"MY_FRAGMENT");
            setFragment();
        }

        mNavigationView = (NavigationView) findViewById(R.id.myNavBar);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Closing drawer on item click
                //drawerLayout.closeDrawers();
                mDrawerLayout.closeDrawer(GravityCompat.START);

                mNavItemId = menuItem.getItemId();
                // navigation opens new activity
                //so we don't want navigation on tool bar
                title = (String) menuItem.getTitle();
                //toolbar5.setTitle(title);

                // to avoid displaying navigation or camera in toolbar
                if (title.equals("Navigation")) {
                    // update highlighted item in the navigation menu
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                setUpToolbar();

                // allow some time after closing the drawer before performing real navigation
                // so the user can see what is happening


                /////    an event occurs and you want to perform a menu update, you must call
                ////      invalidateOptionsMenu() to request that the system call onPrepareOptionsMenu()
                invalidateOptionsMenu();

                if (mNavItemId == R.id.navigation_item_1) {
                    selectedFragment = new MapFragment();
                    setFragment();
                    showSnackbar(title + " pressed");
                } else if (mNavItemId == R.id.navigation_item_2) {
                    selectedFragment = new VoiceCommandsFragment();
                    setFragment();
                    showSnackbar(title + " pressed");
                } else if (mNavItemId == R.id.navigation_item_3) {
                    selectedFragment = new HelplineNumbers();
                    setFragment();
                    showSnackbar(title + " pressed");
                } else if (mNavItemId == R.id.navigation_item_4) {
                    //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + latitude + "," + longitude));
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    //showSnackbar(title + " pressed");
                } else if (mNavItemId == R.id.navigation_item_5) {
                    selectedFragment = new SharePicture();
                    setFragment();
                    showSnackbar(title + " pressed");
                } else if (mNavItemId == R.id.navigation_item_6) {
                    selectedFragment = new NearbyHelpFragment();
                    setFragment();
                    showSnackbar(title + " pressed");
                }/*else if (mNavItemId == R.id.navigation_item_7) {
                    Intent intent = new Intent(MainMapScreen.this, NearbyPlaces.class);
                    startActivity(intent);
                    //selectedFragment = new NearbyHelpFragment();
                    //setFragment();

                    showSnackbar(title + " pressed");
                }*/ else if (mNavItemId == R.id.navigation_item_8) {
                    selectedFragment = new AppSettingsFragment();
                    setFragment();
                    showSnackbar(title + " pressed");
                }

                //Closing drawer on item click
                //////mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        setUpNavDrawer();
    }


    public void setUpToolbar(){
        if(mToolbar==null) {
            mToolbar = (Toolbar) findViewById(R.id.toolbarMap);
            setSupportActionBar(mToolbar);
            //getSupportActionBar().setTitle("Ask for help");
        }
        if(!(title.equals("Navigation"))){
            getSupportActionBar().setTitle(title);
        }
    }

    public void setUpNavDrawer(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

    }

    public void setFragment() {
        if (selectedFragment == null) {
            selectedFragment = new MapFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, selectedFragment).commit();
        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, selectedFragment).commit();
        }
    }

    public void showSnackbar(String message)   {
        Snackbar mSnackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);

        mSnackBar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mSnackBar.setActionTextColor(Color.WHITE);

        View snackbarView = mSnackBar.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        mSnackBar.show();
    }

    void loadData() {

        //  getting previously stored data
        SharedPreferences sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE);
        name = sharedPreferences.getString("Name", DEFAULT);
        email = sharedPreferences.getString("Email", DEFAULT);

        //  checking the collected data and putting it to screen
        if (name.equals(DEFAULT) || email.equals(DEFAULT)) {
            Toast.makeText(MainMapScreen.this, "No name or email ID was found", Toast.LENGTH_SHORT).show();
        } else {

            pic=(ImageView)findViewById(R.id.profilePic);
            tvName=(TextView)findViewById(R.id.username);
            tvEmail=(TextView)findViewById(R.id.email);

            tvName.setText(name);
            tvEmail.setText(email);

            //  setting image from phone
            BufferedOutputStream out = null;
            String path= Environment.getExternalStorageDirectory()+"/Android/data/"+ getPackageName();
            try {
                File temp = new File(path, "profile_pic.png");
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(temp));
                pic.setImageBitmap(b);

            } catch (FileNotFoundException e) {
                Log.d("user","exception "+e);
            } finally {
                if (out != null) try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // screen orientation saving the instance and opening the same fragment
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(title != "Navigation")
            outState.putString("TOOLBAR_TITLE", title);
        getSupportFragmentManager().putFragment(outState, "MY_FRAGMENT", selectedFragment);
        Log.d("menu", "onSaveInstanceState " + title);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();

        if(title.equals("Voice Commands")) {
            Log.d("menu","item 1");
            inflater.inflate(R.menu.menu_voice_commands, menu);
        }
        else if(title.equals("Nearby Help")) {
            Log.d("menu","item 6");
            inflater.inflate(R.menu.menu_nearby_help_fragment, menu);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        Log.d("menu","on options selected");
        int id = item.getItemId();
        choice = item.getTitle().toString();

        //mRequestQueue = VolleySingleton.getInstance().getmRequestQueue();


        Log.d("menu", "choice " + choice);

        //  object was needed to call its method
        //////////////////////  new
        ////////////////////// NearbyHelpFragment nearbyHelpFragment = new NearbyHelpFragment();
        //  new fragment is created but it was not set up
        //  therefore NPE in googlemap
        //  so fragment is set again

        ///////////////////////selectedFragment = nearbyHelpFragment;
        ///////////////////////setFragment();
        NearbyHelpFragment nearbyHelpFragment = (NearbyHelpFragment)selectedFragment;

        Log.d("menu", "fragments ok");

        String lat=null,lng=null;
        try {
            //  converting latitude n longitude from nearby fragment
            lat = NearbyHelpFragment.latitude.toString();
            lng = (NearbyHelpFragment.longitude).toString();
            Log.d("menu","lat lng :"+lat+lng);

        }catch (Exception e)  {
            Log.d("menu","lat lng exception "+e);
        }

        Log.d("menu", "lat lng ok");

        //////////////  FOR NEARBY FRAGMENT
        //noinspection SimplifiableIfStatement
        if (id == R.id.policeStaion) {

            Log.d("menu", "choice again" + choice);

            if(lat != null && lng != null)
                url = PLACES_BASE_URL +lat+ ","
                        +lng+ "&rankby=distance&types=police&sensor=false&key=" +getString(R.string.google_maps_key);
            else
                url = PLACES_BASE_URL + "28.729007,%2077.129470" +
                        "&rankby=distance&types=police&sensor=false&key=" +getString(R.string.google_maps_key);

            nearbyHelpFragment.updateUrl(url);

            Log.d("menu", "url : "+url);
            return true;
        }
        else if (id == R.id.hospital) {
            Log.d("menu", "hospital");
            //url = "https://maps.googleapis.com/maps/api/place/search/json?location=""28.729007,%2077.129470&rankby=distance&types=hospital&sensor=false&key=" +getString(R.string.google_maps_key);
            if(lat != null && lng != null)
                url = PLACES_BASE_URL +lat+ "," +lng+ "&rankby=distance&types=hospital&sensor=false&key=" +getString(R.string.google_maps_key);
            else
                url = PLACES_BASE_URL + "28.729007,%2077.129470&rankby=distance&types=hospital&sensor=false&key=" +getString(R.string.google_maps_key);

            nearbyHelpFragment.updateUrl(url);
            Log.d("menu", "sending request");
        }
        else if (id == R.id.doctor) {
            Log.d("menu", "doctor");
           // url = "https://maps.googleapis.com/maps/api/place/search/json?location="28.729007,%2077.129470&rankby=distance&types=doctor&sensor=false&key=" +getString(R.string.google_maps_key);

            if(lat != null && lng != null)
                url = PLACES_BASE_URL +lat+ "," +lng+ "&rankby=distance&types=doctor&sensor=false&key=" +getString(R.string.google_maps_key);
            else
                url = PLACES_BASE_URL + "28.729007,%2077.129470&rankby=distance&types=doctor&sensor=false&key=" +getString(R.string.google_maps_key);

            nearbyHelpFragment.updateUrl(url);
            Log.d("menu", "sending request");
        }
        else if (id == R.id.trainStaion) {
            Log.d("menu", "train station");
            if(lat != null && lng != null)
                url = PLACES_BASE_URL +lat+ "," +lng+ "&rankby=distance&types=train_station&sensor=false&key=" +getString(R.string.google_maps_key);
            else
                url = PLACES_BASE_URL + "28.729007,%2077.129470&rankby=distance&types=train_station&sensor=false&key=" +getString(R.string.google_maps_key);

            //url = "https://maps.googleapis.com/maps/api/place/search/json?location="28.729007,%2077.129470&rankby=distance&types=train_station&sensor=false&key=" +getString(R.string.google_maps_key);
            Log.d("menu", "url's fine");
            nearbyHelpFragment.updateUrl(url);
            Log.d("menu", "sending request");
        }
        else if (id == R.id.subwayStaion) {
            Log.d("menu", "subway station");
            if(lat != null && lng != null)
                url = PLACES_BASE_URL +lat+ "," +lng+ "&rankby=distance&types=subway_station&sensor=false&key=" +getString(R.string.google_maps_key);
            else
                url = PLACES_BASE_URL + "28.729007,%2077.129470&rankby=distance&types=subway_station&sensor=false&key=" +getString(R.string.google_maps_key);

            //url = "https://maps.googleapis.com/maps/api/place/search/json?location="28.729007,%2077.129470&rankby=distance&types=subway_station&sensor=false&key=" +getString(R.string.google_maps_key);
            Log.d("menu", "url's fine");
            nearbyHelpFragment.updateUrl(url);
            Log.d("menu", "sending request");
        }
        else if (id == R.id.busStaion) {
            Log.d("menu", "bus station");
            if(lat != null && lng != null)
                url = PLACES_BASE_URL +lat+ "," +lng+ "&rankby=distance&types=bus_station&sensor=false&key=" +getString(R.string.google_maps_key);
            else
                url = PLACES_BASE_URL + "28.729007,%2077.129470&rankby=distance&types=bus_station&sensor=false&key=" +getString(R.string.google_maps_key);

            Log.d("menu", "url's fine");
            nearbyHelpFragment.updateUrl(url);
            Log.d("menu", "sending request");
        }
        else if (id == R.id.gasStaion) {
            Log.d("menu", "gas station");
            if(lat != null && lng != null)
                url = PLACES_BASE_URL +lat+ "," +lng+ "&rankby=distance&types=gas_station&sensor=false&key=" +getString(R.string.google_maps_key);
            else
                url = PLACES_BASE_URL + "28.729007,%2077.129470&rankby=distance&types=gas_station&sensor=false&key=" +getString(R.string.google_maps_key);

            //url = "https://maps.googleapis.com/maps/api/place/search/json?location="28.729007,%2077.129470&rankby=distance&types=gas_station&sensor=false&key=" +getString(R.string.google_maps_key);
            Log.d("menu", "url's fine");
            nearbyHelpFragment.updateUrl(url);
            Log.d("menu", "sending request");
        }
        else if (id == R.id.taxi_stand) {
            Log.d("menu", "taxi stand");
            if(lat != null && lng != null)
                url = PLACES_BASE_URL +lat+ "," +lng+ "&rankby=distance&types=taxi_stand&sensor=false&key=" +getString(R.string.google_maps_key);
            else
                url = PLACES_BASE_URL + "28.729007,%2077.129470&rankby=distance&types=taxi_stand&sensor=false&key=" +getString(R.string.google_maps_key);

            //url = "https://maps.googleapis.com/maps/api/place/search/json?location="28.729007,%2077.129470&rankby=distance&types=taxi_stand&sensor=false&key=" +getString(R.string.google_maps_key);
            Log.d("menu", "url's fine");
            nearbyHelpFragment.updateUrl(url);
            Log.d("menu", "sending request");
        }
        ///////////////     NEARBY FRAGMENT END

        return super.onOptionsItemSelected(item);
    }


}