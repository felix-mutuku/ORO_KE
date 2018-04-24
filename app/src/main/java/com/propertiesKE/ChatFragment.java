package com.propertiesKE;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
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
import android.widget.ListView;
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

import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

public class ChatFragment extends Fragment {
    TextView create_txt, available;
    private ListView GetAllAdsListView;
    private JSONArray jsonArray;
    SwipeRefreshLayout swipe_refresh_layout;
    public static final String DEFAULT = "N/A";
    String SEmail, SLogin, SEmailTo, SSubject, SAcode;
    Intent showDetails;

    public ChatFragment() {
        //Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        create_txt = view.findViewById(R.id.create_txt);
        GetAllAdsListView = view.findViewById(R.id.GetAdsListView);
        available = view.findViewById(R.id.available);
        swipe_refresh_layout = view.findViewById(R.id.swipe_refresh_layout);

        //getting information entered by user
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SEmail = sharedPreferences.getString(Constants.EMAIL2, DEFAULT);
        SLogin = sharedPreferences.getString(Constants.IS_LOGGED_IN, DEFAULT);

        if (Objects.equals(SLogin, Constants.IS_LOGGED_IN_YES)) {
            //if user has logged in
            new GetAllChatsTask().execute(new ApiConnector());

            swipe_refresh_layout.setRefreshing(true);
        } else {
            //user hasn't logged in
            available.setVisibility(View.VISIBLE);
            swipe_refresh_layout.setVisibility(View.GONE);
        }

        swipe_refresh_layout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        new GetAllChatsTask().execute(new ApiConnector());
                    }
                }
        );

        GetAllAdsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    // GEt the customer which was clicked
                    JSONObject itemClicked = jsonArray.getJSONObject(position);
                    UserDetails.chatWith = itemClicked.getString("from_name");
                    // Send Customer ID
                    showDetails = new Intent(getActivity().getApplicationContext(), ChatActivity.class);
                    showDetails.putExtra("email", itemClicked.getString("email_to"));//email of message receiver
                    showDetails.putExtra("acode", itemClicked.getString("acode"));//ad code for message
                    showDetails.putExtra("subject", itemClicked.getString("subject"));//ad subject for message
                    showDetails.putExtra("from", itemClicked.getString("email_from"));//email of message sender
                    //showDetails.putExtra("from_name", itemClicked.getInt("from_name"));
                    //startActivity(showDetails);

                    SEmailTo = itemClicked.getString("email_to");
                    SSubject = itemClicked.getString("subject");
                    SAcode = itemClicked.getString("acode");

                    updateStatus();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        create_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToNewMessage();
            }
        });

        return view;
    }

    private void goToNewMessage() {
        Intent intent = new Intent(getActivity(), NewMessageActivity.class);
        startActivity(intent);
    }

    public void setListAdapter(JSONArray jsonArray) {
        try {
            this.jsonArray = jsonArray;
            this.GetAllAdsListView.setAdapter(new GetChatsListViewAdapter(jsonArray, getActivity()));
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
    private class GetAllChatsTask extends AsyncTask<ApiConnector, Long, JSONArray> {
        @Override
        protected JSONArray doInBackground(ApiConnector... params) {
            try {
                swipe_refresh_layout.setVisibility(View.VISIBLE);
                //it is executed on Background thread
                return params[0].GetChats(SEmail);
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

    private void updateStatus() {
        //delete the token of the user on the server
        //Showing the progress dialog
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.UPDATE_STATUS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        //Toast.makeText(ChatActivity.this, s, Toast.LENGTH_LONG).show();

                        if (s.equals("Success")) {
                            //when successful
                            //play okay sound
                            final MediaPlayer errorSound = MediaPlayer.create(getActivity(), R.raw.click);
                            errorSound.start();

                            startActivity(showDetails);//start chat activity
                        } else {
                            Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();

                            final MediaPlayer errorSound = MediaPlayer.create(getActivity(), R.raw.error);
                            errorSound.start();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Volley error occurred
                        Toast.makeText(getActivity(), "Volley Error", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                Map<String, String> params = new Hashtable<>();

                //Adding parameters
                params.put("email_to", SEmailTo.replaceAll("'", "''"));// email of receiver
                params.put("subject", SSubject.replaceAll("'", "''"));//subject of message sent
                //params.put("message", SMessage.replaceAll("'", "''"));
                params.put("email_from", SEmail.replaceAll("'", "''"));//email of sender
                params.put("acode", SAcode);//ad code of message sent
                //params.put("from_name", SName.replaceAll("'", "''"));
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
}
