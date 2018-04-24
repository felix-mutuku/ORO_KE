package com.propertiesKE;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FilterAdsFragment extends Fragment {
    private GridView GetAllAdsListView;
    private JSONArray jsonArray;
    String SPrice, SCounty;
    int SPid;
    Spinner category_spinner, county_spinner, price_spinner;
    SwipeRefreshLayout swipe_refresh_layout;
    ImageView available;
    Button b_filter;

    public FilterAdsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filter_ads, container, false);

        GetAllAdsListView = view.findViewById(R.id.GetAdsListView);

        category_spinner = view.findViewById(R.id.category_spinner);
        county_spinner = view.findViewById(R.id.county_spinner);
        price_spinner = view.findViewById(R.id.price_spinner);
        available = view.findViewById(R.id.available);
        swipe_refresh_layout = view.findViewById(R.id.swipe_refresh_layout);
        b_filter = view.findViewById(R.id.b_filter);

        b_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedCategory = category_spinner.getSelectedItem().toString();
                switch (selectedCategory) {
                    case "Land":
                        SPid = 1;
                        break;
                    case "Plot":
                        SPid = 2;
                        break;
                    case "Commercial Building":
                        SPid = 3;
                        break;
                    case "Godown":
                        SPid = 4;
                        break;
                    case "Shop":
                        SPid = 5;
                        break;
                    case "Office":
                        SPid = 6;
                        break;
                    case "Studio":
                        SPid = 7;
                        break;
                    case "House":
                        SPid = 8;
                        break;
                    case "Villa":
                        SPid = 9;
                        break;
                    case "Apartment":
                        SPid = 10;
                        break;
                    case "Bungalow":
                        SPid = 11;
                        break;
                    case "Maisonette":
                        SPid = 12;
                        break;
                    case "Short Term Rental":
                        SPid = 13;
                        break;
                    case "Holiday Rental":
                        SPid = 14;
                        break;
                }

                String selectedCounty = county_spinner.getSelectedItem().toString();
                SCounty = selectedCounty.replace(" ","_");

                String selectedPrice = price_spinner.getSelectedItem().toString();
                switch (selectedPrice) {
                    case "High to Low":
                        SPrice = "DESC";
                        break;
                    case "Low to High":
                        SPrice = "ASC";
                        break;
                }

                swipe_refresh_layout.setRefreshing(true);
                swipe_refresh_layout.setVisibility(View.VISIBLE);

                new GetAllAdsTask().execute(new ApiConnector());
            }
        });


        swipe_refresh_layout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        new GetAllAdsTask().execute(new ApiConnector());
                    }
                }
        );

        GetAllAdsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    //Get the customer which was clicked
                    JSONObject itemClicked = jsonArray.getJSONObject(position);
                    //Send Customer ID
                    Intent showDetails = new Intent(getActivity().getApplicationContext(), AdDetailsActivity.class);
                    showDetails.putExtra("item", itemClicked.getInt("aid"));
                    startActivity(showDetails);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    public void setListAdapter(JSONArray jsonArray) {
        try {
            this.jsonArray = jsonArray;
            this.GetAllAdsListView.setAdapter(new GetAdsListViewAdapter(jsonArray, getActivity()));
            if(jsonArray == null){
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
                //it is executed on Background thread
                return params[0].GetFilterAds(SPid, SPrice, SCounty);
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
}
