package com.lateefazeez.connectmeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    TextView loginAccountCheckText, recoverPassword;

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
        recoverPassword = findViewById(R.id.recover_password);

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
                finish();
            }
        });

        // Handle clicks on password recovery text
        recoverPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog();
            }
        });

        //initialize progress dialog
        loginProgress = new ProgressDialog(this);

    }

    private void showRecoverPasswordDialog() {
        //Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

        //Set linear layout
        LinearLayout pageLinearLayout = new LinearLayout(this);

        //Views to set in dialog
        final EditText userEmailInput = new EditText(this);
        userEmailInput.setHint("Your Email");
        userEmailInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        //set the minimum width of the email input to fit a text of n "M" Letters regardless of the actual text extension and text size
        userEmailInput.setMinEms(16);

        pageLinearLayout.addView(userEmailInput);
        pageLinearLayout.setPadding(10, 10, 10, 10);

        builder.setView(pageLinearLayout);

        //recover button
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //get user email
                String userEmail = userEmailInput.getText().toString().trim();
                beginRecovery(userEmail);
            }
        });



        //cancel button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Dismiss the dialog
                dialog.dismiss();
            }
        });

        //show dialog
        builder.create().show();

    }

    private void beginRecovery(String userEmail) {
        //show progress dialog
        //show progress dialog
        loginProgress.setMessage("Sending email...");
        loginProgress.show();
        mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loginProgress.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(LoginActivity.this, "Failed...", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loginProgress.dismiss();
                //get and show the error message
                Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUser(String user_email, String user_password) {
        //show progress dialog
        loginProgress.setMessage("Logging In...");
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
