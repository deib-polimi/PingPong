package it.polimi.wifidirect;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A simple server socket that accepts connection and writes some data on
 * the stream.
 *
 * Created by Stefano Cappa on 22/02/15.
 */
class FileServerAsyncTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = "FileServerSyncTask";

    private Context context;

    /**
     * Constructor of this class.
     * @param context Context
     */
    public FileServerAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            ServerSocket serverSocket = new ServerSocket(8988);
            Log.d(TAG, "Server: Socket opened");
            Socket client = serverSocket.accept();
            Log.d(TAG, "Server: connection done");
            final File f = new File(Environment.getExternalStorageDirectory() + "/"
                    + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
                    + ".mp4");

            File dirs = new File(f.getParent());
            if (!dirs.exists())
                dirs.mkdirs();

            if(!f.exists()) {
                f.createNewFile();
            }

            Log.d(TAG, "Server: copying files " + f.toString());
            InputStream inputstream = client.getInputStream();

            copyFile(inputstream, new FileOutputStream(f));

            serverSocket.close();
            return f.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            context.startActivity(intent);
        }

    }

    /**
     * Method to copy a file from input to output streams.
     * @param inputStream InputStream
     * @param out OutputStream
     * @return true if completed, or false otherwise.
     */
    private static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0 , len);
            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "copyFile", e);
            return false;
        }
        return true;
    }
}
