package com.example.gurpreetsingh.project;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.ContactsContract;
import com.github.clans.fab.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import java.util.ArrayList;



////////////////////////////////////  DONT USE NEW OPERATOR AT EVERY CALL IN ADAPTER



public class ReceiverInfo extends AppCompatActivity {

    TextInputLayout etContacts;
    FloatingActionButton myFab;
    final int PICK_CONTACT=1;
    String contactName=null, contactNumber=null, action;
    ListView lvContacts;
    ImageView ivAddContact, ivDeleteContact, ivReplaceContact;
    int selectedIndex=-1;
    public static int contactCounter=0;
    ArrayList<String> listName=new ArrayList<String>();
    ArrayList<String> listNumber=new ArrayList<String>();
    View selectedView=null;
    TextView tvInstruction;
    /////////////////MyAdapter contactsListAdapter = new MyAdapter(this, R.layout.row, listName);

    @Override
    public void onBackPressed() {
        if(selectedIndex == -1)  {
            finish();
        }
        else {
            selectedView.setBackgroundColor(getResources().getColor(R.color.white));
            ivAddContact.setVisibility(View.VISIBLE);
            ivDeleteContact.setVisibility(View.GONE);
            ivReplaceContact.setVisibility(View.GONE);
            myFab.setVisibility(View.VISIBLE);
            selectedIndex=-1;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_info);

        Toolbar toolbar3 = (Toolbar) findViewById(R.id.toolbarReceiverInfo);
        setSupportActionBar(toolbar3);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        Log.d("receiverinfo","on create");
        tvInstruction=(TextView)findViewById(R.id.textViewInstruction);
        Log.d("receiverinfo","find tv instruction");

        if(contactName==null && contactNumber==null)  {
            Log.d("receiverinfo","contact null");
            tvInstruction.setVisibility(View.VISIBLE);
        }

        Log.d("receiverinfo","list view");
        lvContacts = (ListView) findViewById(R.id.listViewContacts);
        lvContacts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if(selectedView != null)  {
                    selectedView.setBackgroundColor(getResources().getColor(R.color.white));
                }
                Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(100);

                view.setBackgroundColor(getResources().getColor(R.color.item_pressed));

                ivAddContact.setVisibility(View.GONE);
                ivDeleteContact.setVisibility(View.VISIBLE);
                ivReplaceContact.setVisibility(View.VISIBLE);
                myFab.setVisibility(View.GONE);

                selectedIndex = position;
                selectedView = view;
                return false;
            }
        });

        ivDeleteContact = (ImageView)findViewById(R.id.imageViewDeleteContact);
        ivDeleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listName.remove(selectedIndex);
                listNumber.remove(selectedIndex);
                lvContacts.setAdapter(new MyAdapter(ReceiverInfo.this, R.layout.row, listName));
                //////////////contactsListAdapter.notifyDataSetChanged();
                ivAddContact.setVisibility(View.VISIBLE);
                ivDeleteContact.setVisibility(View.GONE);
                ivReplaceContact.setVisibility(View.GONE);
                myFab.setVisibility(View.VISIBLE);

                contactCounter--;
            }
        });

        ivReplaceContact = (ImageView)findViewById(R.id.imageViewReplaceContact);
        ivReplaceContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action = "replace";
                /////////////////////////contactsListAdapter.notifyDataSetChanged();
                // add new contact at that position
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });

        ivAddContact = (ImageView)findViewById(R.id.imageViewAddContact);
        ivAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action = "add";
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);

            }
        });

        myFab = (FloatingActionButton) findViewById(R.id.fab3);
        myFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(contactCounter==0)  {
                    Toast.makeText(ReceiverInfo.this, "Please add at least one contact", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent i = new Intent(ReceiverInfo.this, AppSettings.class);
                    startActivity(i);
                    finish();
                }
            }
        });
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
                            mProjection,                        // The columns to return for each row
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
                    if (cursor.moveToFirst()) {

                        contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        Log.d("gp", "contact name " + contactName);

                        ContentResolver cr = getContentResolver();
                        Cursor mCursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                                "DISPLAY_NAME = '" + contactName + "'", null, null);
                        if (mCursor.moveToFirst()) {
                            Log.d("gp", "moved to first");
                            String contactId = mCursor.getString(mCursor.getColumnIndex(ContactsContract.Contacts._ID));
                            Log.d("gp", "contact id " + contactId);

                            //
                            //  Find number based on id
                            //
                            Cursor phones = cr.query(Phone.CONTENT_URI, null,
                                    Phone.CONTACT_ID + " = " + contactId, null, null);
                            Log.d("gp", "cursor phones ok");

                            if (phones.moveToFirst()) {
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
                            if(contactCounter>=5)  {
                                Toast.makeText(ReceiverInfo.this, "Sorry, you can't add more than 5 contacts", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                if(listName.contains(contactName) || listNumber.contains(contactNumber))  {
                                    Toast.makeText(ReceiverInfo.this, "Contact already added. Please select different contact", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    listName.add(contactName);
                                    listNumber.add(contactNumber);
                                    lvContacts.setAdapter(new MyAdapter(this, R.layout.row, listName));
                                    contactCounter++;

                                    saveData(contactCounter);
                                }
                            }
                        }
                        else if(action.equals("replace")) {
                            if(listName.contains(contactName) || listNumber.contains(contactNumber))  {
                                Toast.makeText(ReceiverInfo.this, "Contact already added. Please select different contact", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                // remove previous contact
                                listName.remove(selectedIndex);
                                listNumber.remove(selectedIndex);
                                // add new contact to that position
                                listName.add(selectedIndex, contactName);
                                listNumber.add(selectedIndex, contactNumber);
                                lvContacts.setAdapter(new MyAdapter(this, R.layout.row, listName));

                                ivAddContact.setVisibility(View.VISIBLE);
                                ivDeleteContact.setVisibility(View.GONE);
                                ivReplaceContact.setVisibility(View.GONE);
                                myFab.setVisibility(View.VISIBLE);

                                saveData(selectedIndex);
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
        editor.commit();
        Toast.makeText(ReceiverInfo.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
    }

    //  custom adapter for listview(country codes)
    class MyAdapter extends ArrayAdapter<String> {

        public MyAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
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
            View row = inflater.inflate(R.layout.row, parent, false);
            row.setBackgroundColor(getResources().getColor(R.color.white));
            //top
            TextView label = (TextView) row.findViewById(R.id.top);
            label.setText(listNumber.get(position));

            //bottom
            TextView sub = (TextView) row.findViewById(R.id.bottom);
            sub.setText(listName.get(position));

            return row;
        }
    }
}
