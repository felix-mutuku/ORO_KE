package com.propertiesKE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.VIBRATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SplashActivity extends AppCompatActivity {
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private static final int SPLASH_DURATION = 700; //0.7 second
    ConnectivityManager cManager;
    NetworkInfo nInfo;
    private boolean backbtnPress;
    public static final String DEFAULT = "N/A";
    public static final int RequestPermissionCode = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        cManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        assert cManager != null;
        nInfo = cManager.getActiveNetworkInfo();

        Handler myHandler = new Handler();
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!backbtnPress) {
                    if (nInfo != null && nInfo.isConnected()) {
                        if (CheckingPermissionIsEnabledOrNot()) {
                            goToNext();
                        } else {
                            RequestMultiplePermission();
                        }
                    } else {
                        Intent intent = new Intent(SplashActivity.this, InternetActivity.class);
                        SplashActivity.this.startActivity(intent);
                        finish();
                    }
                }
            }
        }, SPLASH_DURATION);
    }

    private void goToNext() {
        Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
        SplashActivity.this.startActivity(intent);
        finish();
    }

    //Permission function starts from here
    private void RequestMultiplePermission() {
        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(SplashActivity.this, new String[]
                {
                        INTERNET,
                        ACCESS_NETWORK_STATE,
                        WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE,
                        ACCESS_COARSE_LOCATION,
                        ACCESS_FINE_LOCATION,
                        VIBRATE,
                        CALL_PHONE,
                        SEND_SMS
                }, RequestPermissionCode);

    }

    // Calling override method.
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case RequestPermissionCode:

                if (grantResults.length > 0) {

                    boolean InternetPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean NetworkStatePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean WriteStoragePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadStoragePermission = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean CourseLocationPermission = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    boolean FineLocationPermission = grantResults[5] == PackageManager.PERMISSION_GRANTED;
                    boolean VibratePermission = grantResults[6] == PackageManager.PERMISSION_GRANTED;
                    boolean CallPermission = grantResults[7] == PackageManager.PERMISSION_GRANTED;
                    boolean SmsPermission = grantResults[8] == PackageManager.PERMISSION_GRANTED;

                    if (InternetPermission &&
                            NetworkStatePermission &&
                            WriteStoragePermission &&
                            ReadStoragePermission &&
                            CourseLocationPermission &&
                            FineLocationPermission &&
                            VibratePermission &&
                            CallPermission &&
                            SmsPermission) {
                        Toast toast = Toast.makeText(SplashActivity.this, "All permissions granted :)", Toast.LENGTH_LONG);
                        View toastView = toast.getView(); //This'll return the default View of the Toast.
                        TextView toastMessage = toastView.findViewById(android.R.id.message);
                        toastMessage.setTextSize(12);
                        toastMessage.setTextColor(getResources().getColor(R.color.white));
                        toastMessage.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_launcher, 0, 0, 0);
                        toastMessage.setGravity(Gravity.CENTER);
                        toastMessage.setCompoundDrawablePadding(10);
                        toastView.setBackground(getResources().getDrawable(R.drawable.bg_button2));
                        toast.show();

                        goToNext();
                    } else {
                        Toast toast = Toast.makeText(SplashActivity.this, "Permissions Denied!!\nPermissions need to be granted to use the app.", Toast.LENGTH_LONG);
                        View toastView = toast.getView(); //This'll return the default View of the Toast.
                        TextView toastMessage = toastView.findViewById(android.R.id.message);
                        toastMessage.setTextSize(12);
                        toastMessage.setTextColor(getResources().getColor(R.color.white));
                        toastMessage.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_launcher, 0, 0, 0);
                        toastMessage.setGravity(Gravity.CENTER);
                        toastMessage.setCompoundDrawablePadding(10);
                        toastView.setBackground(getResources().getDrawable(R.drawable.bg_button2));
                        toast.show();

                        finish();
                    }
                }

                break;
        }
    }

    // Checking permission is enabled or not using function starts from here.
    public boolean CheckingPermissionIsEnabledOrNot() {
        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), INTERNET);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_NETWORK_STATE);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int ForthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int FifthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);
        int SixthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int SeventhPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), VIBRATE);
        int EighthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);
        int NinethPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ForthPermissionResult == PackageManager.PERMISSION_GRANTED &&
                FifthPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SixthPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SeventhPermissionResult == PackageManager.PERMISSION_GRANTED &&
                EighthPermissionResult == PackageManager.PERMISSION_GRANTED &&
                NinethPermissionResult == PackageManager.PERMISSION_GRANTED;
    }
}
