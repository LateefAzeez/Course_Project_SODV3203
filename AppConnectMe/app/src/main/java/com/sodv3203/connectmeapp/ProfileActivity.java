package com.sodv3203.connectmeapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class ProfileActivity extends AppCompatActivity implements
  FetchAddressTask.OnTaskCompleted {

  String TAG = "[Check] ProfileActiv.";

  //Location Declarations
  private static final int REQUEST_LOCATION_PERMISSION = 1;
  static Location mLastLocation;
  FusedLocationProviderClient mFusedLocationClient;
  LocationCallback mLocationCallback;


  //Firebase Authentication

  private FirebaseDatabase database = FirebaseDatabase.getInstance();
  private DatabaseReference myRef = database.getReference();
  FirebaseAuth firebaseAuth;
  FirebaseUser currentUser;
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
        case R.id.nav_find_friends:
          //profile fragment transaction
          actionBar.setTitle("Find Friends");  //change actionbar title
          FindFriendsFragment findFriendsFragment = new FindFriendsFragment();
          FragmentTransaction findFriendsFragmentTransaction = getSupportFragmentManager().beginTransaction();
          findFriendsFragmentTransaction.replace(R.id.content, findFriendsFragment, "");
          findFriendsFragmentTransaction.commit();
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
    assert actionBar != null;
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

    // Initialize the location callbacks.
    mLocationCallback = new LocationCallback() {
      /**
       * This is the callback that is triggered when the
       * FusedLocationClient updates your location.
       *
       * @param locationResult The result containing the device location.
       */
      @Override
      public void onLocationResult(LocationResult locationResult) {

//        Log.d(TAG, "locationResult - lat: " + locationResult.getLastLocation().getLatitude());
//        Log.d(TAG, "locationResult - long: " + locationResult.getLastLocation().getLongitude());

        Location mNewLocation = locationResult.getLastLocation();

        if (mNewLocation != null) {

          if (mLastLocation == null) { //in the first time getLocation() runs, mLastLocation will be null... this condition block handles it and avoid crash.

            mLastLocation = mNewLocation;
            String mLastLocationString = getString(R.string.location_text, mLastLocation.getLatitude(), mLastLocation.getLongitude(), mLastLocation.getTime());
            Toast.makeText(ProfileActivity.this, mLastLocationString, Toast.LENGTH_LONG).show();

            new FetchAddressTask(ProfileActivity.this, ProfileActivity.this)
              .execute(locationResult.getLastLocation());

            try{
              postToDatabase(mLastLocation);
            } catch (Throwable e) {
              e.printStackTrace();
            }

          } else { //if there's a last location, only fetch address if the latitude and longitude of the new location are different from last location

            double mNewRoundedLatitude = roundLatLong(mNewLocation.getLatitude());
            double mLastRoundedLatitude = roundLatLong(mLastLocation.getLatitude());
            double mNewRoundedLongitude = roundLatLong(mNewLocation.getLongitude());
            double mLastRoundedLongitude = roundLatLong(mLastLocation.getLongitude());

//            Log.d(TAG, "Location Comparison:" + "\n\n  mNewRoundedLatitude:" + mNewRoundedLatitude + "\n  mLastRoundedLatitude:" + mLastRoundedLatitude+ "\n\n  mLastRoundedLongitude:" + mLastRoundedLongitude + "\n  mNewRoundedLongitude:" + mNewRoundedLongitude + "\n\n");

            // compare new location with last location to decide if address should be fetched
            if (mNewRoundedLatitude != mLastRoundedLatitude || mNewRoundedLongitude != mLastRoundedLongitude) {
              mLastLocation = mNewLocation;
              String mLastLocationString = getString(R.string.location_text, mLastLocation.getLatitude(), mLastLocation.getLongitude(), mLastLocation.getTime());
              Log.d(TAG, "Last location processed: " + mLastLocationString);
              Toast.makeText(ProfileActivity.this, mLastLocationString, Toast.LENGTH_LONG).show();

              new FetchAddressTask(ProfileActivity.this, ProfileActivity.this)
                .execute(locationResult.getLastLocation());

              try{
                postToDatabase(mLastLocation);
              } catch (Throwable e) {
                e.printStackTrace();
              }

            }
          }
        }
      }
    };
  }

  public void checkUserStatus() {
    //get current user
    currentUser = firebaseAuth.getCurrentUser();

    assert currentUser != null;
    Log.d(TAG, "Current User Uid: " + currentUser.getUid());
    Log.d(TAG, "Current User Name: " + currentUser.getDisplayName());
    Log.d(TAG, "Current User Email: " + currentUser.getEmail());


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

  public double roundLatLong(Double number){
    double precision =  Math.pow(10, 5);
    return ((int)(precision * number))/precision;
  };

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
              mFusedLocationClient.requestLocationUpdates
                (getLocationRequest(),
                  mLocationCallback,
                  null /* Looper */);
              Toast.makeText(ProfileActivity.this, R.string.address_loading, Toast.LENGTH_SHORT).show();

            } else {
              Log.d(TAG, String.valueOf(R.string.address_not_found));
              Toast.makeText(ProfileActivity.this, R.string.address_not_found, Toast.LENGTH_SHORT).show();
            }
          }
        }
      );
    }
  }

  private LocationRequest getLocationRequest() {
    LocationRequest locationRequest = new LocationRequest();
    locationRequest.setInterval(60000);
    locationRequest.setFastestInterval(20000);
    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    return locationRequest;
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == REQUEST_LOCATION_PERMISSION) {// If the permission is granted, get the location,
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
    }
  }

  @Override
  public void onTaskCompleted(String result) {
    // Update the UI
      String mLastAddressFetched = getString(R.string.address_text, result, System.currentTimeMillis());
      Toast.makeText(this, mLastAddressFetched, Toast.LENGTH_LONG).show();
      Log.d(TAG, "Fetched "+mLastAddressFetched);
  }

  @Override
  protected void onPause() {
    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
      super.onPause();
    }

  @Override
  protected void onResume() {
    getLocation();
    super.onResume();
  }

  public void postToDatabase(Location location) {

    //firebase database instance
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    //path to store user data named "users"
    DatabaseReference reference = database.getReference("Users");

    //put data within database
    String childPath = currentUser.getUid()+"/lastLocation";
    reference.child(childPath).setValue(location);
  }

}
