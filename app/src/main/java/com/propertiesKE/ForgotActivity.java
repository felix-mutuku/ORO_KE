package com.propertiesKE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.basgeekball.awesomevalidation.AwesomeValidation;

import java.util.Objects;

import static com.basgeekball.awesomevalidation.ValidationStyle.UNDERLABEL;

public class ForgotActivity extends AppCompatActivity {
    ImageView back, next_mobile, next_verify, next_password, next_confirm_password, next_login;
    LinearLayout linear_mobile, linear_verify, linear_password, linear_confirm_password, linear_login;
    EditText mobile, verify, password, confirm_password;
    ProgressBar progress_mobile, progress_verify, progress_password, progress_confirm_password;
    private AwesomeValidation awesomeValidation;
    String SMobile, SVerify, SPassword, SConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        back = findViewById(R.id.back);

        next_mobile = findViewById(R.id.next_mobile);
        next_verify = findViewById(R.id.next_verify);
        next_password = findViewById(R.id.next_password);
        next_confirm_password = findViewById(R.id.next_confirm_password);
        next_login = findViewById(R.id.next_login);
        linear_mobile = findViewById(R.id.linear_mobile);
        linear_verify = findViewById(R.id.linear_verify);
        linear_password = findViewById(R.id.linear_password);
        linear_confirm_password = findViewById(R.id.linear_confirm_password);
        linear_login = findViewById(R.id.linear_login);
        mobile = findViewById(R.id.mobile);
        verify = findViewById(R.id.verify);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        progress_mobile = findViewById(R.id.progress_mobile);
        progress_verify = findViewById(R.id.progress_verify);
        progress_password = findViewById(R.id.progress_password);
        progress_confirm_password = findViewById(R.id.progress_confirm_password);

        linear_mobile.setVisibility(View.VISIBLE);

        awesomeValidation = new AwesomeValidation(UNDERLABEL);
        awesomeValidation.setContext(this);
        awesomeValidation.addValidation(this, R.id.mobile, Patterns.PHONE, R.string.invalid_number);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        next_mobile.setOnClickListener(new View.OnClickListener() {
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
                            progress_mobile.setVisibility(View.VISIBLE);
                            next_mobile.setVisibility(View.INVISIBLE);
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

        verify.setFilters(new InputFilter[]{filter});

        next_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (verify.length() > 0) {
                    if (verify.length() > 10) {
                        SVerify = verify.getText().toString();

                        progress_verify.setVisibility(View.VISIBLE);
                        next_verify.setVisibility(View.INVISIBLE);
                        new CheckVerifyCode().execute(new ApiConnector());
                    } else {
                        //name too short
                        verify.setError("Verification code too short");
                    }
                } else {
                    //name is empty
                    verify.setError("Please enter verification code");
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

        next_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (password.length() > 0) {
                    if (password.length() >= 4) {
                        SPassword = password.getText().toString();

                        progress_password.setVisibility(View.VISIBLE);
                        next_password.setVisibility(View.INVISIBLE);
                        goToConfirmPassword();
                    } else {
                        //too short
                        password.setError("Password too short !");
                    }
                } else {
                    //is empty
                    password.setError("Please enter new password");
                }
            }
        });

        next_confirm_password.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                try {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (confirm_password.length() > 0) {
                    if (confirm_password.length() >= 4) {
                        SConfirmPassword = confirm_password.getText().toString();

                        if (Objects.equals(SPassword, SConfirmPassword)) {
                            progress_confirm_password.setVisibility(View.VISIBLE);
                            next_confirm_password.setVisibility(View.INVISIBLE);
                            new UpdatePassword().execute(new ApiConnector());
                        } else {
                            progress_confirm_password.setVisibility(View.INVISIBLE);
                            next_confirm_password.setVisibility(View.VISIBLE);
                            confirm_password.setError("Passwords do not match !");
                        }

                    } else {
                        //name too short
                        confirm_password.setError("Password too short !");
                    }
                } else {
                    //name is empty
                    confirm_password.setError("Please enter new password");
                }
            }
        });

        next_login.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                goToRealLogin();
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
                    progress_mobile.setVisibility(View.INVISIBLE);
                    next_mobile.setVisibility(View.VISIBLE);
                    new SendVerifyCode().execute(new ApiConnector());
                } else {
                    progress_mobile.setVisibility(View.INVISIBLE);
                    next_mobile.setVisibility(View.VISIBLE);
                    mobile.setError("Phone number does not exist !");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class SendVerifyCode extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].SendVerifyCode(SMobile);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {
                if (Objects.equals(response, "Success")) {
                    progress_mobile.setVisibility(View.INVISIBLE);
                    next_mobile.setVisibility(View.VISIBLE);
                    goToVerify();
                } else {
                    progress_mobile.setVisibility(View.INVISIBLE);
                    next_mobile.setVisibility(View.VISIBLE);
                    mobile.setError("Error, Please Try Again");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class CheckVerifyCode extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].CheckVerifyCode(SMobile, SVerify);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {
                if (Objects.equals(response, "Exist")) {
                    progress_verify.setVisibility(View.INVISIBLE);
                    next_verify.setVisibility(View.VISIBLE);
                    goToNewPassword();
                } else {
                    progress_verify.setVisibility(View.INVISIBLE);
                    next_verify.setVisibility(View.VISIBLE);
                    verify.setError("Verification Code is invalid !");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class UpdatePassword extends AsyncTask<ApiConnector, Long, String> {
        @Override
        protected String doInBackground(ApiConnector... params) {
            //it is executed on Background thread
            return params[0].UpdatePassword(SMobile, SPassword);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String response) {
            try {
                if (Objects.equals(response, "Success")) {
                    progress_confirm_password.setVisibility(View.INVISIBLE);
                    next_confirm_password.setVisibility(View.VISIBLE);
                    goToLogin();
                } else {
                    progress_mobile.setVisibility(View.INVISIBLE);
                    next_mobile.setVisibility(View.VISIBLE);
                    mobile.setError("Error, Try Again Later.");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void goToVerify() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.MOBILE, SMobile);
        editor.apply();

        linear_verify.setVisibility(View.VISIBLE);
        linear_mobile.setVisibility(View.GONE);
    }

    private void goToNewPassword() {
        linear_verify.setVisibility(View.GONE);
        linear_mobile.setVisibility(View.GONE);
        linear_password.setVisibility(View.VISIBLE);
    }

    private void goToConfirmPassword() {
        linear_verify.setVisibility(View.GONE);
        linear_mobile.setVisibility(View.GONE);
        linear_password.setVisibility(View.GONE);
        linear_confirm_password.setVisibility(View.VISIBLE);
    }

    private void goToLogin() {
        linear_verify.setVisibility(View.GONE);
        linear_mobile.setVisibility(View.GONE);
        linear_password.setVisibility(View.GONE);
        linear_confirm_password.setVisibility(View.GONE);
        linear_login.setVisibility(View.VISIBLE);
    }

    private void goToRealLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PASSWORD, SPassword);
        editor.putString(Constants.MOBILE, SMobile);
        editor.apply();

        Intent intent = new Intent(ForgotActivity.this, LoginActivity.class);
        ForgotActivity.this.startActivity(intent);
        finish();
    }
}
