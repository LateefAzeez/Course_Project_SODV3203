package com.sodv3203.connectmeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    //Firebase Authentication
    FirebaseAuth firebaseAuth;

    ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Set ActionBar title
        actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");

        //initialize firebase
        firebaseAuth = FirebaseAuth.getInstance();

        //bottom Navigation
        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        //home fragment transaction => Default onStart
        actionBar.setTitle("Home"); //change actionbar Title
        HomeFragment fragment_home = new HomeFragment();
        FragmentTransaction homeFragmentTransaction = getSupportFragmentManager().beginTransaction();
        homeFragmentTransaction.replace(R.id.content, fragment_home, "");
        homeFragmentTransaction.commit();

    }



    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener = new OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            //handle item clicks
            switch (item.getItemId()) {
                case R.id.nav_home:
                //home fragment transaction
                    actionBar.setTitle("Home");  //change actionbar title
                    HomeFragment homeFragment = new HomeFragment();
                    FragmentTransaction homeFragmentTransaction = getSupportFragmentManager().beginTransaction();
                    homeFragmentTransaction.replace(R.id.content, homeFragment, "");
                    homeFragmentTransaction.commit();
                    return true;
                case R.id.nav_profile:
                    //profile fragment transaction
                    actionBar.setTitle("Profile");  //change actionbar title
                    ProfileFragment profileFragment = new ProfileFragment();
                    FragmentTransaction profileFragmentTransaction = getSupportFragmentManager().beginTransaction();
                    profileFragmentTransaction.replace(R.id.content, profileFragment, "");
                    profileFragmentTransaction.commit();
                    return true;
                case R.id.nav_users:
                    //profile fragment transaction
                    actionBar.setTitle("Users");  //change actionbar title
                    UsersFragment usersFragment = new UsersFragment();
                    FragmentTransaction usersFragmentTransaction = getSupportFragmentManager().beginTransaction();
                    usersFragmentTransaction.replace(R.id.content, usersFragment, "");
                    usersFragmentTransaction.commit();
                    return true;

                case R.id.nav_chat:
                    //profile fragment transaction
                    actionBar.setTitle("Users");  //change actionbar title
                    ChatListsFragment chatListsFragment = new ChatListsFragment();
                    FragmentTransaction chatListFragmentTransaction = getSupportFragmentManager().beginTransaction();
                    chatListFragmentTransaction.replace(R.id.content, chatListsFragment, "");
                    chatListFragmentTransaction.commit();
                    return true;

            }

          return false;
        }


    };

    public void checkUserStatus () {
        //get current user
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            //user is signed in, stay here
            //set email of logged in user
            //userProfile.setText(currentUser.getEmail());
        }
        else {
            //user not signed in, go to main activity
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        // Check user on start of app
        checkUserStatus();
        super.onStart();
    }


}
