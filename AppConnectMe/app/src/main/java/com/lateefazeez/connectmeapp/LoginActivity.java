package com.lateefazeez.connectmeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    //The Views
    EditText loginEmailInput, loginPasswordInput;
    Button loginButton;
    TextView loginAccountCheckText;

    //Declare an instance of FirebaseAuth
    private FirebaseAuth mAuth;

    //Progress Bar
   ProgressDialog loginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmailInput = findViewById(R.id.login_email_input);
        loginPasswordInput = findViewById(R.id.login_password_input);
        loginButton = findViewById(R.id.btn_login);
        loginAccountCheckText = findViewById(R.id.login_account_check);

        //In the onCreate() method, initialize the FirebaseAuth instance.
        mAuth = FirebaseAuth.getInstance();

        //Set ActionBar title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Login");

        //Enable back button
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //input user data
                String user_email = loginEmailInput.getText().toString();
                String user_password = loginPasswordInput.getText().toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(user_email).matches()) {
                    // Set error for invalid email pattern
                    loginEmailInput.setError("Invalid Email");
                    loginEmailInput.setFocusable(true);
                }
                else {
                    //Email pattern is valid
                    loginUser(user_email, user_password);
                }
            }
        });

        loginAccountCheckText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        //initialize progress dialog
        loginProgress = new ProgressDialog(this);
        loginProgress.setMessage("Logging In...");
    }

    private void loginUser(String user_email, String user_password) {
        //show progress dialog
        loginProgress.show();
        mAuth.signInWithEmailAndPassword(user_email, user_password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //dismiss progress dialog
                            loginProgress.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                            //user is logged in, start loginactivity
                            startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                        } else {
                            //dismiss progress dialog
                            loginProgress.dismiss();
                            // If sign in fails, display a message to the user.
                           // Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loginProgress.dismiss();
                //error, get and show error message
                Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); //Return to previous activity
        return super.onSupportNavigateUp();
    }
}
