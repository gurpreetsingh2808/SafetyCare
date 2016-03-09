package com.example.gurpreetsingh.project;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.List;

public class NearbyPlaces extends AppCompatActivity {
    private int PLACE_PICKER_REQUEST = 1;
    private TextView tvName;
    private TextView tvAddress;
    private TextView tvAttributions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_places);

        tvName = (TextView) findViewById(R.id.name);
        tvAddress = (TextView) findViewById(R.id.address);
        tvAttributions = (TextView) findViewById(R.id.attributions);
    }
    public void onPickButtonClick(View v) {
        // Construct an intent for the place picker
        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(this);
            // Start the intent by requesting a result,
            // identified by a request code.
            startActivityForResult(intent, PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {


        if (requestCode == PLACE_PICKER_REQUEST
                && resultCode == Activity.RESULT_OK) {

            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, this);

            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            String attributions = PlacePicker.getAttributions(data);
            if (attributions == null) {
                attributions = "";
            }
            tvName.setText(name);
            tvAddress.setText(address);
            tvAttributions.setText(Html.fromHtml(attributions));

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
