package com.propertiesKE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {
    TextView mobile, email, last_name, first_name;
    ImageView back, profile_pic;
    Button btn_register;
    ProgressBar progress;
    public static final String DEFAULT = "N/A";
    private static final int PICK_FILE_REQUEST = 1;
    private String selectedFilePath2;
    String SFirstname, SLastname, SEmail, SMobile, SPassword, SImage, fileName;
    private static Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mobile = findViewById(R.id.mobile);
        email = findViewById(R.id.email);
        last_name = findViewById(R.id.last_name);
        first_name = findViewById(R.id.first_name);

        back = findViewById(R.id.back);
        profile_pic = findViewById(R.id.profile_pic);

        btn_register = findViewById(R.id.btn_register);
        progress = findViewById(R.id.progress);

        //getting information entered by user
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SFirstname = sharedPreferences.getString(Constants.FIRSTNAME, DEFAULT);
        SLastname = sharedPreferences.getString(Constants.LASTNAME, DEFAULT);
        SEmail = sharedPreferences.getString(Constants.EMAIL, DEFAULT);
        SMobile = sharedPreferences.getString(Constants.MOBILE, DEFAULT);
        SPassword = sharedPreferences.getString(Constants.PASSWORD, DEFAULT);

        if (SFirstname.equals(DEFAULT) ||
                SLastname.equals(DEFAULT) ||
                SEmail.equals(DEFAULT) ||
                SMobile.equals(DEFAULT) ||
                SPassword.equals(DEFAULT)) {

            //show error
            Toast toast = Toast.makeText(this, "Error.Please try again.", Toast.LENGTH_LONG);
            toast.show();

            startActivity(new Intent(RegisterActivity.this, RNameActivity.class));
            finish();
        } else {
            //everything is fine, show data
            first_name.setText("First Name:   " + SFirstname);
            last_name.setText("Last Name:   " + SLastname);
            email.setText("Email:   " + SEmail);
            mobile.setText("Phone Number:   +" + SMobile);
        }

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_register.setVisibility(View.INVISIBLE);
                progress.setVisibility(View.VISIBLE);
                if (selectedFilePath2 != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                new registerUser().execute(new ApiConnector());

                            } catch (OutOfMemoryError e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        btn_register.setVisibility(View.VISIBLE);
                                        progress.setVisibility(View.INVISIBLE);
                                        Toast.makeText(RegisterActivity.this, "Insufficient Memory :(", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).start();
                } else {
                    btn_register.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.INVISIBLE);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegisterActivity.this);
                    alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
                    alertDialogBuilder.setMessage("Please Select a Profile Picture to Continue");

                    alertDialogBuilder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            profile_pic.callOnClick();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //on attachment icon click
                showFileChooser();
            }
        });
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        //sets the select file to all types of files
        intent.setType("image/*");//select any file on the device
        //allows to select data and return it
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //starts new activity to select file and return data
        startActivityForResult(Intent.createChooser(intent, "Choose Your Image"), PICK_FILE_REQUEST);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_FILE_REQUEST) {
                if (data == null) {
                    //no data present
                    return;
                }

                Uri filePath = data.getData();

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                selectedFilePath2 = FilePath.getPath(this, filePath);

                if (selectedFilePath2 != null && !selectedFilePath2.equals("")) {
                    Glide
                            .with(RegisterActivity.this)
                            .load(filePath)
                            .crossFade()
                            .into(new GlideDrawableImageViewTarget(profile_pic));

                    SImage = getStringImage(bitmap);

                    new uploadImage().execute(new ApiConnector());
                    //uploadFile(selectedFilePath2);
                    btn_register.setVisibility(View.INVISIBLE);
                    progress.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(RegisterActivity.this, "Cannot Upload picture", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class uploadImage extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].UploadProfilePicture(SEmail, SImage);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(RegisterActivity.this);

                if (response.equals("Successful")) {
                    progress.setVisibility(View.INVISIBLE);
                    Toast toast = Toast.makeText(RegisterActivity.this, "Picture Uploaded Successfully :)", Toast.LENGTH_LONG);
                    View toastView = toast.getView(); //This'll return the default View of the Toast.
                    TextView toastMessage = toastView.findViewById(android.R.id.message);
                    toastMessage.setTextSize(12);
                    toastMessage.setTextColor(getResources().getColor(R.color.white));
                    toastMessage.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_launcher, 0, 0, 0);
                    toastMessage.setGravity(Gravity.CENTER);
                    toastMessage.setCompoundDrawablePadding(10);
                    toastView.setBackground(getResources().getDrawable(R.drawable.bg_button2));
                    toast.show();

                    progress.setVisibility(View.INVISIBLE);
                    btn_register.setVisibility(View.VISIBLE);
                } else {
                    progress.setVisibility(View.INVISIBLE);
                    btn_register.setVisibility(View.VISIBLE);

                    builder.setMessage(response + "\n\nTry again?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    new uploadImage().execute(new ApiConnector());
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                    dialog.dismiss();
                                }
                            });
                    builder.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class registerUser extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].RegisterUser(SFirstname, SLastname, SEmail, SMobile, SPassword);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {

                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(RegisterActivity.this);

                if (response.equals("Successful")) {
                    progress.setVisibility(View.INVISIBLE);
                    Toast toast = Toast.makeText(RegisterActivity.this, "You are Now Registered", Toast.LENGTH_LONG);
                    View toastView = toast.getView(); //This'll return the default View of the Toast.
                    TextView toastMessage = toastView.findViewById(android.R.id.message);
                    toastMessage.setTextSize(12);
                    toastMessage.setTextColor(getResources().getColor(R.color.white));
                    toastMessage.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_launcher, 0, 0, 0);
                    toastMessage.setGravity(Gravity.CENTER);
                    toastMessage.setCompoundDrawablePadding(10);
                    toastView.setBackground(getResources().getDrawable(R.drawable.bg_button2));
                    toast.show();

                    goToMain();
                } else {
                    progress.setVisibility(View.INVISIBLE);
                    btn_register.setVisibility(View.VISIBLE);

                    builder.setMessage(response + "\n\nTry again?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    new registerUser().execute(new ApiConnector());
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                    dialog.dismiss();
                                }
                            });

                    builder.show();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void goToMain() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        RegisterActivity.this.startActivity(intent);
        finish();
    }
}
