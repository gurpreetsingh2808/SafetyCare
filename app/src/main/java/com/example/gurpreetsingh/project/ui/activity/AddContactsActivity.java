package com.example.gurpreetsingh.project.ui.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;

import com.example.gurpreetsingh.project.domain.Contact;
import com.example.gurpreetsingh.project.MainMapScreen;
import com.example.gurpreetsingh.project.R;
import com.example.gurpreetsingh.project.framework.data.DataStore;
import com.example.gurpreetsingh.project.ui.adapter.EmergencyContactsAdapter;
import com.example.gurpreetsingh.project.util.AppUtils;
import com.lacronicus.easydatastorelib.DatastoreBuilder;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


////////////////////////////////////  DONT USE NEW OPERATOR AT EVERY CALL IN ADAPTER



public class AddContactsActivity extends AppCompatActivity implements EmergencyContactsAdapter.ItemListener{

    private static final int PERMISSION_CONTACTS_REQUEST_CODE = 1000;
    private static final int PICK_CONTACT=1;

    private String contactName=null, contactNumber=null, action;
    private static int contactCounter=0;
    private ArrayList<String> listName=new ArrayList<String>();
    private ArrayList<String> listNumber=new ArrayList<String>();
    private TextView tvInstruction;
    private EmergencyContactsAdapter mAdapter;
    private DataStore datastore;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.initializeCalligraphy();
        setContentView(R.layout.activity_add_contacts);

        Toolbar toolbar3 = (Toolbar) findViewById(R.id.toolbarReceiverInfo);
        setSupportActionBar(toolbar3);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        //  initialize data store
        datastore = new DatastoreBuilder(PreferenceManager.getDefaultSharedPreferences(this))
                .create(DataStore.class);

        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvContacts.setLayoutManager(mLayoutManager);
        mAdapter = new EmergencyContactsAdapter(this);
        rvContacts.setAdapter(mAdapter);


        tvInstruction=(TextView)findViewById(R.id.textViewInstruction);
        Log.d("receiverinfo","find tv instruction");

        if(contactName==null && contactNumber==null)  {
            Log.d("receiverinfo","contact null");
            tvInstruction.setVisibility(View.VISIBLE);
        }

        ImageView ivAddContact = (ImageView) findViewById(R.id.imageViewAddContact);
        ivAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForContactsPermission();

            }
        });

        FloatingActionButton buttonAddContacts = (FloatingActionButton) findViewById(R.id.fabAddContacts);
        buttonAddContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(datastore.emergencyContactList().get() != null && datastore.emergencyContactList().get().isEmpty())  {
                    Toast.makeText(AddContactsActivity.this, "Please add at least one contact", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent i = new Intent(AddContactsActivity.this, MainMapScreen.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    private void askForContactsPermission() {
        //  add already added accounts in device to autocomplete
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            ActivityCompat.requestPermissions(AddContactsActivity.this, new String[]
                            {Manifest.permission.READ_CONTACTS, android.Manifest.permission.GET_ACCOUNTS},
                    PERMISSION_CONTACTS_REQUEST_CODE);
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            //return;
        } else {
            action = "add";
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, PICK_CONTACT);
    }
    }


    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PICK_CONTACT) :
                if (resultCode == RESULT_OK) {

                    //  GETTING CONTACT NAME

                    Log.d("gp", "result code is ok");
                    /* Queries the user dictionary and returns results
                    mCursor = getContentResolver().query(
                            UserDictionary.Words.CONTENT_URI,   // The content URI of the words table
                            mProjection,                        // The columns to return for each item_contact
                            mSelectionClause                    // Selection criteria
                            mSelectionArgs,                     // Selection criteria
                            mSortOrder);                        // The sort order for the returned rows
                    */
                    Uri contactData = data.getData();
                    Log.d("gp", "uri's fine");
                    Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
                    Log.d("gp", "cursor's fine");

                    //  GETTING PHONE NUMBER

                    //  Find contact based on name.
//
                    if (cursor != null && cursor.moveToFirst()) {

                        contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        Log.d("gp", "contact name " + contactName);

                        ContentResolver cr = getContentResolver();
                        Cursor mCursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                                "DISPLAY_NAME = '" + contactName + "'", null, null);
                        if (mCursor != null && mCursor.moveToFirst()) {
                            Log.d("gp", "moved to first");
                            String contactId = mCursor.getString(mCursor.getColumnIndex(ContactsContract.Contacts._ID));
                            Log.d("gp", "contact id " + contactId);

                            //
                            //  Find number based on id
                            //
                            Cursor phones = cr.query(Phone.CONTENT_URI, null,
                                    Phone.CONTACT_ID + " = " + contactId, null, null);
                            Log.d("gp", "cursor phones ok");

                            if (phones != null && phones.moveToFirst()) {
                                try {
                                    contactNumber = phones.getString(phones.getColumnIndex(Phone.NUMBER));
                                } catch (Exception e) {
                                    Log.d("gp", "exception " + e);
                                }
                                Log.d("gp", "number " + contactNumber);

                                /*
                                int type = phones.getInt(phones.getColumnIndex(Phone.TYPE));
                                switch (type) {
                                    case Phone.TYPE_HOME:
                                        // do something with the Home number here...
                                        Log.d("gp", "home");
                                        break;
                                    case Phone.TYPE_MOBILE:
                                        // do something with the Mobile number here...
                                        Log.d("gp", "mobile");
                                        break;
                                    case Phone.TYPE_WORK:
                                        // do something with the Work number here...
                                        Log.d("gp", "work");
                                        break;
                                }
                                */
                                phones.close();
                            }
                        }
                        cursor.close();

                        if(action.equals("add")) {
                            if(datastore.emergencyContactList().get() != null && datastore.emergencyContactList().get().size() >= 5)  {
                                Toast.makeText(AddContactsActivity.this, "Sorry, you can't add more than 5 contacts", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                if(listName.contains(contactName) || listNumber.contains(contactNumber))  {
                                    Toast.makeText(AddContactsActivity.this, "Contact already added. Please select different contact", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    List<Contact> listContact = new ArrayList<>();
                                    //  create new contact object
                                    Contact contact = new Contact(contactName, contactNumber);
                                    //  fetch contacts list from data store(shared pref)
                                    if(datastore.emergencyContactList().get() != null) {
                                        listContact = datastore.emergencyContactList().get();
                                    }
                                    //  add new contact object to the list
                                    listContact.add(contact);
                                    //  save the list in shared pref
                                    datastore.emergencyContactList().put(listContact);

                                    mAdapter.add(contact);
                                    if(tvInstruction.getVisibility() == View.VISIBLE) {
                                        tvInstruction.setVisibility(View.GONE);
                                    }
                                    contactCounter++;
                                    saveData(contactCounter);
                                }
                            }
                        }
                        else if(action.equals("replace")) {
                            if(listName.contains(contactName) || listNumber.contains(contactNumber))  {
                                Toast.makeText(AddContactsActivity.this, "Contact already added. Please select different contact", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                // remove previous contact
                                /*
                                buttonAddContacts.setVisibility(View.VISIBLE);

                                saveData(selectedIndex);*/
                            }
                        }
                    }
                }
                break;
        }
    }   //on activity result

    void saveData(int index)  {
        SharedPreferences sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("EmergencyContactName["+index+"]", contactName);
        editor.putString("EmergencyContactNumber["+index+"]", contactNumber);
        editor.apply();
        Toast.makeText(AddContactsActivity.this, "Contact saved successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(Contact contact, int position) {

    }

    //  custom adapter for listview(country codes)

}
