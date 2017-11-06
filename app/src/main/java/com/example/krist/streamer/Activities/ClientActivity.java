package com.example.krist.streamer.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.example.krist.streamer.Helpers.ConnectToServer;
import com.example.krist.streamer.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String img = in.readLine();
                if(img != null) {
                    byte[] bytes = decodeString(img);
                    Bitmap map = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    runOnUiThread(() -> ((ImageView) findViewById(R.id.activity_client_display)).setImageBitmap(map));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private byte[] decodeString(String string) {
        String[] str = string.substring(1, string.length() - 1).replace(" ", "").split(",");
        byte[] bytes = new byte[str.length];

        for (int i = 0, len = bytes.length; i < len; i++) {
            bytes[i] = Byte.parseByte(str[i].trim());
        }
        return bytes;
    }

    @Override
    protected void onStop() {
        if (cts != null) {
            cts.stopConnecting();
        }
        super.onStop();
    }
}
