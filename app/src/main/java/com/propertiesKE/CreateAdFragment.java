package com.propertiesKE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageBase64;
import com.kosalgeek.android.photoutil.PhotoLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class CreateAdFragment extends Fragment {
    Spinner category_spinner, county_spinner, type_spinner;
    EditText title, description, price, bedrooms, bathrooms, surface;
    TextView titleCount, descriptionCount, ad_pics;
    ImageView ad_pic, ad_pic_cancel, ad_pics_cancel;
    Button b_upload;
    ProgressBar progress;
    public static final String DEFAULT = "N/A";
    String SFirstname, SLastname, SMobile, SEmail, SCategory, SCounty,
            SType, SPid, STitle, SDescription, SPrice, SNegotiable,
            SBedrooms, SBathrooms, SSurface, SPet, SRandom_No, SType2,
            SDuration, SBouquet, SAmount, SImage, encodedString;
    ConnectivityManager cManager;
    NetworkInfo nInfo;
    private static Bitmap bitmap;
    private static final int PICK_FILE_REQUEST = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    private String selectedFilePath;
    public static final int progress_bar_type = 0;
    CheckBox negotiable, pet;
    final int GALLERY_REQUEST = 1200;
    LinearLayout linearMain;
    ArrayList<String> imageList = new ArrayList<>();
    GalleryPhoto galleryPhoto;
    MyCommand myCommand;

    public CreateAdFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_ad, container, false);

        progress = view.findViewById(R.id.progress);
        category_spinner = view.findViewById(R.id.category_spinner);
        county_spinner = view.findViewById(R.id.county_spinner);
        type_spinner = view.findViewById(R.id.type_spinner);
        title = view.findViewById(R.id.title);
        description = view.findViewById(R.id.description);
        price = view.findViewById(R.id.price);
        titleCount = view.findViewById(R.id.titleCount);
        descriptionCount = view.findViewById(R.id.descriptionCount);
        ad_pic = view.findViewById(R.id.ad_pic);
        b_upload = view.findViewById(R.id.b_upload);
        negotiable = view.findViewById(R.id.negotiable);
        pet = view.findViewById(R.id.pet);
        bedrooms = view.findViewById(R.id.bedrooms);
        bathrooms = view.findViewById(R.id.bathrooms);
        surface = view.findViewById(R.id.surface);
        ad_pics = view.findViewById(R.id.ad_pics);
        linearMain = view.findViewById(R.id.linearMain);
        ad_pic_cancel = view.findViewById(R.id.ad_pic_cancel);
        ad_pics_cancel = view.findViewById(R.id.ad_pics_cancel);

        ad_pics_cancel.setVisibility(View.GONE);
        ad_pic_cancel.setVisibility(View.GONE);

        galleryPhoto = new GalleryPhoto(getActivity().getApplicationContext());
        myCommand = new MyCommand(getActivity().getApplicationContext());

        title.addTextChangedListener(mTextEditorWatcherTitle);
        description.addTextChangedListener(mTextEditorWatcherDescription);

        //getting information entered by user
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SFirstname = sharedPreferences.getString(Constants.FIRSTNAME, DEFAULT);
        SLastname = sharedPreferences.getString(Constants.LASTNAME, DEFAULT);
        SMobile = sharedPreferences.getString(Constants.MOBILE, DEFAULT);
        SEmail = sharedPreferences.getString(Constants.EMAIL2, DEFAULT);

        cManager = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
        nInfo = cManager.getActiveNetworkInfo();

        category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                String selectedItem = parentView.getItemAtPosition(position).toString();

                if (Objects.equals(selectedItem, "Land") ||
                        Objects.equals(selectedItem, "Plot") ||
                        Objects.equals(selectedItem, "Commercial Building") ||
                        Objects.equals(selectedItem, "Godown") ||
                        Objects.equals(selectedItem, "Shop") ||
                        Objects.equals(selectedItem, "Office") ||
                        Objects.equals(selectedItem, "Short Term Rental") ||
                        Objects.equals(selectedItem, "Holiday Rental")) {

                    bedrooms.setVisibility(View.GONE);
                    bathrooms.setVisibility(View.GONE);
                    pet.setVisibility(View.GONE);
                    bathrooms.setText("0");
                    bedrooms.setText("0");
                } else {
                    bedrooms.setVisibility(View.VISIBLE);
                    bathrooms.setVisibility(View.VISIBLE);
                    pet.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //catch me outside when nothing is selected
            }

        });

        ad_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = galleryPhoto.openGalleryIntent();
                startActivityForResult(in, PICK_FILE_REQUEST);
            }
        });

        ad_pics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //on attachment icon click
                Intent in = galleryPhoto.openGalleryIntent();
                startActivityForResult(in, GALLERY_REQUEST);
            }
        });

        ad_pic_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedFilePath = null;
                ad_pic.setImageDrawable(null);

                ad_pic_cancel.setVisibility(View.GONE);
            }
        });

        ad_pics_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageList.clear();
                linearMain.removeAllViews();

                ad_pics_cancel.setVisibility(View.GONE);
            }
        });

        b_upload.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                try {
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (Exception e) {
                    //catch something
                    e.printStackTrace();
                }

                SCategory = category_spinner.getSelectedItem().toString();
                SCounty = county_spinner.getSelectedItem().toString();
                SType = type_spinner.getSelectedItem().toString();

                STitle = title.getText().toString();
                SDescription = description.getText().toString();
                SPrice = price.getText().toString();

                SBedrooms = bedrooms.getText().toString();
                SBathrooms = bathrooms.getText().toString();

                if (Objects.equals(SType, "Normal Ad 1 week - Kes.150")) {

                    SType2 = "Normal";
                    SDuration = "1";
                    SBouquet = "week";
                    SAmount = "150";
                } else if (Objects.equals(SType, "Normal Ad 1 month - Kes.400")) {

                    SType2 = "Normal";
                    SDuration = "1";
                    SBouquet = "month";
                    SAmount = "400";
                } else if (Objects.equals(SType, "Normal Ad 2 months - Kes.750")) {

                    SType2 = "Normal";
                    SDuration = "2";
                    SBouquet = "month";
                    SAmount = "750";
                } else if (Objects.equals(SType, "Normal Ad 3 months - Kes.1,000")) {

                    SType2 = "Normal";
                    SDuration = "3";
                    SBouquet = "month";
                    SAmount = "1000";
                } else if (Objects.equals(SType, "Top Ad 1 week - Kes.1,000")) {

                    SType2 = "Top";
                    SDuration = "1";
                    SBouquet = "week";
                    SAmount = "1000";
                } else if (Objects.equals(SType, "Top Ad 1 month - Kes.3,000")) {

                    SType2 = "Top";
                    SDuration = "1";
                    SBouquet = "month";
                    SAmount = "3000";
                }

                if (pet.isChecked()) {
                    SPet = "yes";
                } else {
                    SPet = "no";
                }

                SSurface = surface.getText().toString();

                if (negotiable.isChecked()) {
                    SNegotiable = "yes";
                } else {
                    SNegotiable = "no";
                }

                switch (SCategory) {
                    case "Land":
                        SPid = String.valueOf(1);

                        break;
                    case "Plot":
                        SPid = String.valueOf(2);

                        break;
                    case "Commercial Building":
                        SPid = String.valueOf(3);

                        break;
                    case "Godown":
                        SPid = String.valueOf(4);

                        break;
                    case "Shop":
                        SPid = String.valueOf(5);

                        break;
                    case "Office":
                        SPid = String.valueOf(6);

                        break;
                    case "Studio":
                        SPid = String.valueOf(7);

                        break;
                    case "House":
                        SPid = String.valueOf(8);

                        break;
                    case "Villa":
                        SPid = String.valueOf(9);

                        break;
                    case "Apartment":
                        SPid = String.valueOf(10);

                        break;
                    case "Bungalow":
                        SPid = String.valueOf(11);

                        break;
                    case "Maisonette":
                        SPid = String.valueOf(12);

                        break;
                    case "Short Term Rental":
                        SPid = String.valueOf(13);

                        break;
                    case "Holiday Rental":
                        SPid = String.valueOf(14);

                        break;
                }

                if (SFirstname != null && SLastname != null && SMobile != null && SEmail != null) {
                    if (nInfo != null && nInfo.isConnected()) {
                        if (title.length() > 0 &&
                                title.length() < 71 &&
                                description.length() > 0 &&
                                description.length() < 4001 &&
                                price.length() > 0 &&
                                surface.length() > 0) {
                            //everything is fine, proceed to database upload
                            b_upload.setVisibility(View.INVISIBLE);
                            progress.setVisibility(View.VISIBLE);
                            if (selectedFilePath != null) {
                                getActivity().showDialog(progress_bar_type);
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            //everything is fine and main picture is already selected
                                            uploadToDatabase();
                                        } catch (OutOfMemoryError e) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getActivity(), "Insufficient Memory :(", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            getActivity().dismissDialog(progress_bar_type);
                                        }
                                    }
                                }).start();
                            } else {
                                //when user has not selected an image for the main picture
                                b_upload.setVisibility(View.VISIBLE);
                                progress.setVisibility(View.INVISIBLE);

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                                alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
                                alertDialogBuilder.setMessage("Please Select Picture to Continue");

                                alertDialogBuilder.setPositiveButton("Select", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        ad_pic.callOnClick();
                                    }
                                });

                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();

                            }

                        } else {
                            //when several fields are left empty
                            title.setError("Check Field");
                            description.setError("Check Field");
                            price.setError("Check Field");
                            bedrooms.setError("Check Field");
                            bathrooms.setError("Check Field");
                            surface.setError("Check Field");
                        }
                    } else {
                        goToInternetPage();
                    }
                }
            }
        });

        return view;
    }

    private final TextWatcher mTextEditorWatcherTitle = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            titleCount.setText("Limit is 70 characters");
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //checks current number of characters being typed
            titleCount.setText(String.valueOf(s.length()));
        }

        public void afterTextChanged(Editable s) {
            //checks that the text limit is not exceeded by the user
            if (s.length() > 70) {
                titleCount.setText("Exceeded limit of 70 characters");
            }
        }
    };

    private final TextWatcher mTextEditorWatcherDescription = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            descriptionCount.setText("Limit is 4000 characters");
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a text view to the current length
            descriptionCount.setText(String.valueOf(s.length()));
        }

        public void afterTextChanged(Editable s) {
            if (s.length() > 4000) {
                descriptionCount.setText("Exceeded limit of 4000 characters");
            }
        }
    };

    private void goToInternetPage() {
        Intent intent = new Intent(getActivity(), InternetActivity.class);
        getActivity().startActivity(intent);
    }

    private void goToMain() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        getActivity().startActivity(intent);
        getActivity().finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_FILE_REQUEST) {
                if (data == null) {
                    //no data present
                    return;
                }
                Uri filePath = data.getData();
                try {
                    //imageList.clear();
                    //linearMain.removeAllViews();

                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                    galleryPhoto.setPhotoUri(data.getData());
                    //String photoPath = galleryPhoto.getPath();
                    //imageList.add(photoPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                selectedFilePath = FilePath.getPath(getActivity(), filePath);
                if (selectedFilePath != null && !selectedFilePath.equals("")) {
                    Glide
                            .with(getActivity())
                            .load(filePath)
                            .crossFade()
                            .into(new GlideDrawableImageViewTarget(ad_pic));

                    ad_pic_cancel.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getActivity(), "Cannot upload this picture", Toast.LENGTH_SHORT).show();
                }

            } else if (requestCode == GALLERY_REQUEST) {
                galleryPhoto.setPhotoUri(data.getData());
                Uri filePath = data.getData();
                String photoPath = galleryPhoto.getPath();
                imageList.add(photoPath);
                try {
                    ImageView imageView = new ImageView(getActivity().getApplicationContext());
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    imageView.setLayoutParams(layoutParams);
                    //imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    imageView.setPadding(5, 0, 5, 0);
                    imageView.setAdjustViewBounds(true);

                    Glide
                            .with(getActivity())
                            .load(filePath)
                            .crossFade()
                            .into(new GlideDrawableImageViewTarget(imageView));

                    linearMain.addView(imageView);

                    ad_pics_cancel.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Error while loading image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void uploadToDatabase() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.UPLOAD_AD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();

                        if (s.equals("Failed to Created Dynamic Folder") ||
                                s.equals("Error Uploading") ||
                                s.equals("Your Ad already exists")) {
                            progress.setVisibility(View.INVISIBLE);
                            b_upload.setVisibility(View.VISIBLE);

                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                            builder.setTitle(s);
                            builder.setMessage(s + "\nTry again?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            uploadToDatabase();
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //User cancelled the dialog
                                            dialog.dismiss();
                                        }
                                    });
                            builder.show();
                        } else {
                            Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
                            SRandom_No = s;
                            SImage = getStringImage(bitmap);
                            new uploadAdPicture().execute(new ApiConnector());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        progress.setVisibility(View.INVISIBLE);
                        b_upload.setVisibility(View.VISIBLE);

                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                        builder.setMessage("Sorry, an error occurred.\nTry again?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        uploadToDatabase();
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
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put("pid", SPid);
                params.put("county", SCounty);
                params.put("firstname", SFirstname.replaceAll("'", "''"));
                params.put("lastname", SLastname.replaceAll("'", "''"));
                params.put("mobile", SMobile);
                params.put("email", SEmail);
                params.put("title", STitle.replaceAll("'", "''"));
                params.put("description", SDescription.replaceAll("'", "''"));
                params.put("price", SPrice);
                params.put("negotiable", SNegotiable);
                params.put("bedrooms", SBedrooms);
                params.put("bathrooms", SBathrooms);
                params.put("surface", SSurface);
                params.put("pet", SPet);
                params.put("ad_type", SType2);
                params.put("bouquet", SBouquet);
                params.put("duration", SDuration);
                params.put("amount", SAmount);
                //params.put("REQUEST_METHOD", "POST");

                //returning parameters
                return params;
            }
        };
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    @SuppressLint("StaticFieldLeak")
    private class uploadAdPicture extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].UploadAdPicture(SImage, SRandom_No);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {
                if (response.equals("success")) {
                    if (SRandom_No != null) {

                        Toast.makeText(getActivity(), SRandom_No, Toast.LENGTH_SHORT).show();

                        int itemCount = imageList.size();
                        if (itemCount > 1) {
                            for (String imagePath : imageList) {
                                Bitmap bitmap = PhotoLoader.init().from(imagePath).requestSize(512, 512).getBitmap();
                                encodedString = ImageBase64.encode(bitmap);

                                new uploadAdPictures().execute(new ApiConnector());
                            }
                        } else {
                            imageNotification();
                            goToMain();
                        }
                    } else {
                        Toast.makeText(getActivity(), "An Unknown Error Occurred \n Please Try Again.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progress.setVisibility(View.INVISIBLE);
                    b_upload.setVisibility(View.VISIBLE);

                    Toast toast = Toast.makeText(getActivity(), response, Toast.LENGTH_LONG);
                    View toastView = toast.getView(); //This'll return the default View of the Toast.
                    TextView toastMessage = toastView.findViewById(android.R.id.message);
                    toastMessage.setTextSize(12);
                    toastMessage.setTextColor(getResources().getColor(R.color.white));
                    toastMessage.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_launcher, 0, 0, 0);
                    toastMessage.setGravity(Gravity.CENTER);
                    toastMessage.setCompoundDrawablePadding(10);
                    toastView.setBackground(getResources().getDrawable(R.drawable.bg_button2));
                    toast.show();
                    Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();

                    imageNotification();
                    goToMain();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class uploadAdPictures extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].UploadAdPictures(encodedString, SRandom_No);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {
                if (Objects.equals(response, "uploaded_failed") || Objects.equals(response, "image_not_in")) {
                    try {
                        Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                        progress.setVisibility(View.INVISIBLE);
                        b_upload.setVisibility(View.VISIBLE);
                        goToMain();
                    } catch (Exception e) {
                        //catch me outside
                        e.printStackTrace();
                    }

                    Toast.makeText(getActivity(), "Additional pictures Failed to Upload", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        imageNotification();
                        goToMain();
                    } catch (Exception e) {
                        //catch me outside
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void imageNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());
        Notification notification;
        notification = builder.setSmallIcon(R.drawable.ic_stat_name).setTicker("Your Ad has been posted.").setWhen(0)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Your Ad has been posted.")
                .setLargeIcon(BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.ic_launcher))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(SFirstname + " please pay for your ad to complete the process."))
                .setAutoCancel(true)
                .setContentText(SFirstname + " please pay for your ad to complete the process.")
                .build();

        Intent notificationIntent = new Intent(getActivity(), MainActivity.class);
        notificationIntent.putExtra("fragmentCreate", "ads");
        PendingIntent contentIntent = PendingIntent.getActivity(getActivity(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        //add sound
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(uri);
        //vibrate
        long[] v = {500, 1000};
        builder.setVibrate(v);

        //Add as notification
        NotificationManager manager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.notify(0, builder.build());

    }
}
