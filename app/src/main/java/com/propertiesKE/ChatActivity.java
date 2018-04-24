package com.propertiesKE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton, back, profile_pic;
    EditText messageArea;
    TextView full_name, subject;
    ScrollView scrollView;
    Firebase reference1, reference2;
    String SEmail, SAcode, SSubject, SMessage, SFrom, SName;
    public static final String DEFAULT = "N/A";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        layout = findViewById(R.id.layout1);
        layout_2 = findViewById(R.id.layout2);
        sendButton = findViewById(R.id.sendButton);
        messageArea = findViewById(R.id.messageArea);
        scrollView = findViewById(R.id.scrollView);
        back = findViewById(R.id.back);
        profile_pic = findViewById(R.id.profile_pic);
        full_name = findViewById(R.id.full_name);
        subject = findViewById(R.id.subject);

        //get activity information from previous activity
        SEmail = getIntent().getStringExtra("email");//email of receiver of message
        SAcode = getIntent().getStringExtra("acode"); //ad code
        SSubject = getIntent().getStringExtra("subject"); //ad subject
        SFrom = getIntent().getStringExtra("from"); //email of sender of message

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String firstname = sharedPreferences.getString(Constants.FIRSTNAME, DEFAULT);
        String lastname = sharedPreferences.getString(Constants.LASTNAME, DEFAULT);
        SName = firstname + " " + lastname;
        UserDetails.username = SName;

        if (!Objects.equals(SEmail, null) && !Objects.equals(SSubject, null)) {
            new GetUserDetails().execute(new ApiConnector());
            subject.setText(SSubject);
        } else {
            profile_pic.setVisibility(View.GONE);
            subject.setVisibility(View.GONE);
        }

        full_name.setText(UserDetails.chatWith);

        Firebase.setAndroidContext(this);
        reference1 = new Firebase("https://orochat-ae438.firebaseio.com/messages/" + UserDetails.username + "_" + UserDetails.chatWith);
        reference2 = new Firebase("https://orochat-ae438.firebaseio.com/messages/" + UserDetails.chatWith + "_" + UserDetails.username);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();
                SMessage = messageText;

                if (!messageText.equals("")) {
                    Map<String, String> map = new HashMap<>();
                    map.put("message", messageText);
                    map.put("user", UserDetails.username);
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    messageArea.setText("");

                    //play okay sound
                    //final MediaPlayer errorSound = MediaPlayer.create(ChatActivity.this, R.raw.sent);
                    //errorSound.start();

                    updateDB();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String userName = map.get("user").toString();

                if (userName.equals(UserDetails.username)) {
                    addMessageBox("You:-\n" + message, 1);
                } else {
                    addMessageBox(UserDetails.chatWith + ":-\n" + message, 2);

                    //notification at this point with immediate chat person.
                    /**Intent intent = new Intent(ChatActivity.this, ChatActivity.class);
                     intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                     PendingIntent pendingIntent = PendingIntent.getActivity(ChatActivity.this, 0, intent, 0);

                     NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ChatActivity.this)
                     .setSmallIcon(R.drawable.ic_stat_name)
                     .setContentTitle("New message from " + UserDetails.chatWith)
                     .setContentText(message).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                     .setStyle(new NotificationCompat.BigTextStyle()
                     .bigText(message))
                     .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                     .setContentIntent(pendingIntent)
                     .setAutoCancel(true);

                     NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ChatActivity.this);
                     //notificationId is a unique int for each notification that you must define
                     notificationManager.notify(1, mBuilder.build());**/

                    //play notification sound
                    //final MediaPlayer errorSound = MediaPlayer.create(ChatActivity.this, R.raw.notification);
                    //errorSound.start();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void addMessageBox(String message, int type) {
        TextView textView = new TextView(ChatActivity.this);
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if (type == 1) {
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.bubble_in);
        } else {
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.bubble_out);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
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

                String path = item.getString("passport");

                Glide
                        .with(ChatActivity.this)
                        .load(Constants.BASE_URL2 + "passport/profiles/" + path)
                        .error(R.drawable.non_image)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .crossFade()
                        .into(profile_pic);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateDB() {
        //delete the token of the user on the server
        //Showing the progress dialog
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.UPDATE_DB,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        //Toast.makeText(ChatActivity.this, s, Toast.LENGTH_LONG).show();

                        if (s.equals("Success")) {
                            //when successful
                            //play okay sound
                            final MediaPlayer errorSound = MediaPlayer.create(ChatActivity.this, R.raw.sent);
                            errorSound.start();
                        } else {
                            Toast.makeText(ChatActivity.this, s, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Volley error occurred
                        Toast.makeText(ChatActivity.this, "Volley Error", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                Map<String, String> params = new Hashtable<>();

                //Adding parameters
                params.put("email", SEmail.replaceAll("'", "''"));
                params.put("subject", SSubject.replaceAll("'", "''"));
                params.put("message", SMessage.replaceAll("'", "''"));
                params.put("from", SFrom.replaceAll("'", "''"));
                params.put("acode", SAcode);
                params.put("from_name", SName.replaceAll("'", "''"));
                //params.put("REQUEST_METHOD", "POST");

                //returning parameters
                return params;
            }
        };
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(ChatActivity.this);
        //Adding request to the queue
        requestQueue.add(stringRequest);
    }
}
