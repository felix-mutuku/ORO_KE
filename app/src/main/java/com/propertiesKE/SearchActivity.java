package com.propertiesKE;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class SearchActivity extends AppCompatActivity {
    SearchView search_bar;
    SwipeRefreshLayout swipe_refresh_layout;
    ImageView available, back;
    TextView enter_text;
    private GridView GetAllAdsListView;
    private JSONArray jsonArray;
    String SSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        GetAllAdsListView = findViewById(R.id.GetAdsListView);
        search_bar = findViewById(R.id.search_bar);
        available = findViewById(R.id.available);
        back = findViewById(R.id.back);
        enter_text = findViewById(R.id.enter_text);
        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);

        search_bar.onActionViewExpanded();

        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onRefresh() {
                        if (!Objects.equals(search_bar.getQuery().toString(), "")) {
                            new GetAllAdsTask().execute(new ApiConnector());
                            swipe_refresh_layout.setRefreshing(true);
                            enter_text.setVisibility(View.INVISIBLE);
                        }
                    }
                }
        );

        search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Toast.makeText(SearchActivity.this, query, Toast.LENGTH_SHORT).show();
                swipe_refresh_layout.setVisibility(View.VISIBLE);
                SSearch = query;
                new GetAllAdsTask().execute(new ApiConnector());
                swipe_refresh_layout.setRefreshing(true);

                try {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                enter_text.setVisibility(View.GONE);

                return false;
            }
        });

        GetAllAdsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    JSONObject itemClicked = jsonArray.getJSONObject(position);
                    Intent showDetails = new Intent(getApplicationContext(), AdDetailsActivity.class);
                    showDetails.putExtra("item", itemClicked.getInt("aid"));
                    startActivity(showDetails);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void setListAdapter(JSONArray jsonArray) {
        try {
            this.jsonArray = jsonArray;
            this.GetAllAdsListView.setAdapter(new GetAdsListViewAdapter(jsonArray, this));
            if (jsonArray == null) {
                available.setVisibility(View.VISIBLE);
                swipe_refresh_layout.setVisibility(View.GONE);
                enter_text.setVisibility(View.VISIBLE);
            }
            swipe_refresh_layout.setRefreshing(false);
            enter_text.setVisibility(View.GONE);
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
                return params[0].GetSearchAds(SSearch);
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
