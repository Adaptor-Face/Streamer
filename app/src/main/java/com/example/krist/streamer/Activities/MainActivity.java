package com.example.krist.streamer.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.krist.streamer.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.activity_main_client_button).setOnClickListener(c-> startClient());
        findViewById(R.id.activity_main_server_button).setOnClickListener(c-> startServer());
    }

    private void startServer() {
        Intent intent = new Intent(this, ServerActivity.class);
        startActivity(intent);
    }

    private void startClient() {
        Intent intent = new Intent(this, ClientActivity.class);
        startActivity(intent);
    }
}
