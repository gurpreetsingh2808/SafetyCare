package com.example.gurpreetsingh.project.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gurpreetsingh.project.R;
import com.github.clans.fab.FloatingActionButton;


public class NumberVerify extends AppCompatActivity {
    Button verifyButton;
    TextInputLayout etName, etPhoneNo;
    String name, countryCode, phoneNo;
    Spinner spinner;
    FloatingActionButton myFab;
    Boolean verified=false;

    String countryCodes[] = {
            "Afghanistan",
            "Albania",
            "Algeria",
            "American Samoa",
            "Andorra",
            "Angola",
            "Anguilla",
            "India"
    };

    String subCodes[] = {
            "+93",
            "+355",
            "+213",
            "+1-684",
            "+376",
            "+244",
            "+1-264",
            "+91"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_verify);

        Toolbar toolbar1 = (Toolbar) findViewById(R.id.toolbarNumberVerify);
        setSupportActionBar(toolbar1);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);


        //Log.d("gp", "start");
        etName = (TextInputLayout) findViewById(R.id.textInputLayoutName);
        etPhoneNo = (TextInputLayout) findViewById(R.id.textInputLayoutPhoneNo);
        myFab = (FloatingActionButton) findViewById(R.id.fab1);
        spinner = (Spinner) findViewById(R.id.spinner);

        //  setting custom adapter to spinner
        spinner.setAdapter(new MyAdapter(NumberVerify.this, R.layout.item_contact, countryCodes));

        //  storing selected value
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < subCodes.length; i++) {
                    if (position == i) {
                        countryCode = subCodes[position];
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //getResources().getColor(R.color.accentColor);
        //buttonNext.setColorNormal(R.color.red);
        //buttonNext.setColorPressed(R.color.primaryColor);

        // setting action to FAB
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                verified=verifyInput();
                if(verified)  {
                    Intent i = new Intent(NumberVerify.this, UserInformation.class);
                    startActivity(i);
                    finish();
                }
            }
        });

        /*verifyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });*/
    }

    boolean verifyInput() {
        boolean ok=false;
        name = etName.getEditText().getText().toString();
        phoneNo = etPhoneNo.getEditText().getText().toString();

        if (name.equals("") || phoneNo.equals("")) {
            Toast.makeText(NumberVerify.this, "Any field cannot be left blank. Please check again.", Toast.LENGTH_LONG).show();
        }
        else if (phoneNo.length() != 10 || phoneNo.contains(" "))  {
            Toast.makeText(NumberVerify.this, "Invalid phone number. Phone number must contain 10 digits without spaces", Toast.LENGTH_LONG).show();
        }
        else {
            SharedPreferences sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Name", name);
            editor.putString("CountryCode", countryCode);
            editor.putString("Phone", phoneNo);

            editor.commit();
            ok=true;
            Toast.makeText(NumberVerify.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
        }
        return ok;
    }

    //  custom adapter for spinner(country codes)
    class MyAdapter extends ArrayAdapter<String> {

        public MyAdapter(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.item_contact, parent, false);
            //////      extra line for colour
            row.setBackgroundColor(getResources().getColor(R.color.white));
            TextView label = (TextView) row.findViewById(R.id.top);
            label.setText(countryCodes[position]);

            TextView sub = (TextView) row.findViewById(R.id.bottom);
            sub.setText(subCodes[position]);

            return row;
        }
    }
}

