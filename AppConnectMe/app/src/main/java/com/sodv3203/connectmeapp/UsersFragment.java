package com.sodv3203.connectmeapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import adapters.AdapterUsers;
import models.ModelUser;


public class UsersFragment extends Fragment {

    RecyclerView recyclerView;
    AdapterUsers adapterUsers;
    List<ModelUser> userList;

    //Firebase Authentication
    FirebaseAuth firebaseAuth;

    public UsersFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        //Set reference
        recyclerView = view.findViewById(R.id.users_recycler_view);

        //initialize firebase
        firebaseAuth = FirebaseAuth.getInstance();

        //set its properties
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //set reference
        userList = new ArrayList<>();

        //get All users
        getAllUsers();

        return view;
    }

    private void getAllUsers() {

        //get current user
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //get path of database named "Users" containing users info
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        //get all data from path
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    ModelUser modelUser = dataSnapshot1.getValue(ModelUser.class);

                    //get all user except currently signed in user
                    try{
                    if (!modelUser.getUid().equals(firebaseUser.getUid())) {
                        userList.add(modelUser);
                    }}catch (Exception e){
                        e.printStackTrace();
                    }

                    //adapter
                    adapterUsers = new AdapterUsers(getActivity(), userList);

                    //set adapter to recycle view
                    recyclerView.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchUsers(final String query) {
        //get current user
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //get path of database named "Users" containing users info
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        //get all data from path
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    ModelUser modelUser = dataSnapshot1.getValue(ModelUser.class);

                    /*Conditions fulfil search:
                    * 1. User not current user
                    * 2. The user name or email contains text entered in SeachView (case insensitive)*/


                    //get all searched users except currently signed in user
                    if (!modelUser.getUid().equals(firebaseUser.getUid())) {

                        if(modelUser.getName().toLowerCase().contains(query.toLowerCase()) || modelUser.getEmail().toLowerCase().contains(query.toLowerCase())) {
                            userList.add(modelUser);
                        }
                        userList.add(modelUser);
                    }

                    //adapter
                    adapterUsers = new AdapterUsers(getActivity(), userList);
                    //refresh adapter
                    adapterUsers.notifyDataSetChanged();

                    //set adapter to recycle view
                    recyclerView.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

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
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true); // to show menu options in fragment
        super.onCreate(savedInstanceState);
    }

    //Inflate options menu

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflating the menu
        inflater.inflate(R.menu.menu_main, menu);

        //searchView
        MenuItem item = menu.findItem(R.id.action_search);
        //SearchView searchView = (SearchView) getActionView(item);
        try {
            SearchView searchView = (SearchView)item.getActionView();

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    //called when user press search button from keypad
                    //if search query is not empty then search
                    if(!TextUtils.isEmpty(s.trim())) {
                        searchUsers(s);
                    }
                    else {
                        //search query contains text, search it
                        getAllUsers();
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    //if search query is not empty then search
                    if(!TextUtils.isEmpty(s.trim())) {
                        searchUsers(s);
                    }
                    else {
                        //search query contains text, search it
                        getAllUsers();
                    }
                    return false;
                }
            });
        }
        catch (Exception e) {

        }

        //Search listener

        super.onCreateOptionsMenu(menu, inflater);
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
