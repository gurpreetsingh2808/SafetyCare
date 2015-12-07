package com.example.gurpreetsingh.project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SharePicture extends Fragment {
    ImageView iv;
    Bitmap bmp;
    FloatingActionButton fabShare;
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
        fabShare=(FloatingActionButton) getView().findViewById(R.id.fabSharePicture);

        Intent i=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, 0);

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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent i) {
        // TODO Auto-generated method stub
        Bundle b = i.getExtras();
        bitmapImage=(Bitmap) b.get("data");
        iv.setImageBitmap(bitmapImage);

        super.onActivityResult(requestCode, resultCode, i);
    }

    private String savedImageName() throws IOException {
        File image=null;
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        Log.d("user","dir "+storageDir);

        BufferedOutputStream out = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            //File temp = new File(path, "profile_pic.png");
            out = new BufferedOutputStream(new FileOutputStream(image));
            //bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, out);

        } catch (FileNotFoundException e) {
            Log.d("user","exception "+e);
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
