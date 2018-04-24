package com.propertiesKE;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

import static com.basgeekball.awesomevalidation.ValidationStyle.COLORATION;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    Button login, register;
    TextView terms, forgot_password;
    ProgressBar progress;
    private AwesomeValidation awesomeValidation;
    private String KEY_EMAIL = "email";
    private String KEY_PASSWORD = "password";
    String SEmail, SPassword, SEmail2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        terms = findViewById(R.id.terms);
        forgot_password = findViewById(R.id.forgot_password);
        progress = findViewById(R.id.progress);

        awesomeValidation = new AwesomeValidation(COLORATION);
        awesomeValidation.setColor(Color.RED);
        awesomeValidation.addValidation(LoginActivity.this, R.id.email, Patterns.EMAIL_ADDRESS, R.string.invalid_mail);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                if (email.length() > 0) {
                    //not empty
                    if (email.length() > 5) {
                        if (awesomeValidation.validate()) {
                            if (password.length() > 0) {
                                //not empty
                                if (password.length() >= 4) {
                                    SEmail = email.getText().toString();
                                    SPassword = password.getText().toString();
                                    progress.setVisibility(View.VISIBLE);
                                    login.setVisibility(View.INVISIBLE);

                                    new loginUser().execute(new ApiConnector());
                                } else {
                                    //too short
                                    password.setError("Password too short");
                                }
                            } else {
                                //is empty
                                password.setError("Please enter Password");
                            }

                        }
                    } else {
                        //too short
                        email.setError("Email too short");
                    }
                } else {
                    //is empty
                    email.setError("Please fill your email");
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegister();
            }
        });

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToTerms();
            }
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToForgot();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class loginUser extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].LoginUser(SEmail, SPassword);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this);

                if (response.equals("Exist")) {
                    progress.setVisibility(View.INVISIBLE);

                    goToSplash();

                } else if (response.equals("Non")) {
                    progress.setVisibility(View.INVISIBLE);
                    login.setVisibility(View.VISIBLE);
                    Toast toast = Toast.makeText(LoginActivity.this, "Login Credentials Incorrect :(", Toast.LENGTH_LONG);
                    View toastView = toast.getView(); //This'll return the default View of the Toast.
                    TextView toastMessage = toastView.findViewById(android.R.id.message);
                    toastMessage.setTextSize(12);
                    toastMessage.setTextColor(getResources().getColor(R.color.white));
                    toastMessage.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_launcher, 0, 0, 0);
                    toastMessage.setGravity(Gravity.CENTER);
                    toastMessage.setCompoundDrawablePadding(10);
                    toastView.setBackground(getResources().getDrawable(R.drawable.bg_button2));
                    toast.show();
                } else {
                    progress.setVisibility(View.INVISIBLE);
                    login.setVisibility(View.VISIBLE);

                    builder.setMessage("Sorry, an error occurred.\nTry again?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //take new user to registration
                                    new loginUser().execute(new ApiConnector());
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                    dialog.dismiss();
                                }
                            });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void goToTerms() {
        Intent intent = new Intent(LoginActivity.this, TermsActivity.class);
        LoginActivity.this.startActivity(intent);
    }

    private void goToForgot() {
        Intent intent = new Intent(LoginActivity.this, ForgotActivity.class);
        LoginActivity.this.startActivity(intent);
    }

    private void goToRegister() {
        Intent intent = new Intent(LoginActivity.this, RNameActivity.class);
        LoginActivity.this.startActivity(intent);
    }

    private void goToSplash() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.EMAIL2, SEmail);
        editor.putString(Constants.PASSWORD, SPassword);
        editor.putString(Constants.IS_LOGGED_IN, Constants.IS_LOGGED_IN_YES);
        editor.apply();

        Toast toast = Toast.makeText(LoginActivity.this, "Welcome :)", Toast.LENGTH_LONG);
        View toastView = toast.getView(); //This'll return the default View of the Toast.
        TextView toastMessage = toastView.findViewById(android.R.id.message);
        toastMessage.setTextSize(12);
        toastMessage.setTextColor(getResources().getColor(R.color.white));
        toastMessage.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_launcher, 0, 0, 0);
        toastMessage.setGravity(Gravity.CENTER);
        toastMessage.setCompoundDrawablePadding(10);
        toastView.setBackground(getResources().getDrawable(R.drawable.bg_button2));
        toast.show();

        Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
        LoginActivity.this.startActivity(intent);
        finish();
    }
}

