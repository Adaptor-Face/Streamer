package com.example.krist.streamer.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.example.krist.streamer.Helpers.ConnectToServer;
import com.example.krist.streamer.R;
import com.example.krist.streamer.Threads.ImageRecieverThread;

import java.net.Socket;

public class ClientActivity extends AppCompatActivity {
    private ConnectToServer cts;
    private final String IP = "10.0.0.95";
    private final int port = 6672;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        findViewById(R.id.activity_client_connection_button).setOnClickListener(c -> connectToServer());
    }

    private void connectToServer() {
        String str = String.valueOf(cts == null);
        switch (str) {
            case "true":

                cts = new ConnectToServer(IP, port, s -> imageStreamDisplay(s));
                cts.start();
                break;
            case "false":
                cts.stopConnecting();
                cts = null;
                break;
        }
    }

    private void imageStreamDisplay(Socket socket) {
        ImageRecieverThread fetcher = new ImageRecieverThread(socket);
        fetcher.setOnImageAvailableListener(bitmap-> runOnUiThread(()->((ImageView) findViewById(R.id.activity_client_display)).setImageBitmap(bitmap)));
        fetcher.start();
    }

    @Override
    protected void onStop() {
        if (cts != null) {
            cts.stopConnecting();
        }
        super.onStop();
    }
}
