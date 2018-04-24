package com.propertiesKE;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Hashtable;
import java.util.Map;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class ContactUsFragment extends Fragment {
    ConnectivityManager cManager;
    NetworkInfo nInfo;
    TextView countTxt, countTxt2;
    Button finished;
    EditText feedback, subject;
    ProgressBar progress;
    String SEmail, SFeedback, SMobile, SFirstname,SSubject;
    public static final String DEFAULT = "N/A";

    public ContactUsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);

        cManager = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
        assert cManager != null;
        nInfo = cManager.getActiveNetworkInfo();

        countTxt = view.findViewById(R.id.countTxt);
        countTxt2 = view.findViewById(R.id.countTxt2);
        finished = view.findViewById(R.id.finished);
        progress = view.findViewById(R.id.progress);
        progress.setVisibility(View.INVISIBLE);

        if (nInfo != null && nInfo.isConnected()) {
            //catch me outside
        } else {
            Intent intent = new Intent(getActivity(), InternetActivity.class);
            getActivity().startActivity(intent);
            getActivity().finish();
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SEmail = sharedPreferences.getString(Constants.EMAIL2, DEFAULT);
        SFirstname = sharedPreferences.getString(Constants.FIRSTNAME, DEFAULT);
        SMobile = sharedPreferences.getString(Constants.MOBILE, DEFAULT);

        feedback = view.findViewById(R.id.feedback);
        subject = view.findViewById(R.id.subject);
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                char[] chars = {'\'', '"'};
                for (int i = start; i < end; i++) {
                    if (new String(chars).contains(String.valueOf(source.charAt(i)))) {
                        return "_";
                    }
                }
                return null;
            }
        };

        feedback.setFilters(new InputFilter[]{filter});
        subject.setFilters(new InputFilter[]{filter});
        feedback.addTextChangedListener(mTextEditorWatcher);
        subject.addTextChangedListener(mTextEditorWatcher2);

        finished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (feedback.length() > 0 &&
                        feedback.length() < 251 &&
                        subject.length() > 0 &&
                        subject.length() < 71) {
                    //send feedback to server
                    finished.setVisibility(View.INVISIBLE);
                    progress.setVisibility(View.VISIBLE);
                    SFeedback = feedback.getText().toString();
                    SSubject = subject.getText().toString();
                    sendFeedback();
                } else if (feedback.length() > 250) {
                    feedback.setError("Exceeded limit");
                    subject.setError("Exceeded limit");
                } else {
                    feedback.setError("Please fill feedback");
                    subject.setError("Please fill subject");
                }
            }
        });

        return view;
    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            countTxt.setText("Limit is 250");
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a text view to the current length
            countTxt.setText(String.valueOf(s.length()));
        }

        public void afterTextChanged(Editable s) {
            if (s.length() > 250) {
                countTxt.setText("Exceeded the limit of");
            }
        }
    };

    private final TextWatcher mTextEditorWatcher2 = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            countTxt2.setText("Limit is 70");
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a text view to the current length
            countTxt2.setText(String.valueOf(s.length()));
        }

        public void afterTextChanged(Editable s) {
            if (s.length() > 70) {
                countTxt2.setText("Exceeded the limit of");
            }
        }
    };

    private void sendFeedback() {
        //delete the token of the user on the server
        //Showing the progress dialog
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.FEEDBACK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (s.equals("Successful")) {
                            progress.setVisibility(View.INVISIBLE);

                            Toast toast = Toast.makeText(getActivity(), " Thank you for your Feedback :)", Toast.LENGTH_LONG);
                            View toastView = toast.getView(); //This'll return the default View of the Toast.
                            TextView toastMessage = (TextView) toastView.findViewById(android.R.id.message);
                            toastMessage.setTextSize(12);
                            toastMessage.setTextColor(getResources().getColor(R.color.white));
                            toastMessage.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_launcher, 0, 0, 0);
                            toastMessage.setGravity(Gravity.CENTER);
                            toastMessage.setCompoundDrawablePadding(10);
                            toastView.setBackground(getResources().getDrawable(R.drawable.bg_button2));
                            toast.show();

                            goToMain();
                        } else {
                            progress.setVisibility(View.INVISIBLE);
                            finished.setVisibility(View.VISIBLE);
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("Sorry, an error occurred.\nTry again?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //take new user to registration
                                            sendFeedback();
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // User cancelled the dialog
                                            dialog.dismiss();
                                        }
                                    });
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        progress.setVisibility(View.INVISIBLE);
                        finished.setVisibility(View.VISIBLE);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Sorry, an error occurred.\nTry again?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //take new user to registration
                                        sendFeedback();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User cancelled the dialog
                                        dialog.dismiss();
                                    }
                                });
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put("email", SEmail);
                params.put("feedback", SFeedback.replaceAll("'", "''"));
                params.put("subject", SSubject.replaceAll("'", "''"));
                params.put("mobile", SMobile);
                params.put("firstname", SFirstname);
                params.put("REQUEST_METHOD", "POST");

                //returning parameters
                return params;
            }
        };
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void goToMain() {
        MainFragment fragment = new MainFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.Content, fragment);
        fragmentTransaction.commit();
    }
}
