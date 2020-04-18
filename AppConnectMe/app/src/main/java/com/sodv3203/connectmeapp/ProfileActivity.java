package com.sodv3203.connectmeapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity implements
  FetchAddressTask.OnTaskCompleted {

  //Location Declarations
  private static final int REQUEST_LOCATION_PERMISSION = 1;
  String TAG = "[Check] Profile:";
  Location mLastLocation;
  String mLastLocationAddress;
  FusedLocationProviderClient mFusedLocationClient;

  //Firebase Authentication
  FirebaseAuth firebaseAuth;
  ActionBar actionBar;


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
        case R.id.nav_map:
          //profile fragment transaction
          actionBar.setTitle("Map");  //change actionbar title
          MapFragment mapFragment = new MapFragment();
          FragmentTransaction mapFragmentTransaction = getSupportFragmentManager().beginTransaction();
          mapFragmentTransaction.replace(R.id.content, mapFragment, "");
          mapFragmentTransaction.commit();
          return true;

      }

      return false;
    }


  };

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

    //Location member variables
    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    //Start tracking location
    getLocation();

  }

  public void checkUserStatus() {
    //get current user
    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    if (currentUser != null) {
      //user is signed in, stay here
      //set email of logged in user
      //userProfile.setText(currentUser.getEmail());
    } else {
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

  private void getLocation() {
    if (ActivityCompat.checkSelfPermission(this,
      Manifest.permission.ACCESS_FINE_LOCATION)
      != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[]
          {Manifest.permission.ACCESS_FINE_LOCATION},
        REQUEST_LOCATION_PERMISSION);
    } else {

      mFusedLocationClient.getLastLocation().addOnSuccessListener(
        new OnSuccessListener<Location>() {
          @Override
          public void onSuccess(Location location) {
            if (location != null) {
                mLastLocation = location;
                String mLastLocationRetrieved = getString(R.string.location_text,
                  mLastLocation.getLatitude(),
                  mLastLocation.getLongitude(),
                  mLastLocation.getTime());
                Toast.makeText(ProfileActivity.this, mLastLocationRetrieved, Toast.LENGTH_LONG).show();

              // Start the reverse geocode AsyncTask
              new FetchAddressTask(ProfileActivity.this,
                ProfileActivity.this).execute(location);
              Toast.makeText(ProfileActivity.this, R.string.address_loading, Toast.LENGTH_SHORT).show();
            } else {
              Toast.makeText(ProfileActivity.this, R.string.location_not_found, Toast.LENGTH_SHORT).show();
            }
          }
        });
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    switch (requestCode) {
      case REQUEST_LOCATION_PERMISSION:
        // If the permission is granted, get the location,
        // otherwise, show a Toast
        if (grantResults.length > 0
          && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          getLocation();
        } else {
          Log.d(TAG, String.valueOf(R.string.location_permission_denied));
          Toast.makeText(this,
            R.string.location_permission_denied,
            Toast.LENGTH_SHORT).show();
        }
        break;
    }
  }

  @Override
  public void onTaskCompleted(String result) {
    // Update the UI
    mLastLocationAddress = getString(R.string.address_text, result, System.currentTimeMillis());
    Toast.makeText(this, mLastLocationAddress, Toast.LENGTH_LONG).show();
    Log.d(TAG,mLastLocationAddress);
  }

}
