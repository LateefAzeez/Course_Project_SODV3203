package com.sodv3203.connectmeapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class HomeFragment extends Fragment {

    //Firebase Authentication
    FirebaseAuth firebaseAuth;

    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //storage
    StorageReference storageReference;


    //Views from xml files
    CardView fitnessGroupCard, MusicGroupCard, fashionGroupCard, gamesGroupCard, SportsViewCard, dogGroupCard, infoTechGroupCard, deepLearningGroupCard;

    TextView fitnessMemberCount, MusicMemberCount, fashionMemberCount, gamesMemberCount, SportsMemberCount, dogMemberCount, infoTechMemberCount, deepLearningmemberCount;

    ProgressDialog progressDialog;



    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Groups");
        storageReference = FirebaseStorage.getInstance().getReference(); // firebase storage reference


        //Init Views
        fitnessGroupCard = view.findViewById(R.id.card_fitness);
        MusicGroupCard = view.findViewById(R.id.card_music);
        fashionGroupCard = view.findViewById(R.id.card_fashion);
        gamesGroupCard = view.findViewById(R.id.card_games);
        SportsViewCard = view.findViewById(R.id.card_sports);
        dogGroupCard = view.findViewById(R.id.card_dogs);
        infoTechGroupCard = view.findViewById(R.id.card_info_tech);
        deepLearningGroupCard = view.findViewById(R.id.card_deep_learning);

        //setup group info

        //init progress dialog
        progressDialog = new ProgressDialog(getActivity());

        addGroup();


        fitnessGroupCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMemberJoiningDialog();
            }
        });
        MusicGroupCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMemberJoiningDialog();
            }
        });
        fashionGroupCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMemberJoiningDialog();
            }
        });
        gamesGroupCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMemberJoiningDialog();
            }
        });
        SportsViewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMemberJoiningDialog();
            }
        });
        dogGroupCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMemberJoiningDialog();
            }
        });
        infoTechGroupCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMemberJoiningDialog();
            }
        });
        deepLearningGroupCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMemberJoiningDialog();
            }
        });

        return view;
    }

    private void addGroup() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Groups");
        String id = databaseReference.push().getKey();
//        Groups groups = new Groups(groupId, groupTitle, timeStamp, participants);
//
//        databaseReference.child(id).setValue(groups);
    }

    private void showMemberJoiningDialog() {

        //custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("This is a Member-Only Group, Do you want to join this group? "); // get permission to join

        //set layout of dialog
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10, 10, 10, 10);

        //setView
        builder.setView(linearLayout);

        //add buttons in dialog to update
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // dismiss the dialog
                dialog.dismiss();
                progressDialog.setMessage("Registering you on the group");
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
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
