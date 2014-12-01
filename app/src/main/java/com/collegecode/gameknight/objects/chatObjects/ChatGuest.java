package com.collegecode.gameknight.objects.chatObjects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.collegecode.gameknight.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by saurabh on 14-11-29.
 */
public class ChatGuest implements ChatInterface {
    private Context context;
    private String message;
    private String sender;

    public ChatGuest(Context context,String sender, String message){
        this.context = context;
        this.message = message;
        this.sender = sender;
    }

    @Override
    public int getViewType() {
        return ChatTypes.CHAT_GUEST.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView, ViewGroup parent) {
        View view;

        if(convertView == null)
            view = inflater.inflate(R.layout.listitem_chat_guest, parent, false);
        else
            view = convertView;

        final ImageView img = (ImageView) view.findViewById(R.id.img_dp);
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", sender);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                Picasso.with(context).load(parseUsers.get(0).getString("profilePicture")).into(img);
            }
        });

        ((TextView)view.findViewById(R.id.txt_me_message)).setText(message);
        ((TextView)view.findViewById(R.id.txt_user)).setText(sender);
        return view;
    }

    public String getMessage(){
        return message;
    }

    public String getSender(){
        return sender;
    }
}
