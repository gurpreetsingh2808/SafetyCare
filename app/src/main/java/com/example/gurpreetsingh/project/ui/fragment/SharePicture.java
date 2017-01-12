package com.example.gurpreetsingh.project.ui.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.gurpreetsingh.project.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SharePicture extends Fragment {

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    ImageView iv;
    Bitmap bmp;
    //FloatingActionButton fabCapture, fabShare;
    FloatingActionButton fabCapture, fabShare;
    Bitmap bitmapImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("helpline","on create view");

        Log.d("helpline", "on create view ends");
        return inflater.inflate(R.layout.activity_share_picture, container, false);
        //return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iv=(ImageView) getView().findViewById(R.id.imageView1);

        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getActivity(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // if the device does't have camera
        }
        else {
            captureImage();
        }

        fabCapture = (FloatingActionButton) getView().findViewById(R.id.fabCaptureImage);
        fabCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test", "onclick");
                // Checking camera availability
                if (!isDeviceSupportCamera()) {
                    Toast.makeText(getActivity(),
                            "Sorry! Your device doesn't support camera",
                            Toast.LENGTH_LONG).show();
                    // if the device does't have camera
                }
                else {
                    captureImage();
                }
            }
        });

        fabShare = (FloatingActionButton) getView().findViewById(R.id.fabShareImage);
        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test", "onclick");
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("image/*");
                try {
                    i.putExtra(Intent.EXTRA_STREAM, Uri.parse("" + savedImageName()));
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Image save exception : " + e, Toast.LENGTH_SHORT).show();
                }
                startActivity(Intent.createChooser(i, "Share With"));
            }
        });

        fabShare.setVisibility(View.GONE);
        fabShare.animate().translationYBy(250);


/*
        Intent i=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, 0);
        Log.d("SharePicture", "camera");

        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("image/*");
                try {
                    i.putExtra(Intent.EXTRA_STREAM, Uri.parse("" + savedImageName()));
                }catch (Exception e) {
                    Toast.makeText(getActivity(),"Image save exception : "+e, Toast.LENGTH_SHORT).show();
                }
                startActivity(Intent.createChooser(i, "Share With"));
            }
        });
        */
    }

    /**
     * Launching camera app to capture image
     */
    private void captureImage() {

        Intent i=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        Log.d("SharePicture", "camera");

        /*
        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("image/*");
                try {
                    i.putExtra(Intent.EXTRA_STREAM, Uri.parse("" + savedImageName()));
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Image save exception : " + e, Toast.LENGTH_SHORT).show();
                }
                startActivity(Intent.createChooser(i, "Share With"));
            }
        });
        */
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent i) {
        // TODO Auto-generated method stub
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                super.onActivityResult(requestCode, resultCode, i);
                //fabCapture.animate().translationYBy(250);
                //fabCapture.setVisibility(View.GONE);
                fabCapture.animate()
                        .translationYBy(250)
                        .setInterpolator(new AnticipateOvershootInterpolator())
                        .setDuration(700)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                fabShare.setVisibility(View.VISIBLE);
                                fabShare.animate().translationY(0).setDuration(500).setInterpolator(new AnticipateOvershootInterpolator());
                            }
                        });

                Bundle b = i.getExtras();
                bitmapImage = (Bitmap) b.get("data");
                iv.setImageBitmap(bitmapImage);
                //super.onActivityResult(requestCode, resultCode, i);

            } else if (resultCode == getActivity().RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(getActivity(), "User cancelled image capture", Toast.LENGTH_SHORT).show();

            } else {
                // failed to capture image
                Toast.makeText(getActivity(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }

        }
    }


    /**
     * Checking device has camera hardware or not
     */
    private boolean isDeviceSupportCamera() {
        if (getActivity().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    private String savedImageName() throws IOException {

        File image=null;
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        Log.d("SharePicture","dir "+storageDir);

        BufferedOutputStream out = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            //File temp = new File(path, "profile_pic.png");
            Log.d("SharePicture", "image : "+image);
            out = new BufferedOutputStream(new FileOutputStream(image));
            Log.d("SharePicture", "buff out stream : "+out);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, out);

        } catch (FileNotFoundException e) {
            Log.d("SharePicture","exception "+e);
        } finally {
            if (out != null) try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Save a file: path for use with ACTION_VIEW intents
        return "file:" + image.getAbsolutePath();
    }

}
