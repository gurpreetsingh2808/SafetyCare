package com.example.gurpreetsingh.project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class VoiceCommandsFragment extends Fragment {

    //FloatingActionButton fabCommand;
    ImageView imageView;
    TextView tvCommand;
    ListView showCommandsListView;
    final int request_code=0;
    ArrayList<String> commands=new ArrayList<String>();
    ArrayList<String> actions=new ArrayList<String>();
    /*
    String commands[]={
            "help",
            "message"
    };
    String actions[]={
            "Call Contact",
            "Send Message"
    };
    */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_voice_commands,container,false);
        imageView=(ImageView)v.findViewById(R.id.imageButtonCommand);
        tvCommand=(TextView)v.findViewById(R.id.tvSpokenCommand);
        showCommandsListView=(ListView)v.findViewById(R.id.listViewShowCommand);

        commands.add("help");
        commands.add("message");

        actions.add("Call Contact");
        actions.add("Send Message");

        //  setting custom adapter to card
        showCommandsListView.setAdapter(new MyVoiceCommandAdapter(getActivity(), R.layout.voice_command_layout, commands));

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                startActivityForResult(i,request_code);
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== request_code){
                if(resultCode== Activity.RESULT_OK){
                    ArrayList<String> al = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    final String command=al.get(0);
                    tvCommand.setText("" + al.get(0));
                    voiceActions(command);
                }
        }
    }

    public void voiceActions(String action){
        String contactNumber;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Data", Context.MODE_PRIVATE);

        switch(action)
        {
            case "help":
                Toast.makeText(getActivity(),"working "+action,Toast.LENGTH_SHORT).show();
                contactNumber = sharedPreferences.getString("EmergencyContactNumber[0]","121");
                PerformAction.call(getActivity(), contactNumber);
                //Intent i=new Intent(Intent.ACTION_CALL);
                //i.setData(Uri.parse("tel:121"));
                //startActivity(i);
                break;
            case "message":
                Toast.makeText(getActivity(),"working "+action,Toast.LENGTH_SHORT).show();
                Log.d("message", "inside switch");
                for(int i=0; i<ReceiverInfo.contactCounter; i++)  {
                    contactNumber = sharedPreferences.getString("EmergencyContactNumber["+i+"]","121");
                    PerformAction.message(contactNumber, "I am  in trouble. Do contact me fast.");
                    Log.d("message", "message sent");
                }
                //PerformAction.message("121", Map.address);
                break;

        }
    }

    //  custom adapter for spinner(country codes)
    public class MyVoiceCommandAdapter extends ArrayAdapter<String> {

        public MyVoiceCommandAdapter(Context context, int textViewResourceId, ArrayList objects) {
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

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View rowCommand = inflater.inflate(R.layout.voice_command_layout, parent, false);
            rowCommand.setBackgroundColor(getResources().getColor(R.color.white));
            TextView tvCommand = (TextView) rowCommand.findViewById(R.id.left);
            //tvCommand.setText(commands[position]);
            tvCommand.setText(commands.get(position));

            TextView tvAction = (TextView) rowCommand.findViewById(R.id.right);
            //tvAction.setText(actions[position]);
            tvAction.setText(actions.get(position));

            return rowCommand;
        }
    }
}
