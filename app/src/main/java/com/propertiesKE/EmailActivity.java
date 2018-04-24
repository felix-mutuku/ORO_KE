package com.propertiesKE;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
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
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

public class EmailActivity extends AppCompatActivity {
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton, back, profile_pic;
    EditText messageArea, guest_email;
    TextView full_name, subject;
    ScrollView scrollView;
    String SEmail, SSubject, SMessage, SAcode, SFrom;
    public static final String DEFAULT = "N/A";
    private AwesomeValidation awesomeValidation;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        layout = findViewById(R.id.layout1);
        layout_2 = findViewById(R.id.layout2);
        sendButton = findViewById(R.id.sendButton);
        messageArea = findViewById(R.id.messageArea);
        scrollView = findViewById(R.id.scrollView);
        back = findViewById(R.id.back);
        profile_pic = findViewById(R.id.profile_pic);
        full_name = findViewById(R.id.full_name);
        subject = findViewById(R.id.subject);
        guest_email = findViewById(R.id.guest_email);

        //get information sent from previous activity
        SEmail = getIntent().getStringExtra("email");
        SSubject = getIntent().getStringExtra("subject");
        SAcode = getIntent().getStringExtra("acode");

        awesomeValidation = new AwesomeValidation(UNDERLABEL);
        awesomeValidation.setContext(this);

        awesomeValidation.addValidation(this, R.id.email, Patterns.EMAIL_ADDRESS, R.string.invalid_mail);

        if (!Objects.equals(SEmail, null)) {
            new GetUserDetails().execute(new ApiConnector());
        } else {
            profile_pic.setVisibility(View.GONE);
        }

        full_name.setText(UserDetails.chatWith);
        subject.setText(SSubject);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SMessage = messageArea.getText().toString();

                if (!SMessage.equals("")) {
                    if (guest_email.length() > 0) {
                        //not empty
                        if (guest_email.length() > 5) {
                            if (awesomeValidation.validate()) {
                                SFrom = guest_email.getText().toString();

                                addMessageBox("You:-\n" + SMessage, 1);
                                addMessageBox("ORO:-\n" + "Message sent as Email.", 1);

                                sendEmail();//send email to user
                            }
                        } else {
                            //too short
                            guest_email.setError("Email too short");
                        }
                    } else {
                        // is empty
                        guest_email.setError("Please fill your email");
                    }
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void addMessageBox(String message, int type) {
        TextView textView = new TextView(EmailActivity.this);
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
                        .with(EmailActivity.this)
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

    private void sendEmail() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.SEND_EMAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        //Toast.makeText(EmailActivity.this, s, Toast.LENGTH_LONG).show();
                        //messageArea.setText(s);

                        if (s.equals("Success")) {
                            //when successful
                            //play okay sound
                            final MediaPlayer errorSound = MediaPlayer.create(EmailActivity.this, R.raw.sent);
                            errorSound.start();
                        } else {
                            //view error message
                            Toast.makeText(EmailActivity.this, s, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Volley error occurred
                        Toast.makeText(EmailActivity.this, "Volley Error", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                Map<String, String> params = new Hashtable<>();

                //Adding parameters
                params.put("email", SEmail);
                params.put("subject", SSubject.replaceAll("'", "''"));
                params.put("message", SMessage.replaceAll("'", "''"));
                params.put("from", SFrom);
                params.put("acode", SAcode);
                //params.put("REQUEST_METHOD", "POST");

                //returning parameters
                return params;
            }
        };
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(EmailActivity.this);
        //Adding request to the queue
        requestQueue.add(stringRequest);
    }
}
