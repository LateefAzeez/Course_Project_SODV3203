package com.lateefazeez.connectmeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    //Firebase Authentication
    FirebaseAuth firebaseAuth;

    //The Views
    TextView userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Set ActionBar title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");

        //initialize firebase
        firebaseAuth = FirebaseAuth.getInstance();

        //set references
        userProfile = findViewById(R.id.user_profile);

    }

    public void checkUserStatus () {
        //get current user
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            //user is signed in, stay here
            //set email of logged in user
            userProfile.setText(currentUser.getEmail());
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

    //Inflate options menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflating the menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //handle menu items clicks

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //get item id
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}
