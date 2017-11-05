package com.example.krist.streamer.Activities;

import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.example.krist.streamer.Data.CameraHelperPicture;
import com.example.krist.streamer.Data.CameraHelperVideo;
import com.example.krist.streamer.Helpers.ServerConnectListener;
import com.example.krist.streamer.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
        findViewById(R.id.activity_server_connection_button).setOnClickListener(c-> listenForConnection((Button) c));
        ch = new CameraHelperPicture(this);
        chv = new CameraHelperVideo(this);
        findViewById(R.id.activity_server_start_button).setOnClickListener(c-> takePicture());
    }

    private void takePicture() {
//        ch.setCallback(i->{
//            ByteBuffer buffer = i.getPlanes()[0].getBuffer();
//            byte[] bytes = new byte[buffer.capacity()];
//            buffer.get(bytes);
//            YuvImage yuvImage = new YuvImage(bytes, ImageFormat.NV21, i.getWidth(), i.getHeight(), null);
//            Bitmap map = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
//            ((ImageView) findViewById(R.id.activity_server_cam_preview)).setImageBitmap(map);
//        } );
        ch.setCallback(image -> {
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            OutputStream output = null;
            try {
                output = new FileOutputStream(pfd.getFileDescriptor());
                output.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        ch.takePicture();
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



    private void listenForConnection(Button v) {
        v.setText("Wating for connection");
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