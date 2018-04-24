package com.propertiesKE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class RPasswordActivity extends AppCompatActivity {
    ImageView back, next;
    EditText password;
    String SPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rpassword);

        back = findViewById(R.id.back);
        next = findViewById(R.id.next);
        password = findViewById(R.id.password);

        ImageView backgrnd = findViewById(R.id.backgrnd);
        Glide
                .with(RPasswordActivity.this)
                .load(Constants.BASE_URL + "backgrounds/04.jpg")
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
                    //catch something
                    //catch me outside
                }

                if (password.length() > 0) {
                    //not empty
                    if (password.length() >= 4) {
                        SPassword = password.getText().toString();
                        goToNext();
                    } else {
                        //too short
                        password.setError("Password too short");
                    }
                } else {
                    //is empty
                    password.setError("Please enter Password");
                }
            }
        });
    }

    private void goToNext() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PASSWORD, SPassword);
        editor.apply();

        Intent intent = new Intent(RPasswordActivity.this, RegisterActivity.class);
        RPasswordActivity.this.startActivity(intent);
    }
}
