// Copyright 2011 Google Inc. All Rights Reserved.

package it.polimi.wifidirect;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * A service that process each file transfer request i.e Intent by opening a
 * socket connection with the WiFi Direct Group Owner and writing the file
 *
 * Copyright 2011 Google Inc. All Rights Reserved. Modified by Stefano Cappa (2015).
 */
public class FileTransferService extends IntentService {

    private static final String TAG = "FileTransferService";
    private static final int SOCKET_TIMEOUT = 5000;
    public static final String ACTION_SEND_FILE = "it.polimi.wifidirect.SEND_FILE";
    public static final String EXTRAS_FILE_PATH = "file_url";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";

    private long sended = 0;

    public FileTransferService(String name) {
        super(name);
    }

    public FileTransferService() {
        super("FileTransferService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Context context = getApplicationContext();
        if (intent.getAction().equals(ACTION_SEND_FILE)) {
            String fileUri = intent.getExtras().getString(EXTRAS_FILE_PATH);
            String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
            Socket socket = new Socket();
            int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);

            try {
                Log.d(TAG, "Opening client socket - ");
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);

                Log.d(TAG, "Client socket - " + socket.isConnected());
                OutputStream stream = socket.getOutputStream();

                Log.d(TAG, context.getFilesDir().getAbsolutePath() + "");

                ContentResolver cr = context.getContentResolver();
                InputStream is = null;
                try {
                    is = cr.openInputStream(Uri.parse(fileUri));
                } catch (FileNotFoundException e) {
                    Log.e(TAG, e.getMessage());
                }
                copyFileClientSide(is, stream);
                Log.d(TAG, "Client: Data written");
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } finally {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

        }
    }

    private void copyFileClientSide(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);
                sended = sended + len;
            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            Log.e("FileTransferService", "copyFileClientSide",e);
        }
    }
}
