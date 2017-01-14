package com.example.gurpreetsingh.project.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;

import com.example.gurpreetsingh.project.R;
import com.github.clans.fab.FloatingActionButton;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class UserInformation extends AppCompatActivity {

    public static final String DEFAULT = "N/A";
    TextInputLayout etEmail, etPassword, etConfirmPassword;
    TextView tvName, tvCode, tvNumber;
    CheckBox cbShowPass;
    RadioGroup rgSex;
    RadioButton rbSex;
    FloatingActionButton myFab;
    ImageView contactImageView;
    String email, name, code, number, password, confirmPassword, sex = "";
    boolean emailValid = false, verified = false;
    Bitmap bitmapImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);

        myFab = (FloatingActionButton) findViewById(R.id.fab2);
        tvName = (TextView) findViewById(R.id.textViewName);
        tvCode = (TextView) findViewById(R.id.textViewCode);
        tvNumber = (TextView) findViewById(R.id.textViewNumber);
        etEmail = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        etPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
        etConfirmPassword = (TextInputLayout) findViewById(R.id.textInputLayoutConfirmPassword);
        cbShowPass = (CheckBox) findViewById(R.id.checkBoxPassword);
        contactImageView = (ImageView) findViewById(R.id.imageViewContact);
        rgSex = (RadioGroup) findViewById(R.id.radioGroupSex);

        Toolbar toolbar2 = (Toolbar) findViewById(R.id.toolbarUserInfo);
        setSupportActionBar(toolbar2);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        loadData();

        //  opening gallery on click
        contactImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
            }
        });


        //  is checkbox checked or not
        cbShowPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbShowPass.isChecked()) {

                    etPassword.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
                    etConfirmPassword.getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
                }
                else {
                    etPassword.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etConfirmPassword.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        //buttonNext.setColorNormal(R.color.accentColor);
        // moving to the next screen
        myFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                verified = checkInput();
                if (verified) {
                    Log.d("check", "verified");
                    try {
                        saveToInternalSorage();
                    } catch (Exception e) {
                        Log.d("user","exception "+e);
                    }
                    Log.d("user", "image saved to internal");
                    Intent i = new Intent(UserInformation.this, AddContactsActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    void loadData() {

        //  getting previously stored data
        SharedPreferences sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE);
        name = sharedPreferences.getString("Name", DEFAULT);
        code = sharedPreferences.getString("CountryCode", DEFAULT);
        number = sharedPreferences.getString("Phone", DEFAULT);

        //  checking the collected data and putting it to screen
        if (name.equals(DEFAULT) || code.equals(DEFAULT) || number.equals(DEFAULT)) {
            Toast.makeText(UserInformation.this, "No data was found", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(UserInformation.this, "Data loaded Successfully", Toast.LENGTH_SHORT).show();
            tvName.setText(name);
            tvCode.setText(code);
            tvNumber.setText(number);
        }
    }

    boolean checkInput() {
        Log.d("user", "inside verify input");
        boolean ok = false;
        email = etEmail.getEditText().getText().toString();
        password = etPassword.getEditText().getText().toString();
        confirmPassword = etConfirmPassword.getEditText().getText().toString();
        int selectedId = rgSex.getCheckedRadioButtonId();


        if (selectedId != -1) {
            Log.d("user", "id " + selectedId);
            rbSex = (RadioButton) findViewById(selectedId);
            Log.d("user", "radiobutton found");
            sex = rbSex.getText().toString();
            Log.d("user", "sex " + sex);
        } else {
            Log.d("user", "no id selected");
        }

        if (email.equals("") || password.equals("") || confirmPassword.equals("") || sex.equals("")) {
            Toast.makeText(UserInformation.this, "Any field cannot be left blank. Please check again.", Toast.LENGTH_LONG).show();
            /*
            try {
                File image=createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            */
        }
        else {
            if (!isValidEmail(email)) {
                Toast.makeText(UserInformation.this,"Invalid email ID",Toast.LENGTH_SHORT).show();
            }

            else if(password.contains(" ") || (password.length() < 8) )  {
                Toast.makeText(UserInformation.this, "Password should be at least 8 characters long and without spaces ", Toast.LENGTH_LONG).show();
            }
            else if(!(password.equals(confirmPassword)))  {
                Toast.makeText(UserInformation.this, "Password & Confirm password don't match", Toast.LENGTH_LONG).show();
            }
            else {
                SharedPreferences sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Email", email);
                editor.putString("Password", password);
                editor.putString("Sex", sex);

                editor.commit();

                ok = true;
                Toast.makeText(UserInformation.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
            }
        }
        return ok;
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        if (reqCode == 1) {
            if (resCode == RESULT_OK) {
                //contactImageView.setImageURI(data.getData());
                if (data != null) {
                    try {
                        bitmapImage = MediaStore.Images.Media.getBitmap(UserInformation.this.getContentResolver(), data.getData());
                        contactImageView.setImageBitmap(bitmapImage);
                    } catch (IOException e) {
                        Log.e("user","image not selected/io exception "+e);
                    }
                }
            }
        }
    }

    private void saveToInternalSorage()  {
        BufferedOutputStream out = null;
        String path=Environment.getExternalStorageDirectory()+"/Android/data/"+ getPackageName();
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdir();
        }

        try {
            File temp2 = new File(Environment.getExternalStorageDirectory(), "profile_pic.png");
            File temp = new File(path, "profile_pic.png");
            out = new BufferedOutputStream(new FileOutputStream(temp));
            //            out = new BufferedOutputStream(new FileOutputStream(temp2));
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (FileNotFoundException e) {
            Log.d("user","file not found exception "+e);
        } finally {
            if (out != null) try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
