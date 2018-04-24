package com.propertiesKE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.relex.circleindicator.CircleIndicator;

public class PicsActivity extends AppCompatActivity {
    ImageView back;
    public static final String DEFAULT = "N/A";
    String SRandom;
    private ViewPager mPager;
    SwipeRefreshLayout swipe_refresh_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pics);

        back = findViewById(R.id.back);
        mPager = findViewById(R.id.slide_pager);
        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);
        swipe_refresh_layout.setProgressViewOffset(true, -2000, -2000);

        //getting information entered by user
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SRandom = sharedPreferences.getString(Constants.RANDOM, DEFAULT);

        new GetPics().execute(new ApiConnector());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                      @Override
                                                      public void onRefresh() {
                                                          onBackPressed();
                                                          overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                                                      }
                                                  }
        );
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

                        mPager.setAdapter(new MyAdapterPics(PicsActivity.this, list));
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

}
