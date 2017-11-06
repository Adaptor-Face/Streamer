package com.example.krist.streamer.Threads;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by krist on 2017-11-06.
 */

public class ImageRecieverThread extends Thread {
    private OnImageAvailable callback;

    public interface OnImageAvailable{
        void onImageAvailable(Bitmap map);
    }
    private Socket socket;

    public ImageRecieverThread(Socket socket) {
        this.socket = socket;
    }

    public void setOnImageAvailableListener(OnImageAvailable listener){
        this.callback = listener;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String img = in.readLine();
                if (img != null) {
                    byte[] bytes = decodeString(img);
                    Bitmap map = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    onImageAvailable(map);
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

    private void onImageAvailable(Bitmap map){
        if(map != null && callback != null){
            this.callback.onImageAvailable(map);
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

}
