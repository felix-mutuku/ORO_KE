package com.propertiesKE;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

public class ProfileFragment extends Fragment {
    ImageView profile_pic;
    TextView userTxt, emailTxt, mobileTxt;
    Button sign_out_button;
    String SEmail;
    public static final String DEFAULT = "N/A";
    SwipeRefreshLayout swipe_refresh_layout;
    Button edit_profile;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //getting information entered by user
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SEmail = sharedPreferences.getString(Constants.EMAIL2, DEFAULT);

        profile_pic = view.findViewById(R.id.profile_pic);
        userTxt = view.findViewById(R.id.userTxt);
        emailTxt = view.findViewById(R.id.emailTxt);
        mobileTxt = view.findViewById(R.id.mobileTxt);
        sign_out_button = view.findViewById(R.id.sign_out_button);
        edit_profile = view.findViewById(R.id.edit_profile);
        sign_out_button.setVisibility(View.GONE);

        new GetUserDetails().execute(new ApiConnector());

        swipe_refresh_layout = view.findViewById(R.id.swipe_refresh_layout);

        swipe_refresh_layout.setRefreshing(true);

        swipe_refresh_layout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        new GetUserDetails().execute(new ApiConnector());
                    }
                }
        );

        sign_out_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure you want to sign out ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //take new user to registration
                                goToSplash();
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
        });

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToEditProfile();
            }
        });

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private class GetUserDetails extends AsyncTask<ApiConnector, Long, JSONArray> {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].GetUserDetails(SEmail);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            try {
                JSONObject item = jsonArray.getJSONObject(0);

                String email = item.getString("email");
                String path = item.getString("passport");
                String firstname = item.getString("first_name");
                String lastname = item.getString("last_name");
                String mobile = item.getString("mobile");

                Glide
                        .with(getActivity())
                        .load(Constants.BASE_URL2 + "passport/profiles/" + path)
                        .error(R.drawable.non_image)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .crossFade()
                        .into(profile_pic);

                userTxt.setText(firstname + " " + lastname);
                emailTxt.setText(email);
                mobileTxt.setText(mobile);

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.EMAIL2, email);
                editor.putString(Constants.FIRSTNAME, firstname);
                editor.putString(Constants.LASTNAME, lastname);
                editor.putString(Constants.MOBILE, mobile);
                editor.putString(Constants.PATH, path);
                editor.apply();

                swipe_refresh_layout.setRefreshing(false);

                sign_out_button.setVisibility(View.VISIBLE);

                if(Objects.equals(path, "francis.jpg")){
                    updatePassportName();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void goToSplash() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.EMAIL2, "");
        editor.putString(Constants.FIRSTNAME, "");
        editor.putString(Constants.LASTNAME, "");
        editor.putString(Constants.MOBILE, "");
        editor.putString(Constants.PATH, "");
        editor.putString(Constants.IS_LOGGED_IN, Constants.IS_LOGGED_IN_NO);
        editor.apply();

        Intent intent = new Intent(getActivity(), SplashActivity.class);
        getActivity().startActivity(intent);
        getActivity().finish();
    }

    private void goToEditProfile() {
        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
        getActivity().startActivity(intent);
    }

    private void updatePassportName() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.EDIT_PASSPORT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (s.equals("Successful")) {
                            RotateAnimation anim = new RotateAnimation(0f, 360f, 150f, 150f);
                            anim.setInterpolator(new LinearInterpolator());
                            anim.setDuration(600);
                            profile_pic.startAnimation(anim);
                        } else {
                            updatePassportName();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        try{
                            updatePassportName();
                        }catch (Exception e){
                          e.printStackTrace();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();
                //Adding parameters
                params.put("email", SEmail);

                //returning parameters
                return params;
            }
        };
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        //Adding request to the queue
        requestQueue.add(stringRequest);
    }
}
