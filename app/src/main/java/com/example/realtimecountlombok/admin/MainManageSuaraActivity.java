package com.example.realtimecountlombok.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.realtimecountlombok.R;
import com.example.realtimecountlombok.util.Util;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainManageSuaraActivity extends AppCompatActivity {

    private static final String TAG = "MainManageSuaraActivity";
    final FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment inputSuaraFragment = new InputSuaraFragment();
    Fragment listSuaraFragment = new ListSuaraFragment();
    Fragment activeFragment;
    Fragment inactiveFragment;
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_manage_suara);

        bottomNav = findViewById(R.id.bottomNavigationManageSuara);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Util.backToLogin(MainManageSuaraActivity.this);
        }

        fragmentManager.beginTransaction().add(R.id.fragmentContainerManageSuara, inputSuaraFragment, "1").commit();
        fragmentManager.beginTransaction().add(R.id.fragmentContainerManageSuara, listSuaraFragment, "2").commit();
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        bottomNav.setSelectedItemId(R.id.nav_cek_suara);
        // fragmentManager.beginTransaction().hide(firstInactiveFragment).hide(secondInactiveFragment).show(activeFragment).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.nav_input_suara:
                            activeFragment = inputSuaraFragment;
                            inactiveFragment = listSuaraFragment;
                            break;
                        case R.id.nav_cek_suara:
                            activeFragment = listSuaraFragment;
                            inactiveFragment = inputSuaraFragment;
                            break;
                    }
                    fragmentManager.beginTransaction().hide(inactiveFragment).show(activeFragment).commit();
                    return true;
                }
            };

}