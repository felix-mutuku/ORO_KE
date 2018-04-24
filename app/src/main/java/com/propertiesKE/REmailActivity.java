package com.propertiesKE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.bumptech.glide.Glide;

import java.util.Objects;

import static com.basgeekball.awesomevalidation.ValidationStyle.COLORATION;
import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

public class REmailActivity extends AppCompatActivity {
    ImageView back, next;
    EditText email;
    String SEmail;
    ProgressBar progress;
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remail);

        ImageView backgrnd = findViewById(R.id.backgrnd);
        Glide
                .with(REmailActivity.this)
                .load(Constants.BASE_URL + "backgrounds/02.jpg")
                .crossFade()
                .into(backgrnd);

        back = findViewById(R.id.back);
        next = findViewById(R.id.next);
        email = findViewById(R.id.email);
        progress = findViewById(R.id.progress);

        awesomeValidation = new AwesomeValidation(UNDERLABEL);
        awesomeValidation.setContext(this);

        awesomeValidation.addValidation(this, R.id.email, Patterns.EMAIL_ADDRESS, R.string.invalid_mail);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (Exception e) {
                    //catch something
                    //catch me outside
                }

                if (email.length() > 0) {
                    //name not empty
                    if (email.length() > 5) {
                        SEmail = email.getText().toString();

                        if (awesomeValidation.validate()) {
                            progress.setVisibility(View.VISIBLE);
                            next.setVisibility(View.INVISIBLE);

                            new CheckUser().execute(new ApiConnector());
                        }
                    } else {
                        //name too short
                        email.setError("Email too short");
                    }
                } else {
                    //name is empty
                    email.setError("Please fill your email");
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class CheckUser extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].CheckEmail(SEmail);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {

                if (Objects.equals(response, "Exist")) {
                    progress.setVisibility(View.INVISIBLE);
                    next.setVisibility(View.VISIBLE);
                    email.setError("Email address already in use !");
                } else {
                    progress.setVisibility(View.INVISIBLE);
                    next.setVisibility(View.VISIBLE);
                    goToNext();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void goToNext() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.EMAIL, SEmail);
        editor.apply();

        Intent intent = new Intent(REmailActivity.this, RMobileActivity.class);
        REmailActivity.this.startActivity(intent);
    }

}
