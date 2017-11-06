package com.example.krist.streamer.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.example.krist.streamer.Data.CameraHelperMediaCodec;
import com.example.krist.streamer.Data.CameraHelperPicture;
import com.example.krist.streamer.Data.CameraHelperVideo;
import com.example.krist.streamer.Data.ImageUtils;
import com.example.krist.streamer.Helpers.ServerConnectListener;
import com.example.krist.streamer.R;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Arrays;

public class ServerActivity extends AppCompatActivity {
    private ServerConnectListener scl;
    private CameraHelperPicture ch;
    CameraHelperVideo chv;
    boolean recording = false;
    private CameraHelperMediaCodec chm;
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        findViewById(R.id.activity_server_connection_button).setOnClickListener(c -> listenForConnection((Button) c));
        ch = new CameraHelperPicture(this);
//        chv = new CameraHelperVideo(this);
//        chm = new CameraHelperMediaCodec(this,findViewById(R.id.activity_server_cam_preview));
        findViewById(R.id.activity_server_start_button).setOnClickListener(c -> takePicture());
    }

    private void takePicture() {
        ch.setCallback(i->{
            byte[] data = ImageUtils.imageToByteArray(i);
            PrintStream output = null;
            String string = Arrays.toString(data);
            try {
                if(socket != null) {
                    output = new PrintStream(socket.getOutputStream());
                    output.println(string);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } );
        ch.takePicture();
    }


    private void listenForConnection(Button v) {
        v.setText("Wating for connection");
        scl = new ServerConnectListener(6672, this::prepareToStream);
        scl.start();
    }

    private void prepareToStream(Socket socket) {
        System.out.println("Connected to " + socket.getInetAddress());
        this.socket = socket;
    }


    @Override
    protected void onStop() {
        if (scl != null) {
            scl.stopServer();
        }
        super.onStop();
    }
}