package com.collegecode.gameknight.objects.chatObjects;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by saurabh on 14-11-28.
 */
public interface ChatInterface {
    public int getViewType();
    public View getView(LayoutInflater inflater, View convertView, ViewGroup parent);
}
