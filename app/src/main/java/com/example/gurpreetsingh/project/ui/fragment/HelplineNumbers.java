package com.example.gurpreetsingh.project.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gurpreetsingh.project.framework.PerformAction;
import com.example.gurpreetsingh.project.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class HelplineNumbers extends Fragment {

    ListView helplineListView;
    String contactName[] = {
            "Police Control Room",
            "Ambulance",
            "C.M. Helpline",
            "Woman Police Help",
            "Child Helpline",
            "Accident Response"
    };

    String contactNumber[] = {
            "100",
            "108",
            "181",
            "1090",
            "1098",
            "1099"
    };

    public HelplineNumbers() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("helpline","on create view");
        //View v = inflater.inflate(R.layout.fragment_helpline_numbers,container,false);
        //helplineListView = (ListView) v.findViewById(R.id.listViewShowContacts);
        //helplineListView.setAdapter(new MyAdapter(getActivity(), R.layout.voice_command_layout, contactName));

        Log.d("helpline", "on create view ends");
        return inflater.inflate(R.layout.fragment_helpline_numbers, container, false);
        //return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("helpline", "on activity created");

        helplineListView = (ListView) getView().findViewById(R.id.listViewShowContacts);

        //  setting custom adapter to spinner
        helplineListView.setAdapter(new MyAdapter(getActivity(), R.layout.voice_command_layout, contactName));

        //  selected item
        helplineListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < contactName.length; i++) {
                    if (position == i) {
                        Toast.makeText(getActivity(), contactName[i] + " selected", Toast.LENGTH_SHORT).show();
                        PerformAction.call(getActivity(), contactNumber[i]);
                    }
                }
            }
        });
    }

    //  custom adapter for helpline
    public class MyAdapter extends ArrayAdapter<String> {

        public MyAdapter(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
            Log.d("helpline", "constructor of my adapter");
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            Log.d("helpline","drop down view");
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d("helpline","get view");
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            Log.d("helpline","get custom view");

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View row = inflater.inflate(R.layout.voice_command_layout, parent, false);
            //////      extra line for colour
            row.setBackgroundColor(getResources().getColor(R.color.white));
            TextView name = (TextView) row.findViewById(R.id.left);
            name.setText(contactName[position]);

            TextView number = (TextView) row.findViewById(R.id.right);
            number.setText(contactNumber[position]);

            Log.d("helpline", "get custom view ends");
            return row;
        }
    }

}
