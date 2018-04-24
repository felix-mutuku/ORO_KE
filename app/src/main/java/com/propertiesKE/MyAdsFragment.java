package com.propertiesKE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyAdsFragment extends Fragment {
    private GridView GetAllAdsListView;
    private JSONArray jsonArray;
    String SEmail, SStatus;
    public static final String DEFAULT = "N/A";
    View active_view, expired_view, pending_view;
    TextView active, pending, expired;
    SwipeRefreshLayout swipe_refresh_layout;
    ImageView available;

    public MyAdsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_ads, container, false);

        GetAllAdsListView = view.findViewById(R.id.GetAdsListView);

        active_view = view.findViewById(R.id.active_view);
        expired_view = view.findViewById(R.id.expired_view);
        pending_view = view.findViewById(R.id.pending_view);
        available = view.findViewById(R.id.available);

        active = view.findViewById(R.id.active);
        pending = view.findViewById(R.id.pending);
        expired = view.findViewById(R.id.expired);

        swipe_refresh_layout = view.findViewById(R.id.swipe_refresh_layout);
        swipe_refresh_layout.setRefreshing(true);
        new CheckNumberActive().execute(new ApiConnector());

        swipe_refresh_layout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        new CheckNumberActive().execute(new ApiConnector());
                    }
                }
        );
        active_view.setVisibility(View.VISIBLE);
        expired_view.setVisibility(View.INVISIBLE);
        pending_view.setVisibility(View.INVISIBLE);

        //getting information entered by user
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SEmail = sharedPreferences.getString(Constants.EMAIL2, DEFAULT);

        GetAllAdsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    JSONObject itemClicked = jsonArray.getJSONObject(position);
                    Intent showDetails = new Intent(getActivity().getApplicationContext(), AdDetailsActivity.class);
                    showDetails.putExtra("item", itemClicked.getInt("aid"));
                    startActivity(showDetails);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SStatus = "active";
                active_view.setVisibility(View.VISIBLE);
                expired_view.setVisibility(View.INVISIBLE);
                pending_view.setVisibility(View.INVISIBLE);

                swipe_refresh_layout.setVisibility(View.VISIBLE);
                swipe_refresh_layout.setRefreshing(true);
                new GetAllAdsTask().execute(new ApiConnector());
            }
        });

        pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SStatus = "pending";
                active_view.setVisibility(View.INVISIBLE);
                expired_view.setVisibility(View.INVISIBLE);
                pending_view.setVisibility(View.VISIBLE);

                swipe_refresh_layout.setVisibility(View.VISIBLE);
                swipe_refresh_layout.setRefreshing(true);
                new GetAllAdsTask().execute(new ApiConnector());
            }
        });

        expired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SStatus = "expired";
                active_view.setVisibility(View.INVISIBLE);
                expired_view.setVisibility(View.VISIBLE);
                pending_view.setVisibility(View.INVISIBLE);

                swipe_refresh_layout.setVisibility(View.VISIBLE);
                swipe_refresh_layout.setRefreshing(true);
                new GetAllAdsTask().execute(new ApiConnector());
            }
        });

        return view;
    }

    public void setListAdapter(JSONArray jsonArray) {
        try {
            this.jsonArray = jsonArray;
            this.GetAllAdsListView.setAdapter(new GetAdsListViewAdapter(jsonArray, getActivity()));
            if (jsonArray == null) {
                available.setVisibility(View.VISIBLE);
                swipe_refresh_layout.setVisibility(View.GONE);
            }
            swipe_refresh_layout.setRefreshing(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetAllAdsTask extends AsyncTask<ApiConnector, Long, JSONArray> {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {
            try {
                swipe_refresh_layout.setVisibility(View.VISIBLE);
                // it is executed on Background thread
                return params[0].GetMyAds(SEmail, SStatus);
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

    @SuppressLint("StaticFieldLeak")
    private class CheckNumberActive extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            SStatus = "active";
            return params[0].CheckNumberMyAds(SEmail, SStatus);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {

                active.setText("Active Ads (" + response + ")");
                new CheckNumberExpired().execute(new ApiConnector());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class CheckNumberExpired extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            SStatus = "expired";
            return params[0].CheckNumberMyAds(SEmail, SStatus);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {
                expired.setText("Expired Ads (" + response + ")");
                new CheckNumberPending().execute(new ApiConnector());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class CheckNumberPending extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            SStatus = "pending";
            return params[0].CheckNumberMyAds(SEmail, SStatus);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {

                pending.setText("Pending Ads (" + response + ")");

                active.callOnClick();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
