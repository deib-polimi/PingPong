/*
Copyright 2015 Stefano Cappa, Politecnico di Milano

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package it.polimi.wifidirect.utilities;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import it.polimi.wifidirect.WiFiDirectActivity;

/**
 * AsyncTask to create a delay with Thread.sleep.<p></p>
 * This is safe, because it is not executed in the UI Thread.
 * <p></p>
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
