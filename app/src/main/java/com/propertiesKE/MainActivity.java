package com.propertiesKE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.VIBRATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ImageView menu_more, menu_nav, nav_profile, menu_search;
    DrawerLayout drawer;
    public static final String DEFAULT = "N/A";
    String SFirstname, SLastname, login, SEmail, SPath, SFullNames;
    TextView fullnames, create_txt, nav_sign_out_button;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FrameLayout Content;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawer = findViewById(R.id.drawer_layout);
        menu_more = findViewById(R.id.menu_more);
        menu_nav = findViewById(R.id.menu_nav);
        menu_search = findViewById(R.id.menu_search);
        Content = findViewById(R.id.Content);
        create_txt = findViewById(R.id.create_txt);

        Firebase.setAndroidContext(this);

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        //getting information entered by user
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SFirstname = sharedPreferences.getString(Constants.FIRSTNAME, DEFAULT);
        SLastname = sharedPreferences.getString(Constants.LASTNAME, DEFAULT);
        SEmail = sharedPreferences.getString(Constants.EMAIL2, DEFAULT);
        SPath = sharedPreferences.getString(Constants.PATH, DEFAULT);
        login = sharedPreferences.getString(Constants.IS_LOGGED_IN, DEFAULT);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);

        fullnames = header.findViewById(R.id.fullnames);
        nav_profile = header.findViewById(R.id.nav_profile);
        nav_sign_out_button = header.findViewById(R.id.nav_sign_out_button);

        navigationView.setNavigationItemSelectedListener(this);

        Menu nav_Menu = navigationView.getMenu();

        if (!Objects.equals(login, Constants.IS_LOGGED_IN_YES) ||
                Objects.equals(SFirstname, DEFAULT)) {
            //if user hasn't logged in
            nav_Menu.findItem(R.id.nav_profile).setVisible(false);
            fullnames.setText("Welcome to ORO");
            nav_sign_out_button.setText("Login");

        } else {
            nav_sign_out_button.setText("Sign Out");
            //if everything iss okay
            fullnames.setText(SFirstname + " " + SLastname);

            String ImageFull = Constants.BASE_URL2 + "passport/profiles/" + SPath;

            Glide
                    .with(MainActivity.this)
                    .load(ImageFull)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .crossFade()
                    .into(nav_profile);

            //login to fire base to enable continuous messaging
            firebaseLogin();

            startService(new Intent(this, BackgroundService.class));

        }

        if (!CheckingPermissionIsEnabledOrNot()) {
            //Calling method to enable permission.
            goToSplash();
        }

        menu_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.dialog_menu_more);

                ImageView dialog_about = dialog.findViewById(R.id.dialog_about);
                ImageView dialog_privacy = dialog.findViewById(R.id.dialog_privacy);
                ImageView dialog_terms = dialog.findViewById(R.id.dialog_terms);

                dialog_about.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                        startActivity(intent);
                    }
                });

                dialog_privacy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, PrivacyActivity.class);
                        startActivity(intent);
                    }
                });

                dialog_terms.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, TermsActivity.class);
                        startActivity(intent);
                    }
                });
                dialog.show();
            }
        });

        menu_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        menu_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        nav_sign_out_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Objects.equals(login, Constants.IS_LOGGED_IN_YES) ||
                        Objects.equals(SFirstname, DEFAULT)) {
                    //if user hasn't logged in
                    goToLogin();
                } else {
                    //if user id logged in
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Are you sure you want to sign out ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //take new user to registration
                                    goToSplashSignOut();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                    dialog.dismiss();
                                }
                            });
                    builder.show();
                }
            }
        });

        nav_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (login.equals(Constants.IS_LOGGED_IN_YES)) {
                    //user is logged in
                    goToProfile();
                } else {
                    //user is not logged in
                    goToLogin();
                }
            }
        });

        create_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (login.equals(Constants.IS_LOGGED_IN_YES)) {
                    goToProfileCreate();
                } else {
                    goToLogin();
                }
            }
        });

        clearContent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_about_us) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_privacy_policy) {
            Intent intent = new Intent(MainActivity.this, PrivacyActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_terms) {
            Intent intent = new Intent(MainActivity.this, TermsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_create_ad) {
            if (login.equals(Constants.IS_LOGGED_IN_YES)) {
                //user is logged in
                //Change Fragment
                goToProfileCreate();
            } else {
                //user is not logged in
                //Change Fragment
                goToLogin();
            }
        } else if (id == R.id.nav_filter_ads) {
            clearPager();

            FilterAdsFragment fragment = new FilterAdsFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.Content, fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_hot_categories) {
            clearPager();

            HotCategoryFragment fragment = new HotCategoryFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.Content, fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_profile) {
            if (login.equals(Constants.IS_LOGGED_IN_YES)) {
                //user is not logged in
                goToProfile();
            } else {
                //user is not logged in
                //Change Fragment
                goToLogin();
            }
        } else if (id == R.id.nav_contact_us) {
            clearPager();

            ContactUsFragment fragment = new ContactUsFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.Content, fragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_home) {
            clearContent();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void goToSplash() {
        Intent intent = new Intent(MainActivity.this, SplashActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToSplashSignOut() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.EMAIL2, "");
        editor.putString(Constants.FIRSTNAME, "");
        editor.putString(Constants.LASTNAME, "");
        editor.putString(Constants.MOBILE, "");
        editor.putString(Constants.PATH, "");
        editor.putString(Constants.IS_LOGGED_IN, Constants.IS_LOGGED_IN_NO);
        editor.apply();

        Intent intent = new Intent(MainActivity.this, SplashActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToProfile() {
        Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
        startActivity(intent);
    }

    private void goToProfileCreate() {
        Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
        intent.putExtra("fragmentCreate", "create");
        startActivity(intent);
    }

    private void goToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void clearContent() {
        Content.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
        create_txt.setVisibility(View.VISIBLE);
    }

    private void clearPager() {
        viewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        Content.setVisibility(View.VISIBLE);
        create_txt.setVisibility(View.GONE);
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

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new MainFragment(), "All");
        adapter.addFrag(new ApartmentFragment(), "Apartment");
        adapter.addFrag(new BungalowFragment(), "Bungalow");
        adapter.addFrag(new CommercialBuildingFragment(), "Commercial Building");
        adapter.addFrag(new GodownFragment(), "Go-Down");
        adapter.addFrag(new HolidayRentalFragment(), "Holiday Rental");
        adapter.addFrag(new HouseFragment(), "House");
        adapter.addFrag(new LandFragment(), "Land");
        adapter.addFrag(new MaisonetteFragment(), "Maisonette");
        adapter.addFrag(new OfficeFragment(), "Office");
        adapter.addFrag(new PlotFragment(), "Plot");
        adapter.addFrag(new ShopFragment(), "Shop");
        adapter.addFrag(new ShortTermFragment(), "Short-Term Rental");
        adapter.addFrag(new StudioFragment(), "Studio");
        adapter.addFrag(new VillaFragment(), "Villa");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

   /* @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }*/

    private void firebaseLogin() {
        String url = "https://orochat-ae438.firebaseio.com/users.json";

        SFullNames = SFirstname + " " + SLastname;

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (s.equals("null")) {
                    firebaseRegistration();
                } else {
                    try {
                        JSONObject obj = new JSONObject(s);
                        if (!obj.has(SFullNames)) {
                            firebaseRegistration();
                        } else if (obj.getJSONObject(SFullNames).getString("password").equals(SEmail)) {
                            UserDetails.username = SFullNames;
                            UserDetails.password = SEmail;

                            //play okay sound
                            final MediaPlayer errorSound = MediaPlayer.create(MainActivity.this, R.raw.click);
                            errorSound.start();

                        } else {
                            //play error sound
                            final MediaPlayer errorSound = MediaPlayer.create(MainActivity.this, R.raw.error);
                            errorSound.start();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(MainActivity.this);
        rQueue.add(request);
    }

    private void firebaseRegistration() {
        final ProgressDialog pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Getting things ready...");
        pd.show();

        String url = "https://orochat-ae438.firebaseio.com/users.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Firebase reference = new Firebase("https://orochat-ae438.firebaseio.com/users");

                if (s.equals("null")) {
                    reference.child(SFullNames).child("password").setValue(SEmail);
                    firebaseLogin();
                } else {
                    try {
                        JSONObject obj = new JSONObject(s);
                        if (!obj.has(SFullNames)) {
                            reference.child(SFullNames).child("password").setValue(SEmail);
                            firebaseLogin();
                        } else {
                            //Toast.makeText(MainActivity.this, "Name already exists :(", Toast.LENGTH_LONG).show();
                            reference.child(SFullNames).child("password").setValue(SEmail);
                            firebaseLogin();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                pd.dismiss();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError);
                pd.dismiss();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(MainActivity.this);
        rQueue.add(request);
    }
}
