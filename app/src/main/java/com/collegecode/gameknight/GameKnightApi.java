package com.collegecode.gameknight;

import android.os.AsyncTask;

import com.collegecode.gameknight.objects.Secrets;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.codec.binary.Base64;

import java.security.MessageDigest;

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

    /* Create signature */
    private class generateSignature extends AsyncTask<Void, Void, Void>{
        String signature;
        long sequence;
        Exception ex = null;

        @Override
        protected Void doInBackground(Void... voids) {
            ParseQuery query = ParseUser.getQuery();
            query.orderByDescending("sequence");
            try{
                sequence = Long.parseLong(ParseUser.getCurrentUser().getString("sequence"));
                String toSign = ParseUser.getCurrentUser().getUsername() + Secrets.appKey + sequence + Secrets.appSecret;
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
                byte[] hash = messageDigest.digest(toSign.getBytes("UTF-8"));
                signature = Base64.encodeBase64String(hash).trim();
            }catch (Exception e){ex = e;}
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            listener.onCompleted(signature, sequence, ex);
        }
    }
}
