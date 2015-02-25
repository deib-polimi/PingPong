package it.polimi.wifidirect.utilities;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import it.polimi.wifidirect.WiFiDirectActivity;

/**
 * AsyncTask to create a delay with Thread.sleep.
 * This is safe, because it is not executed in the UI Thread.
 * Created by Stefano Cappa on 25/02/15.
 */
public class SleepAsyncTask extends AsyncTask<Void, Void, Void> {

    private Activity activity;

    public SleepAsyncTask(Activity activity) {
        super();
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Thread.sleep(1000); //if you have problems use 2000 here
        } catch (InterruptedException e) {
            Log.e("SleepAsyncTask", "doInBackground" , e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        ((WiFiDirectActivity)activity).sleepCompleted();
    }

}
