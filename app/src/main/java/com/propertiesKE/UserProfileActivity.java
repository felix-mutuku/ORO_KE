package com.propertiesKE;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity {
    ImageView back;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_chat:
                    ChatFragment fragment4 = new ChatFragment();
                    android.support.v4.app.FragmentTransaction fragmentTransaction4 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction4.replace(R.id.Content, fragment4);
                    fragmentTransaction4.commit();
                    return true;

                case R.id.navigation_create:
                    CreateAdFragment fragment = new CreateAdFragment();
                    android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.Content, fragment);
                    fragmentTransaction.commit();
                    return true;

                case R.id.navigation_my_ads:
                    MyAdsFragment fragment2 = new MyAdsFragment();
                    android.support.v4.app.FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction2.replace(R.id.Content, fragment2);
                    fragmentTransaction2.commit();
                    return true;

                case R.id.navigation_profile:
                    ProfileFragment fragment3 = new ProfileFragment();
                    android.support.v4.app.FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction3.replace(R.id.Content, fragment3);
                    fragmentTransaction3.commit();
                    return true;

            }
            return false;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        back = findViewById(R.id.back);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        String create = getIntent().getStringExtra("fragmentCreate");

        if (Objects.equals(create, "create")) {
            CreateAdFragment fragment = new CreateAdFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.Content, fragment);
            fragmentTransaction.commit();

        } else if (Objects.equals(create, "chat")) {
            ChatFragment fragment4 = new ChatFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction4 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction4.replace(R.id.Content, fragment4);
            fragmentTransaction4.commit();

        } else if (Objects.equals(create, "ads")){
            MyAdsFragment fragment = new MyAdsFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.Content, fragment);
            fragmentTransaction.commit();

        }else {
            ProfileFragment fragment = new ProfileFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.Content, fragment);
            fragmentTransaction.commit();

        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
}
