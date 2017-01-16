package com.example.gurpreetsingh.project.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.gurpreetsingh.project.R;
import com.example.gurpreetsingh.project.framework.service.ShakeService;
import com.example.gurpreetsingh.project.framework.service.VolumeService;

public class AppSettingsFragment extends Fragment {

    public static final String DEFAULT = "unchecked";
    Switch switchFake, switchAuto, switchShake, switchQuick;
    Intent intentVolumeService, intentShakeService;
    String shakeState, quickStartState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=null;
        Log.d("setting","oncreateview started");
        try {
            v = inflater.inflate(R.layout.fragment_app_settings, container, false);
            Log.d("setting", "oncreateview finished");
        }
        catch (Exception e){
            Log.e("setting", "exception: "+e);
        }
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //switchAuto=(Switch)getView().findViewById(R.id.switchAuto2);
        //switchFake=(Switch)getView().findViewById(R.id.switchFake2);
        switchShake=(Switch)getView().findViewById(R.id.switchShake2);
        switchQuick=(Switch)getView().findViewById(R.id.switchQuick2);

        //  loading previous state of switch
        loadState();

        switchQuick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                Log.d("Volume", "checked");
                intentVolumeService = new Intent(getActivity(), VolumeService.class);
                if (switchQuick.isChecked()) {
                    editor.putString("QuickStartState", "checked");
                    Log.d("Volume", "checked");
                    getActivity().startService(intentVolumeService);
                } else {
                    editor.putString("QuickStartState", "unchecked");
                    getActivity().stopService(intentVolumeService);
                    Log.d("AppSettings", " not checked");
                }
                editor.apply();
            }
        });

        switchShake.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                intentShakeService = new Intent(getActivity(), ShakeService.class);
                if (switchShake.isChecked()) {
                    Log.d("AppSettings", "checked");
                    editor.putString("ShakeState", "checked");
                    getActivity().startService(intentShakeService);
                } else {
                    editor.putString("ShakeState", "unchecked");
                    getActivity().stopService(intentShakeService);
                    Log.d("AppSettings", " not checked");
                }
                editor.apply();
            }
        });

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    void loadState()  {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Data", Context.MODE_PRIVATE);
        shakeState = sharedPreferences.getString("ShakeState", DEFAULT);
        quickStartState = sharedPreferences.getString("QuickStartState", DEFAULT);
        if(shakeState.equals("checked"))  {
            switchShake.setChecked(true);
        }
        if(quickStartState.equals("checked"))  {
            switchQuick.setChecked(true);
        }
    }

}
