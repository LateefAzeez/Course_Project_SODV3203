package com.sodv3203.connectmeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatActivity extends AppCompatActivity {
    //views from xml layout
    Toolbar chatToolBar;
    RecyclerView chatRecyclerView;
    ImageView chatUserImage;
    TextView chatReceiverStatus, chatReceiverName;
    EditText chatMessage;
    ImageButton chatSendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Set Views
        chatToolBar = findViewById(R.id.chat_user_toolbar);
        setSupportActionBar(chatToolBar);
        chatToolBar.setTitle("");
        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        chatReceiverName = findViewById(R.id.chat_receiver_name);
        chatReceiverStatus = findViewById(R.id.chat_receiver_status);
        chatMessage = findViewById(R.id.chat_message);
        chatSendButton = findViewById(R.id.send_message_button);



    }
}
