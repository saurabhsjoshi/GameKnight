package com.collegecode.gameknight;

import android.os.AsyncTask;

/**
 * Created by saurabh on 14-11-06.
 */
public class GameKnightApi {

    public interface onTaskCompleted{public void onCompleted(String signature, long sequence, Exception e);}

    private onTaskCompleted listener;

    public void getAuthorizedSignatureForUser(onTaskCompleted listener){
        this.listener = listener;
        new generateSignature().execute();
    }

    private class generateSignature extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
