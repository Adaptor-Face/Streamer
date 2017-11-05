package com.example.krist.streamer.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;

import com.example.krist.streamer.Helpers.ConnectToServer;
import com.example.krist.streamer.R;

public class ClientActivity extends AppCompatActivity {
    private ConnectToServer cts;
    private final String IP = "10.0.0.95";
    private final int port = 6672;
    private TextureView display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        findViewById(R.id.activity_client_connection_button).setOnClickListener(c -> connectToServer());
        display = findViewById(R.id.activity_client_display);
    }

    private void connectToServer() {
        String str = String.valueOf(cts == null);
        switch (str) {
            case "true":
                cts = new ConnectToServer(IP, port, b -> prepareView());
                cts.start();
                break;
            case "false":
                cts.stopConnecting();
                cts = null;
                break;
        }
    }

    private void prepareView() {
        System.out.println("Connected");
    }

    @Override
    protected void onStop() {
        if(cts != null) {
            cts.stopConnecting();
        }
        super.onStop();
    }
}
