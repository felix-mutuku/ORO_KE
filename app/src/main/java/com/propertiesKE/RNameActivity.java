package com.propertiesKE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class RNameActivity extends AppCompatActivity {
    ImageView back, next;
    EditText first_name, last_name;
    String SFirst, SLast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rname);

        back = findViewById(R.id.back);
        next = findViewById(R.id.next);
        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);

        ImageView backgrnd = findViewById(R.id.backgrnd);
        Glide
                .with(RNameActivity.this)
                .load(Constants.BASE_URL + "backgrounds/01.jpg")
                .crossFade()
                .into(backgrnd);

        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                char[] chars = {' ', '\'', '"'};
                for (int i = start; i < end; i++) {
                    if (new String(chars).contains(String.valueOf(source.charAt(i)))) {
                        return "_";
                    }
                }
                return null;
            }
        };

        first_name.setFilters(new InputFilter[]{filter});
        last_name.setFilters(new InputFilter[]{filter});

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (Exception e) {
                    //catch something
                    //catch me outside
                    e.printStackTrace();
                }

                if (first_name.length() > 0) {
                    //name not empty
                    if (first_name.length() > 3) {
                        if (last_name.length() > 0) {
                            if (last_name.length() > 3) {
                                SFirst = first_name.getText().toString();
                                SLast = last_name.getText().toString();
                                goToNext();
                            } else {
                                //too short
                                first_name.setError("Name too short");
                            }
                        } else {
                            //is empty
                            last_name.setError("Please fill in your Name");
                        }
                    } else {
                        //too short
                        first_name.setError("Name too short");
                    }
                } else {
                    //is empty
                    first_name.setError("Please fill in your Name");
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

    private void goToNext() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.FIRSTNAME, SFirst);
        editor.putString(Constants.LASTNAME, SLast);
        editor.apply();

        Intent intent = new Intent(RNameActivity.this, REmailActivity.class);
        RNameActivity.this.startActivity(intent);
    }
}
