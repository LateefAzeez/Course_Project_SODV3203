package com.sodv3203.connectmeapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ConnectMapFragment extends Fragment {

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View rootView = inflater.inflate(R.layout.fragment_connect_map, container, false);



    SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map);
    mapFragment.getMapAsync(new OnMapReadyCallback() {
      @Override
      public void onMapReady(final GoogleMap mMap) {

        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.clear(); //clear old markers

        CameraPosition userLocation = CameraPosition.builder()
          .target(new LatLng(ProfileActivity.mLastLocation.getLatitude(), ProfileActivity.mLastLocation.getLongitude()))
          .zoom(15)
          .bearing(0)
          .tilt(0)
          .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(userLocation), 1500, null);

        FirebaseDatabase.getInstance().getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {

            mMap.clear(); //clear old markers

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();

            for (DataSnapshot user : dataSnapshot.getChildren()) {
              String name = (String) user.child("name").getValue();
              String email = (String) user.child("email").getValue();
              DataSnapshot lastLocation = user.child("lastLocation");
              Double latitude = (Double) lastLocation.child("latitude").getValue();
              Double longitude = (Double) lastLocation.child("longitude").getValue();

              Log.d(TAG, "onDataChange: latitude"+latitude);
              Log.d(TAG, "onDataChange: longitude"+longitude);


              if(latitude != null && longitude != null && !currentUser.getEmail().equals(email)) {
                mMap.addMarker(new MarkerOptions()
                  .position(new LatLng(latitude, longitude))
                  .title(email)
                  .snippet("test snippet")
                );
              }
            }
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {
            Log.d(TAG, "[CHECK] onDataChange: FAIL \n" + databaseError);
          }
        });

      }
    });
    return rootView;
  }
}
