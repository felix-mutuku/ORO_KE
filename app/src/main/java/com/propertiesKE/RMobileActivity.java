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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.bumptech.glide.Glide;

import java.util.Objects;

import static com.basgeekball.awesomevalidation.ValidationStyle.COLORATION;
import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

public class RMobileActivity extends AppCompatActivity {
    ImageView back, next;
    EditText mobile;
    String SMobile;
    ProgressBar progress;
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rmobile);

        back = findViewById(R.id.back);
        next = findViewById(R.id.next);
        mobile = findViewById(R.id.mobile);
        progress = findViewById(R.id.progress);

        awesomeValidation = new AwesomeValidation(UNDERLABEL);
        awesomeValidation.setContext(this);
        awesomeValidation.addValidation(this, R.id.mobile, Patterns.PHONE, R.string.invalid_number);

        ImageView backgrnd = findViewById(R.id.backgrnd);
        Glide
                .with(RMobileActivity.this)
                .load(Constants.BASE_URL + "backgrounds/03.jpg")
                .crossFade()
                .into(backgrnd);

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
                    e.printStackTrace();
                }

                if (mobile.length() > 0) {
                    //name not empty
                    if (mobile.length() > 10) {
                        SMobile = mobile.getText().toString();
                        if (awesomeValidation.validate()) {
                            progress.setVisibility(View.VISIBLE);
                            next.setVisibility(View.INVISIBLE);
                            new CheckUser().execute(new ApiConnector());
                        }
                    } else {
                        //name too short
                        mobile.setError("Phone Number too short");
                    }
                } else {
                    //name is empty
                    mobile.setError("Please fill your Phone Number");
                }
            }
        });

        mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String enteredString = s.toString();
                if (enteredString.startsWith("0")) {
                    mobile.setText("254");
                    mobile.setSelection(mobile.getText().length());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //before typing
            }

            @Override
            public void afterTextChanged(Editable s) {
                //after typing
            }
        });

    }

    @SuppressLint("StaticFieldLeak")
    private class CheckUser extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].CheckMobile(SMobile);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {

                if (Objects.equals(response, "Exist")) {
                    progress.setVisibility(View.INVISIBLE);
                    next.setVisibility(View.VISIBLE);
                    mobile.setError("Phone Number already in use !");
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
        editor.putString(Constants.MOBILE, SMobile);
        editor.apply();

        Intent intent = new Intent(RMobileActivity.this, RPasswordActivity.class);
        RMobileActivity.this.startActivity(intent);
    }
}
