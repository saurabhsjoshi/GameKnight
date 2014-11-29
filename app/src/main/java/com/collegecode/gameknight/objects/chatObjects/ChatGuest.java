package com.collegecode.gameknight.objects.chatObjects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.collegecode.gameknight.R;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

/**
 * Created by saurabh on 14-11-29.
 */
public class ChatGuest implements ChatInterface {
    Context context;
    String message;

    public ChatGuest(Context context, String message){
        this.context = context;
        this.message = message;
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

        ImageView img = (ImageView) view.findViewById(R.id.img_dp);
        Picasso.with(context).load(ParseUser.getCurrentUser().getString("profilePicture")).into(img);
        ((TextView)view.findViewById(R.id.txt_me_message)).setText(message);
        return view;
    }
}
