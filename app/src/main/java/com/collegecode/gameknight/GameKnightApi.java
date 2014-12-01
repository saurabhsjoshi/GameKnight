package com.collegecode.gameknight;

import android.os.AsyncTask;

import com.collegecode.gameknight.objects.Message;
import com.collegecode.gameknight.objects.Secrets;
import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.codec.binary.Base64;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by saurabh on 14-11-06.
 */
public class GameKnightApi {

    public interface onTaskCompleted{public void onCompleted(String signature, long sequence, Exception e);}
    public interface onParseRequestCompleted{public void onCompleted(Object result, com.parse.ParseException exception);}

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

    public static void getAllUsersInRoom(String gameCode, final onParseRequestCompleted listener){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("currentRoom", gameCode);
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, com.parse.ParseException e) {
                if(e!=null)
                    e.printStackTrace();
                List<String> lst = new ArrayList<String>();
                for(int i = 0; i < parseUsers.size(); i++){
                    lst.add(parseUsers.get(i).getString("username"));
                }
                listener.onCompleted(lst, e);
            }
        });
    }

    public static String createJSONMessage(Message message){
        JSONObject object = new JSONObject();
        try{
            object.put("type", message.type);
            object.put("message", message.message);
        }catch (Exception ignore){}
        return object.toString();
    }

    public static Message getMessageFromJSON(String message){
        try {
            JSONObject object = new JSONObject(message);
            return new Message(object.getString("type"), object.getString("message"));

        }catch (Exception ignore){}
        return null;
    }

}
