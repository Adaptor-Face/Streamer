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
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class ClientActivity extends AppCompatActivity {
    private ConnectToServer cts;
    private final String IP = "192.168.0.125";
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

                cts = new ConnectToServer(IP, port, s -> hailMary(s));
                cts.start();
                break;
            case "false":
                cts.stopConnecting();
                cts = null;
                break;
        }
    }

    private void hailMary(Socket socket) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String img = in.readLine();
            System.out.println(img);
            String[] str = img.substring(1, img.length() - 1).replace(" ", "").split(",");
            System.out.println(str.length);
            int counter = 0;
            for (String s : str) {
                System.out.print(s);
                counter++;
                if((counter % 500) == 0){
                    System.out.println();
                }
            }
            System.out.println();
            byte[] bytes = new byte[str.length];

            for (int i=0, len=bytes.length; i<len; i++) {
                bytes[i] = Byte.parseByte(str[i].trim());
            }
            for (byte b : bytes) {
                System.out.print(b);
            }
            System.out.println();
            while (true) {
                sleep(1999);
                Bitmap map = BitmapFactory.decodeByteArray(bytes, 0, 0);
                runOnUiThread(() -> ((ImageView) findViewById(R.id.activity_client_display)).setImageBitmap(map));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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

    private void prepareView(Socket socket) {
        this.socket = socket;
        runOnUiThread(() -> {
            try {
                ParcelFileDescriptor pfd = ParcelFileDescriptor.fromSocket(socket);
                InputStream input = new FileInputStream(pfd.getFileDescriptor());
                BufferedInputStream buf = new BufferedInputStream(input);
                try {
                    while (true) {
                        System.out.println(buf.available());
                        byte[] bytes = new byte[buf.available()];
                        buf.read(bytes);
                        Bitmap bMap = null;
                        while (bMap == null) {
                            sleep(50);
                            byte[] subBytes = new byte[buf.available()];
                            buf.read(subBytes);
                            bytes = concat(bytes, subBytes);
                            bMap = BitmapFactory.decodeByteArray(bytes, 0, 0);
                            System.out.println(bytes.length);
                            for (byte b : bytes) {
                                System.out.print(b);
                            }
                            System.out.println();
                        }
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public byte[] concat(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;
        byte[] c = new byte[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    @Override
    protected void onStop() {
        if (cts != null) {
            cts.stopConnecting();
        }
        super.onStop();
    }
}
