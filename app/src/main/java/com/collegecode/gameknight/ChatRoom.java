package com.collegecode.gameknight;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.collegecode.gameknight.adapters.ChatRoomListAdapter;
import com.collegecode.gameknight.objects.chatObjects.ChatGuest;
import com.collegecode.gameknight.objects.chatObjects.ChatInterface;
import com.collegecode.gameknight.objects.chatObjects.ChatMe;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saurabh on 14-11-27.
 */
public class ChatRoom extends BaseActivity {
    public static final String EXTRA_IMAGE = "ChatRoom:image";

    private ListView listView;
    private ChatRoomListAdapter chatRoomListAdapter;
    private ArrayList<ChatInterface> chatList;

    private ImageView img_game;
    private TextView txt_title;
    private TextView txt_dev;
    private Context context;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_chatroom;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        super.setTitle("Chatroom");

        listView = (ListView) findViewById(R.id.list);

        chatList = new ArrayList<ChatInterface>();
        chatList.add(new ChatMe(context, "sup" ));
        chatList.add(new ChatMe(context, "sup" ));
        chatList.add(new ChatGuest(context, "sup" ));
        chatList.add(new ChatMe(context, "sup" ));
        chatList.add(new ChatGuest(context, "sup" ));
        chatRoomListAdapter = new ChatRoomListAdapter(context, chatList);
        listView.setAdapter(chatRoomListAdapter);


        String gameCode = getIntent().getExtras().getString("gameCode");

        if(getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);

        img_game = (ImageView) findViewById(R.id.img_game);
        txt_title = (TextView) findViewById(R.id.txt_game);
        txt_dev = (TextView) findViewById(R.id.txt_developer);

        //Set users current Room
        ParseUser.getCurrentUser().put("currentRoom", gameCode);
        ParseUser.getCurrentUser().saveInBackground();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Games");
        query.whereEqualTo("gameCode", gameCode);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                ParseObject game = parseObjects.get(0);
                txt_title.setText(game.getString("gameTitle"));
                txt_dev.setText(game.getString("developer"));
                Picasso.with(context)
                        .load(game.getString("gameImage"))
                        .fit()
                        .into(img_game);
            }
        });
        ViewCompat.setTransitionName(img_game, EXTRA_IMAGE);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void launch(BaseActivity activity, View transitionView, String gameId) {
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity, transitionView, EXTRA_IMAGE);
        Intent intent = new Intent(activity, ChatRoom.class);
        intent.putExtra("gameCode",gameId);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }


}
