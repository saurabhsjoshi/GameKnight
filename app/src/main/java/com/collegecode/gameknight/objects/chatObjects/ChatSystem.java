package com.collegecode.gameknight.objects.chatObjects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.collegecode.gameknight.R;

/**
 * Created by saurabh on 14-11-30.
 */
public class ChatSystem implements ChatInterface {
    Context context;
    String message;

    public ChatSystem(Context context, String message){
        this.context = context;
        this.message = message;
    }

    @Override
    public int getViewType() {
        return ChatTypes.CHAT_SYSTEM_MESSAGE.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView, ViewGroup parent) {
        View view;

        if(convertView == null)
            view = inflater.inflate(R.layout.listitem_chat_system, parent, false);
        else
            view = convertView;

        ((TextView)view.findViewById(R.id.txt_me_message)).setText(message);
        return view;
    }
}
