package com.example.krist.streamer.Helpers;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by krist on 2017-11-05.
 */

public class ServerConnectListener extends Thread {
    private final String TAG = "STREAMER";
    private boolean suppressed = false;

    public interface OnSocketAvailable {
        void onSocketAvailable(Socket socket);
    }

    private int port;
    private OnSocketAvailable listener;
    Socket socket;
    ServerSocket serverSocket;

    public ServerConnectListener(int port, OnSocketAvailable listener) {
        this.port = port;
        this.listener = listener;
        setName("Server-Connector");
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            Socket socket = serverSocket.accept();
            onSocketAvailable(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v(TAG, getName() + " thread finished");
    }

    public void stopServer() {
        try {
            serverSocket.close();
            Log.v(TAG, "Stopped listening for connections");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onSocketAvailable(Socket socket) {
        this.socket = socket;
        if (listener != null) {
            listener.onSocketAvailable(socket);
        }
    }
}
