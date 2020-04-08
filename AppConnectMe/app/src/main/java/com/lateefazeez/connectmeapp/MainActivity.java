package com.lateefazeez.connectmeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    //The Views
    Button registerButton, loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set reference
        registerButton = findViewById(R.id.btn_register);
        loginButton = findViewById(R.id.btn_login);

        // Handle click for register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Launch Register Activity
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

    }
}
