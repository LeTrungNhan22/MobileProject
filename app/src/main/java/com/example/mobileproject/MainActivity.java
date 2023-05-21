package com.example.mobileproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.mobileproject.fragments.HomeFragment;
import com.example.mobileproject.fragments.NotificationFragment;
import com.example.mobileproject.fragments.ProfileFragment;
import com.example.mobileproject.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);


        Bundle intent = getIntent().getExtras();

        if (intent != null) {
            String publisher = intent.getString("publisherId");

            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileId", publisher);
            editor.apply();

            // Kiểm tra xem publisherId có phải là ID người dùng hiện tại hay không
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null && publisher.equals(currentUser.getUid())) {
                // Nếu là ID người dùng hiện tại, hiển thị ProfileFragment
                selectedFragment = new ProfileFragment();
            } else {
                // Nếu không phải, hiển thị HomeFragment
                selectedFragment = new HomeFragment();
            }
        } else {
            // Không có intent, hiển thị HomeFragment
            selectedFragment = new HomeFragment();
        }

        // Tiếp tục với code hiển thị fragment
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                selectedFragment = new HomeFragment();
                break;
            case R.id.navigation_search:
                selectedFragment = new SearchFragment();
                break;
            case R.id.navigation_add:
                selectedFragment = null;
                startActivity(new Intent(MainActivity.this, PostActivity.class));
                break;
            case R.id.navigation_heart:
                selectedFragment = new NotificationFragment();
                break;
            case R.id.navigation_profile:
                SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                editor.apply();
                selectedFragment = new ProfileFragment();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }
        if (selectedFragment != null)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

        return true;
    };


}