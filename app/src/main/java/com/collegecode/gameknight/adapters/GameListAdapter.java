package com.collegecode.gameknight.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.collegecode.gameknight.R;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by saurabh on 14-11-26.
 */
public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.ViewHolder> {
    private ArrayList<ParseObject> games;
    private Context context;

    public GameListAdapter(Context context, ArrayList<ParseObject> games){
        this.games = games;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_game, viewGroup, false);
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
