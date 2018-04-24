package com.propertiesKE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import me.relex.circleindicator.CircleIndicator;

public class AdDetailsActivity extends AppCompatActivity {
    ImageView back, call_user, sms_user;
    private int ItemID;
    Button deleteAd;
    public static final String DEFAULT = "N/A";
    String SEmail, Smobile, SRandom, STitle, SMessage, SPemail, SAcode, SFullNames,Slogin,Sfirstname,Slastname;
    TextView top, property_type, title, location, price, negotiable, pet, description, date_posted,
            bedrooms, bathrooms, surface, ad_views, user_names, user_email;
    LinearLayout pet_linear, bathrooms_linear, bedrooms_linear;
    private ProgressDialog dialog;
    private ViewPager mPager;
    private GridView GetAllAdsListView;
    private JSONArray jsonArray;
    ImageView available;
    int Sproperty_type_int;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_details);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SEmail = sharedPreferences.getString(Constants.EMAIL2, DEFAULT);
        Slogin = sharedPreferences.getString(Constants.IS_LOGGED_IN, DEFAULT);
        Sfirstname = sharedPreferences.getString(Constants.FIRSTNAME, DEFAULT);
        Slastname = sharedPreferences.getString(Constants.LASTNAME, DEFAULT);

        back = findViewById(R.id.back);
        deleteAd = findViewById(R.id.deleteAd);
        deleteAd.setVisibility(View.GONE);
        top = findViewById(R.id.top);
        property_type = findViewById(R.id.property_type);
        title = findViewById(R.id.title);
        location = findViewById(R.id.location);
        price = findViewById(R.id.price);
        negotiable = findViewById(R.id.negotiable);
        pet = findViewById(R.id.pet);
        description = findViewById(R.id.description);
        date_posted = findViewById(R.id.date_posted);
        bedrooms = findViewById(R.id.bedrooms);
        bathrooms = findViewById(R.id.bathrooms);
        surface = findViewById(R.id.surface);
        ad_views = findViewById(R.id.ad_views);
        user_names = findViewById(R.id.user_names);
        user_email = findViewById(R.id.user_email);
        call_user = findViewById(R.id.call_user);
        sms_user = findViewById(R.id.sms_user);
        mPager = findViewById(R.id.slide_pager);
        GetAllAdsListView = findViewById(R.id.GetAdsListView);
        available = findViewById(R.id.available);

        pet_linear = findViewById(R.id.pet_linear);
        bathrooms_linear = findViewById(R.id.bathrooms_linear);
        bedrooms_linear = findViewById(R.id.bedrooms_linear);

        dialog = new ProgressDialog(AdDetailsActivity.this);

        call_user.setVisibility(View.INVISIBLE);
        sms_user.setVisibility(View.INVISIBLE);

        //get Customer ID
        this.ItemID = getIntent().getIntExtra("item", -1);

        if (this.ItemID > 0) {
            //we have customer ID passed correctly.
            new GetAdDetails().execute(new ApiConnector());
            dialog.setMessage("Ad loading...");
            dialog.setCancelable(false);
            dialog.show();
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        GetAllAdsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    // GEt the customer which was clicked
                    JSONObject itemClicked = jsonArray.getJSONObject(position);
                    // Send Customer ID
                    Intent showDetails = new Intent(getApplicationContext(), AdDetailsActivity.class);
                    showDetails.putExtra("item", itemClicked.getInt("aid"));
                    startActivity(showDetails);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        call_user.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (Objects.equals(SEmail, SPemail)) {
                    Toast toast = Toast.makeText(AdDetailsActivity.this, "You can't call yourself !", Toast.LENGTH_LONG);
                    View toastView = toast.getView(); //This'll return the default View of the Toast.
                    TextView toastMessage = toastView.findViewById(android.R.id.message);
                    toastMessage.setTextSize(12);
                    toastMessage.setTextColor(getResources().getColor(R.color.white));
                    toastMessage.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_launcher, 0, 0, 0);
                    toastMessage.setGravity(Gravity.CENTER);
                    toastMessage.setCompoundDrawablePadding(10);
                    toastView.setBackground(getResources().getDrawable(R.drawable.bg_button2));
                    toast.show();
                } else {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + Smobile));

                    if (ActivityCompat.checkSelfPermission(AdDetailsActivity.this,
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        startActivity(callIntent);
                    } catch (Exception e) {
                        //catch me outside
                        e.printStackTrace();
                    }
                }
            }
        });

        sms_user.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (Objects.equals(SEmail, SPemail)) {
                    Toast toast = Toast.makeText(AdDetailsActivity.this, "You can't text yourself !", Toast.LENGTH_LONG);
                    View toastView = toast.getView(); //This'll return the default View of the Toast.
                    TextView toastMessage = toastView.findViewById(android.R.id.message);
                    toastMessage.setTextSize(12);
                    toastMessage.setTextColor(getResources().getColor(R.color.white));
                    toastMessage.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_launcher, 0, 0, 0);
                    toastMessage.setGravity(Gravity.CENTER);
                    toastMessage.setCompoundDrawablePadding(10);
                    toastView.setBackground(getResources().getDrawable(R.drawable.bg_button2));
                    toast.show();
                } else {
                    final Dialog dialog = new Dialog(AdDetailsActivity.this);
                    dialog.setTitle("Pick an action");
                    dialog.setContentView(R.layout.dialog_message);

                    ImageView dialog_direct = dialog.findViewById(R.id.dialog_direct);
                    ImageView dialog_sms = dialog.findViewById(R.id.dialog_sms);

                    dialog_direct.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                            //Set user to chat with
                            UserDetails.chatWith = SFullNames;
                            UserDetails.username = Sfirstname + " " + Slastname;

                            if (!Objects.equals(Slogin, Constants.IS_LOGGED_IN_YES)) {
                                //if user hasn't logged in
                                goToEmail();
                            } else {
                                Intent showDetails = new Intent(getApplicationContext(), ChatActivity.class);
                                showDetails.putExtra("email", SPemail);
                                showDetails.putExtra("subject", STitle);
                                showDetails.putExtra("from", SEmail);
                                showDetails.putExtra("acode", SAcode);
                                showDetails.putExtra("from_name", SFullNames);
                                startActivity(showDetails);
                            }
                        }
                    });

                    dialog_sms.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                            Intent smsIntent = new Intent(Intent.ACTION_VIEW);

                            smsIntent.setData(Uri.parse("smsto:"));
                            smsIntent.setType("vnd.android-dir/mms-sms");
                            smsIntent.putExtra("address", Smobile);
                            smsIntent.putExtra("sms_body", "Hi, I want to ask about " + STitle + " from ORO classifieds.");

                            try {
                                startActivity(smsIntent);
                            } catch (Exception e) {
                                //catch me outside
                                e.printStackTrace();
                            }
                        }
                    });
                    dialog.show();
                }
            }
        });

        deleteAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AdDetailsActivity.this);
                alertDialogBuilder.setTitle("Warning !");
                alertDialogBuilder.setMessage("Are you sure you want to permanently delete this Ad ?");

                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        deleteMyAd();
                    }
                });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class GetAdDetails extends AsyncTask<ApiConnector, Long, JSONArray> {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].GetAdDetails(ItemID);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            try {
                JSONObject item = jsonArray.getJSONObject(0);

                String Image = item.getString("email");
                String Sproperty_type = item.getString("pid");
                String Snegotiable = item.getString("negotiable");
                String Spet = item.getString("pet");
                Smobile = "+" + item.getString("mobile");
                int SPrice = item.getInt("price");

                Sproperty_type_int = Integer.parseInt(Sproperty_type);

                if (Objects.equals(Sproperty_type, "1") ||
                        Objects.equals(Sproperty_type, "2") ||
                        Objects.equals(Sproperty_type, "3") ||
                        Objects.equals(Sproperty_type, "4") ||
                        Objects.equals(Sproperty_type, "5") ||
                        Objects.equals(Sproperty_type, "6") ||
                        Objects.equals(Sproperty_type, "13") ||
                        Objects.equals(Sproperty_type, "14")) {

                    bedrooms_linear.setVisibility(View.GONE);
                    bathrooms_linear.setVisibility(View.GONE);
                    pet_linear.setVisibility(View.GONE);
                } else {
                    bedrooms_linear.setVisibility(View.VISIBLE);
                    bathrooms_linear.setVisibility(View.VISIBLE);
                    pet_linear.setVisibility(View.VISIBLE);
                }

                switch (Sproperty_type) {
                    case "1":
                        property_type.setText("Land");
                        break;
                    case "2":
                        property_type.setText("Plot");
                        break;
                    case "3":
                        property_type.setText("Commercial Building");
                        break;
                    case "4":
                        property_type.setText("Godown");
                        break;
                    case "5":
                        property_type.setText("Shop");
                        break;
                    case "6":
                        property_type.setText("Office");
                        break;
                    case "7":
                        property_type.setText("Studio");
                        break;
                    case "8":
                        property_type.setText("House");
                        break;
                    case "9":
                        property_type.setText("Villa");
                        break;
                    case "10":
                        property_type.setText("Apartment");
                        break;
                    case "11":
                        property_type.setText("Bungalow");
                        break;
                    case "12":
                        property_type.setText("Maisonette");
                        break;
                    case "13":
                        property_type.setText("Short Term Rental");
                        break;
                    case "14":
                        property_type.setText("Holiday Rental");
                        break;
                }

                STitle = item.getString("title");
                title.setText(STitle);

                location.setText(item.getString("county").replaceAll("_", " "));

                String Sprice2 = NumberFormat.getNumberInstance(Locale.US).format(SPrice);
                price.setText("Kes. " + Sprice2);

                if (Objects.equals(item.getString("ad_type"), "Top")) {
                    top.setVisibility(View.VISIBLE);
                } else {
                    top.setVisibility(View.INVISIBLE);
                }

                if (Objects.equals(Snegotiable, "yes")) {
                    negotiable.setText("Negotiable");
                } else {
                    negotiable.setText("Not negotiable");
                }

                if (Objects.equals(Spet, "yes")) {
                    pet.setText("Yes");
                } else {
                    pet.setText("No");
                }

                description.setText(item.getString("description"));
                date_posted.setText("Date Added: " + item.getString("date_posted") + "   " + item.getString("time_posted"));
                bedrooms.setText(item.getString("bedrooms"));
                bathrooms.setText(item.getString("bathrooms"));
                surface.setText(item.getString("surface"));
                ad_views.setText(item.getString("ad_views"));

                user_names.setText(item.getString("firstname") + " " + item.getString("lastname"));
                user_email.setText(item.getString("email"));

                SAcode = (item.getString("acode"));
                SPemail = (item.getString("email"));
                SFullNames = (item.getString("firstname") + " " + item.getString("lastname"));

                if (SEmail.equals(Image)) {
                    deleteAd.setVisibility(View.VISIBLE);
                }

                call_user.setVisibility(View.VISIBLE);
                sms_user.setVisibility(View.VISIBLE);

                SRandom = item.getString("acode");

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

                SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.RANDOM, SRandom);
                editor.apply();

                new GetPics().execute(new ApiConnector());

                new GetAllAdsTask().execute(new ApiConnector());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteMyAd() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.AD_DELETE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (s.equals("Successful")) {
                            Toast toast = Toast.makeText(AdDetailsActivity.this, "Ad has been deleted!" + "  ", Toast.LENGTH_LONG);
                            View toastView = toast.getView(); //This'll return the default View of the Toast.
                            TextView toastMessage = toastView.findViewById(android.R.id.message);
                            toastMessage.setTextSize(12);
                            toastMessage.setTextColor(getResources().getColor(R.color.white));
                            toastMessage.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_launcher, 0, 0, 0);
                            toastMessage.setGravity(Gravity.CENTER);
                            toastMessage.setCompoundDrawablePadding(10);
                            toastView.setBackground(getResources().getDrawable(R.drawable.bg_button2));
                            toast.show();

                            onBackPressed();
                        } else {
                            deleteAd.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        deleteAd.setVisibility(View.VISIBLE);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();
                String Post = String.valueOf(ItemID);
                //Adding parameters
                params.put("aid", Post);
                params.put("REQUEST_METHOD", "POST");

                //returning parameters
                return params;
            }
        };
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    @SuppressLint("StaticFieldLeak")
    private class GetPics extends AsyncTask<ApiConnector, Long, JSONArray> {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].GetAllPics(SRandom);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            try {
                List<String> list = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {

                    if (!Objects.equals(jsonArray.getString(i), ".") &&
                            !Objects.equals(jsonArray.getString(i), "..")) {

                        list.add(jsonArray.getString(i));

                        mPager.setAdapter(new MyAdapterAdDetails(AdDetailsActivity.this, list));
                        CircleIndicator indicator = findViewById(R.id.slide_indicator);
                        assert indicator != null;
                        indicator.setViewPager(mPager);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setListAdapter(JSONArray jsonArray) {
        try {
            this.jsonArray = jsonArray;
            this.GetAllAdsListView.setAdapter(new GetAdsListViewAdapter(jsonArray, this));
            if (jsonArray == null) {
                available.setVisibility(View.VISIBLE);
                GetAllAdsListView.setVisibility(View.GONE);
            }

            //swipe_refresh_layout.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetAllAdsTask extends AsyncTask<ApiConnector, Long, JSONArray> {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {
            try {
                GetAllAdsListView.setVisibility(View.VISIBLE);
                // it is executed on Background thread
                return params[0].GetTabAds(Sproperty_type_int);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            setListAdapter(jsonArray);
        }
    }

    private void goToEmail() {
        //Not logged in, send message as email
        Intent showDetails = new Intent(getApplicationContext(), EmailActivity.class);
        showDetails.putExtra("email", SPemail);
        showDetails.putExtra("subject", STitle);
        showDetails.putExtra("acode", SAcode);
        startActivity(showDetails);
        finish();
    }
}
