package com.collegecode.gameknight.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.collegecode.gameknight.objects.chatObjects.ChatInterface;
import com.collegecode.gameknight.objects.chatObjects.ChatTypes;

import java.util.ArrayList;

/**
 * Created by saurabh on 14-11-28.
 */
public class ChatRoomListAdapter extends ArrayAdapter<ChatInterface>{
    private LayoutInflater mInflater;

    public ChatRoomListAdapter(Context context, ArrayList<ChatInterface> items) {
        super(context, 0, items);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ChatInterface getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getViewTypeCount() {
        return ChatTypes.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).getView(mInflater, convertView,parent);
    }
}
