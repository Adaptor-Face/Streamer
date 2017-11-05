package com.example.krist.streamer.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.example.krist.streamer.Helpers.ConnectToServer;
import com.example.krist.streamer.R;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ClientActivity extends AppCompatActivity {
    private ConnectToServer cts;
    private final String IP = "10.0.0.95";
    private final int port = 6672;
    private ImageView display;
    private Socket socket;

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

                cts = new ConnectToServer(IP, port, s -> prepareView(s));
                cts.start();
                break;
            case "false":
                cts.stopConnecting();
                cts = null;
                break;
        }
    }

    private void prepareView(Socket socket) {
        this.socket = socket;
        runOnUiThread(() -> {
            try {
                ParcelFileDescriptor pfd = ParcelFileDescriptor.fromSocket(socket);
                InputStream input = new FileInputStream(pfd.getFileDescriptor());
                BufferedInputStream buf = new BufferedInputStream(input);
                try {
                    while (true) {
                        Bitmap bMap = BitmapFactory.decodeStream(buf);
                        ((ImageView) findViewById(R.id.activity_client_display)).setImageBitmap(bMap);
                    }
                } finally {
                    if (input != null) {
                        input.close();
                    }
                    if (buf != null) {
                        buf.close();
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onStop() {
        if (cts != null) {
            cts.stopConnecting();
        }
        super.onStop();
    }
}
