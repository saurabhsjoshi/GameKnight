package com.collegecode.gameknight.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.collegecode.gameknight.R;
import com.collegecode.gameknight.objects.OnItemClickListener;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by saurabh on 14-11-26.
 */
public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.ViewHolder> implements View.OnClickListener {
    private ArrayList<ParseObject> games;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public GameListAdapter(Context context, ArrayList<ParseObject> games, OnItemClickListener onItemClickListener){
        this.games = games;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        onItemClickListener.onClick(v, v.getTag());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_game, viewGroup, false);
        v.setOnClickListener(this);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        ParseObject game = games.get(i);
        viewHolder.gameTitle.setText(game.getString("gameTitle"));

        Picasso.with(context)
                .load(game.getString("gameImage"))
                .fit()
                .into(viewHolder.gameImage);

        viewHolder.itemView.setTag(game);

    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView gameTitle;
        public ImageView gameImage;

        public ViewHolder(View itemView) {
            super(itemView);
            gameTitle = (TextView)  itemView.findViewById(R.id.lbl_game);
            gameImage = (ImageView) itemView.findViewById(R.id.img_game);
        }
    }
}
