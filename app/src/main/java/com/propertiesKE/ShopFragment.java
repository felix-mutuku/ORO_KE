package com.propertiesKE;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShopFragment extends Fragment {
    private GridView GetAllAdsListView;
    private JSONArray jsonArray;
    SwipeRefreshLayout swipe_refresh_layout;
    ImageView available;

    public ShopFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shop, container, false);
        GetAllAdsListView = view.findViewById(R.id.GetAdsListView);

        new GetAllAdsTask().execute(new ApiConnector());
        available = view.findViewById(R.id.available);
        swipe_refresh_layout = view.findViewById(R.id.swipe_refresh_layout);

        swipe_refresh_layout.setRefreshing(true);

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
                    JSONObject itemClicked = jsonArray.getJSONObject(position);
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
                // it is executed on Background thread
                return params[0].GetTabAds(5);
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
