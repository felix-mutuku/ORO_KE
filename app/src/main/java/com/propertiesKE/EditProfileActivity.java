package com.propertiesKE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import static com.basgeekball.awesomevalidation.ValidationStyle.COLORATION;

public class EditProfileActivity extends AppCompatActivity {
    ImageView back, save, profile_pic;
    EditText mobile, first_name, last_name;
    public static final String DEFAULT = "N/A";
    String SMobile, SFirstname, SLastname, SEmail,SPath,SImage;
    ConnectivityManager cManager;
    NetworkInfo nInfo;
    private static Bitmap bitmap;
    ProgressBar progress;
    private String selectedFilePath;
    public static final int progress_bar_type = 0;
    private static final int PICK_FILE_REQUEST = 1;
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        progress = findViewById(R.id.progress);
        back = findViewById(R.id.back);
        save = findViewById(R.id.save);
        profile_pic = findViewById(R.id.profile_pic);
        mobile = findViewById(R.id.mobile);
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);

        cManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        nInfo = cManager.getActiveNetworkInfo();

        if (nInfo != null && nInfo.isConnected()) {
            //getting information entered by user
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SFirstname = sharedPreferences.getString(Constants.FIRSTNAME, DEFAULT);
            SLastname = sharedPreferences.getString(Constants.LASTNAME, DEFAULT);
            SMobile = sharedPreferences.getString(Constants.MOBILE, DEFAULT);
            SEmail = sharedPreferences.getString(Constants.EMAIL2, DEFAULT);
            SPath = sharedPreferences.getString(Constants.PATH, DEFAULT);

            first_name.setText(SFirstname);
            last_name.setText(SLastname);
            mobile.setText(SMobile);

            Glide
                    .with(EditProfileActivity.this)
                    .load(Constants.BASE_URL2 + "passport/profiles/" + SPath)
                    .error(R.drawable.non_image)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .crossFade()
                    .into(profile_pic);

        } else {
            goToInternetPage();
        }

        awesomeValidation = new AwesomeValidation(COLORATION);
        awesomeValidation.setColor(Color.RED);
        awesomeValidation.addValidation(this, R.id.mobile, Patterns.PHONE, R.string.invalid_number);

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

        mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String enteredString = s.toString();
                if (enteredString.startsWith("0")) {
                    mobile.setText("254");
                    mobile.setSelection(mobile.getText().length());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //before typing
            }

            @Override
            public void afterTextChanged(Editable s) {
                //after typing
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (first_name.length() > 0 ||
                        last_name.length() > 0 ||
                        mobile.length() > 0) {
                    //everything is fine, proceed to database upload
                    save.setVisibility(View.INVISIBLE);
                    progress.setVisibility(View.VISIBLE);
                    back.setVisibility(View.INVISIBLE);

                    if (selectedFilePath != null) {
                        showDialog(progress_bar_type);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    new uploadImage().execute(new ApiConnector());
                                } catch (OutOfMemoryError e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            save.setVisibility(View.VISIBLE);
                                            back.setVisibility(View.VISIBLE);
                                            Toast.makeText(EditProfileActivity.this, "Insufficient Memory :(", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    dismissDialog(progress_bar_type);
                                }
                            }
                        }).start();
                    } else {
                        if (awesomeValidation.validate()) {
                            uploadToDatabase();
                        } else {
                            save.setVisibility(View.INVISIBLE);
                            progress.setVisibility(View.VISIBLE);
                            back.setVisibility(View.INVISIBLE);
                        }
                    }
                } else {
                    //name is empty
                    first_name.setError("Check Fields");
                    last_name.setError("Check Fields");
                    mobile.setError("Check Fields");
                }
            }
        });

        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                char[] chars = {' ', '\'', '"'};
                for (int i = start; i < end; i++) {
                    if (new String(chars).contains(String.valueOf(source.charAt(i)))) {
                        return "_";
                    }
                }
                return null;
            }
        };

        first_name.setFilters(new InputFilter[]{filter});
        last_name.setFilters(new InputFilter[]{filter});
    }

    private void goToInternetPage() {
        Intent intent = new Intent(EditProfileActivity.this, InternetActivity.class);
        EditProfileActivity.this.startActivity(intent);
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

                selectedFilePath = FilePath.getPath(this, filePath);

                if (selectedFilePath != null && !selectedFilePath.equals("")) {
                    Glide
                            .with(EditProfileActivity.this)
                            .load(filePath)
                            .crossFade()
                            .into(new GlideDrawableImageViewTarget(profile_pic));

                    SImage = getStringImage(bitmap);

                } else {
                    Toast.makeText(this, "Cannot Upload picture", Toast.LENGTH_SHORT).show();
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
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(EditProfileActivity.this);

                if (response.equals("Successful")) {
                    progress.setVisibility(View.INVISIBLE);
                    Toast toast = Toast.makeText(EditProfileActivity.this, "Picture Uploaded Successfully :)", Toast.LENGTH_LONG);
                    View toastView = toast.getView(); //This'll return the default View of the Toast.
                    TextView toastMessage = toastView.findViewById(android.R.id.message);
                    toastMessage.setTextSize(12);
                    toastMessage.setTextColor(getResources().getColor(R.color.white));
                    toastMessage.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_launcher, 0, 0, 0);
                    toastMessage.setGravity(Gravity.CENTER);
                    toastMessage.setCompoundDrawablePadding(10);
                    toastView.setBackground(getResources().getDrawable(R.drawable.bg_button2));
                    toast.show();

                    uploadToDatabase();
                } else {
                    progress.setVisibility(View.INVISIBLE);
                    save.setVisibility(View.VISIBLE);
                    back.setVisibility(View.VISIBLE);

                    builder.setMessage("Sorry, an error occurred.\nTry again?")
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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadToDatabase() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.EDIT_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (s.equals("Successful")) {
                            Toast toast = Toast.makeText(EditProfileActivity.this, s + "  ", Toast.LENGTH_LONG);
                            View toastView = toast.getView(); //This'll return the default View of the Toast.
                            TextView toastMessage = toastView.findViewById(android.R.id.message);
                            toastMessage.setTextSize(12);
                            toastMessage.setTextColor(getResources().getColor(R.color.white));
                            toastMessage.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_launcher, 0, 0, 0);
                            toastMessage.setGravity(Gravity.CENTER);
                            toastMessage.setCompoundDrawablePadding(10);
                            toastView.setBackground(getResources().getDrawable(R.drawable.bg_button2));
                            toast.show();

                            SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Constants.FIRSTNAME, first_name.getText().toString());
                            editor.putString(Constants.LASTNAME, last_name.getText().toString());
                            editor.putString(Constants.MOBILE, mobile.getText().toString());
                            editor.apply();

                            onBackPressed();
                        } else {
                            Toast toast = Toast.makeText(EditProfileActivity.this, s + "  ", Toast.LENGTH_LONG);
                            View toastView = toast.getView(); //This'll return the default View of the Toast.
                            TextView toastMessage = toastView.findViewById(android.R.id.message);
                            toastMessage.setTextSize(12);
                            toastMessage.setTextColor(getResources().getColor(R.color.white));
                            toastMessage.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_launcher, 0, 0, 0);
                            toastMessage.setGravity(Gravity.CENTER);
                            toastMessage.setCompoundDrawablePadding(10);
                            toastView.setBackground(getResources().getDrawable(R.drawable.bg_button2));
                            toast.show();

                            save.setVisibility(View.VISIBLE);
                            progress.setVisibility(View.INVISIBLE);
                            back.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        progress.setVisibility(View.INVISIBLE);
                        save.setVisibility(View.VISIBLE);
                        back.setVisibility(View.VISIBLE);
                        //Showing toast
                        Snackbar snackbar = Snackbar.make(getCurrentFocus(), "Error! Please try again", Snackbar.LENGTH_LONG)
                                .setAction("ok", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        uploadToDatabase();
                                    }
                                });
                        //ACTION
                        snackbar.setActionTextColor(getResources().getColor(R.color.white));
                        View snackBarView = snackbar.getView();
                        //BACKGROUND
                        snackBarView.setBackgroundColor(getResources().getColor(R.color.black));
                        //MESSAGE
                        TextView tv = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(getResources().getColor(R.color.white));
                        snackbar.show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();
                //Adding parameters
                params.put("firstname", first_name.getText().toString());
                params.put("lastname", last_name.getText().toString());
                params.put("mobile", mobile.getText().toString());
                params.put("email", SEmail);

                //returning parameters
                return params;
            }
        };
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

}
