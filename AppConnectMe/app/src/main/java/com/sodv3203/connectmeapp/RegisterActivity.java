package com.sodv3203.connectmeapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    //The Views
    EditText registerEmailInput, registerPasswordInput;
    Button registerButton;
    TextView regAccountCheckText;

    //Progressbar to display during user registration
   ProgressDialog registrationProgress;

    //Declare an instance of FirebaseAuth
    private FirebaseAuth mAuth;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Set References
        registerEmailInput = findViewById(R.id.reg_email_input);
        registerPasswordInput = findViewById(R.id.reg_password_input);
        registerButton = findViewById(R.id.btn_register);
        regAccountCheckText = findViewById(R.id.reg_account_check);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        registrationProgress = new ProgressDialog(this);
        registrationProgress.setMessage("Registering User...");

        //Set ActionBar title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Account");

        //Enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //Handle register button click
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Input Email and password
                String user_email = registerEmailInput.getText().toString().trim();
                String user_password = registerPasswordInput.getText().toString().trim();

                //Validate email and password
                if (!Patterns.EMAIL_ADDRESS.matcher(user_email).matches()) {
                    //Set error and focus on email input box
                    registerEmailInput.setError("Invalid Email");
                    registerEmailInput.setFocusable(true);
                }
                else if (user_password.length() < 6) {
                    //Set Error and focus to password input box
                    registerPasswordInput.setError("Password length must be at least 6 Characters");
                    registerPasswordInput.setFocusable(true);
                }
                else {
                    //Continue Registration
                    registerUser(user_email, user_password);
                }
            }
        });

        //Handle login textView listener
        regAccountCheckText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void registerUser(String user_email, String user_password) {
        //Since email and password is valid, show progress bar and start registering the user
        registrationProgress.show();

        mAuth.createUserWithEmailAndPassword(user_email, user_password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, dismiss progress and start register activity
                            registrationProgress.dismiss();

                            FirebaseUser user = mAuth.getCurrentUser();
                            //Get user email and uid from auth
                            String user_email = user.getEmail();
                            String user_uid = user.getUid();

                            //When a user is registered, store information in firebase realtime database as well
                            //using HashMap
                            HashMap<Object, String> hashMap = new HashMap<>();
                            //put information in hashMap
                            hashMap.put("email", user_email);
                            hashMap.put("uid", user_uid);
                            hashMap.put("name", "");   // Will add later (e.g edit profile)
                            hashMap.put("phone", "");  // Will add later (e.g edit profile)
                            hashMap.put("image", "");  // Will add later (e.g edit profile)
                            hashMap.put("cover", "");  // Will add later (e.g edit profile)

                            //firebase database instance
                            FirebaseDatabase database = FirebaseDatabase.getInstance();

                            //path to store user data named "users"
                            DatabaseReference reference = database.getReference("Users");

                            //put data within hashMap in database
                            reference.child(user_uid).setValue(hashMap);

                            Toast.makeText(RegisterActivity.this, "Registered...\n" + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, ProfileActivity.class));
                            finish();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            registrationProgress.dismiss();
                            //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onFailure(@NonNull Exception e) {
                //Error! Dismiss progress bar. get and show the error message
                registrationProgress.dismiss();
                Toast.makeText(RegisterActivity.this, "" +e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //Return to previous activity
        return super.onSupportNavigateUp();
    }
}
