package com.example.krist.streamer.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.example.krist.streamer.Data.CameraHelperPicture;
import com.example.krist.streamer.Data.CameraHelperVideo;
import com.example.krist.streamer.Helpers.ServerConnectListener;
import com.example.krist.streamer.R;

import java.net.Socket;
import java.nio.ByteBuffer;

public class ServerActivity extends AppCompatActivity {
    private ServerConnectListener scl;
    private CameraHelperPicture ch;
    CameraHelperVideo chv;
    boolean recording = false;
    ParcelFileDescriptor pfd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        findViewById(R.id.activity_server_connection_button).setOnClickListener(c-> listenForConnection());
        ch = new CameraHelperPicture(ServerActivity.this);
        chv = new CameraHelperVideo(this, findViewById(R.id.textureView2));
        findViewById(R.id.activity_server_start_button).setOnClickListener(c-> record());
    }

    private void takePicture() {
        ch.setCallback(i->{
            ByteBuffer buffer = i.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            Bitmap map = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            ((ImageView) findViewById(R.id.activity_server_cam_preview)).setImageBitmap(map);
        } );
        ch.takePicture(pfd);
    }

    private void record(){
        if(recording){
            chv.stopRecordingVideo();
            recording= false;
        } else {
            chv.starRecordingVideo(pfd);
            recording = true;
        }
    }

    private void listenForConnection() {
        scl = new ServerConnectListener(6672, this::prepareToStream);
        scl.start();
    }

    private void prepareToStream(Socket socket) {
        System.out.println("Connected to " + socket.getInetAddress());
        pfd = ParcelFileDescriptor.fromSocket(socket);
    }


    @Override
    protected void onStop() {
        if(scl != null) {
            scl.stopServer();
        }
        super.onStop();
    }
}