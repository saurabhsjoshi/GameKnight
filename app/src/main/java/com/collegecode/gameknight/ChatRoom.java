package com.collegecode.gameknight;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.collegecode.gameknight.adapters.ChatRoomListAdapter;
import com.collegecode.gameknight.objects.Constants;
import com.collegecode.gameknight.objects.Message;
import com.collegecode.gameknight.objects.chatObjects.ChatGuest;
import com.collegecode.gameknight.objects.chatObjects.ChatInterface;
import com.collegecode.gameknight.objects.chatObjects.ChatMe;
import com.collegecode.gameknight.objects.chatObjects.ChatSystem;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;
import com.sinch.android.rtc.messaging.WritableMessage;
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

    private MessageClient messageClient;

    private ImageView img_game;
    private TextView txt_title;
    private TextView txt_dev;
    private EditText txt_message;
    private ImageButton btn_send;

    private Context context;
    private String gameCode;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_chatroom;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        super.setTitle("Chatroom");
        initMessaging();
        listView = (ListView) findViewById(R.id.list);
        gameCode = getIntent().getExtras().getString("gameCode");

        chatList = new ArrayList<ChatInterface>();
        chatRoomListAdapter = new ChatRoomListAdapter(context, chatList);
        listView.setAdapter(chatRoomListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                try{
                    if(listView.getItemAtPosition(position) instanceof ChatGuest){
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setItems(R.array.array_dialog, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(which == 0){
                                    Intent i = new Intent(context, PrivateChat.class);
                                    i.putExtra("Sender",
                                            ((ChatGuest) listView.getItemAtPosition(position)).getSender());
                                    startActivity(i);
                                }
                                else{
                                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                    android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text",
                                            ((ChatGuest) listView.getItemAtPosition(position)).getMessage());
                                    clipboard.setPrimaryClip(clip);
                                    Toast.makeText(context, "Message copied", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        builder.show();
                    }
                }catch (Exception ignore){}
            }
        });

        sendSystemMessage(ParseUser.getCurrentUser().getUsername() + " has joined the room");
        chatList.add(new ChatSystem(context, "You have joined the room"));


        if(getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);

        img_game = (ImageView) findViewById(R.id.img_game);
        txt_title = (TextView) findViewById(R.id.txt_game);
        txt_dev = (TextView) findViewById(R.id.txt_developer);
        btn_send = (ImageButton) findViewById(R.id.btn_send);
        txt_message = (EditText) findViewById(R.id.txt_msg);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserMessage();
            }
        });
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

    private void sendSystemMessage(String message){
        final Message msg = new Message("0", message);
        GameKnightApi.getAllUsersInRoom(gameCode, new GameKnightApi.onParseRequestCompleted() {
            @Override
            public void onCompleted(Object result, ParseException exception) {
                sendMessage(((ArrayList<String>)result),GameKnightApi.createJSONMessage(msg));
            }
        });

    }

    private void sendUserMessage(){
        String m =  txt_message.getText().toString().trim();
        if(m.length() != 0){
            final Message msg = new Message("1",m);
            txt_message.setText("");
            GameKnightApi.getAllUsersInRoom(gameCode, new GameKnightApi.onParseRequestCompleted() {
                @Override
                public void onCompleted(Object result, ParseException exception) {
                    sendMessage(((ArrayList<String>)result),GameKnightApi.createJSONMessage(msg));
                    chatList.add(new ChatMe(context, msg.message));
                }
            });
        }
    }

    private void initMessaging(){
        messageClient = getSinchClient().getMessageClient();
        messageClient.addMessageClientListener(new MessageClientListener() {
            @Override
            public void onIncomingMessage(MessageClient messageClient, com.sinch.android.rtc.messaging.Message message) {
                Message m = GameKnightApi.getMessageFromJSON(message.getTextBody());
                if(m.type.equals("0"))
                    chatList.add(new ChatSystem(context,m.message));
                else if(m.type.equals("1"))
                    chatList.add(new ChatGuest(context,message.getSenderId(),m.message));
                chatRoomListAdapter.notifyDataSetChanged();
                listView.setSelection(chatRoomListAdapter.getCount() - 1);
            }

            @Override
            public void onMessageSent(MessageClient messageClient, com.sinch.android.rtc.messaging.Message message, String s) {
                Log.i(Constants.logTag, "Sent!!");
            }

            @Override
            public void onMessageFailed(MessageClient messageClient, com.sinch.android.rtc.messaging.Message message, MessageFailureInfo messageFailureInfo) {
                Log.e(Constants.logTag, messageFailureInfo.getSinchError().getMessage());
            }

            @Override
            public void onMessageDelivered(MessageClient messageClient, MessageDeliveryInfo messageDeliveryInfo) {
            }

            @Override
            public void onShouldSendPushData(MessageClient messageClient, com.sinch.android.rtc.messaging.Message message, List<PushPair> pushPairs) {
            }
        });
    }

    private void sendMessage(List<String> recipients, String message){
        if(recipients.size() != 0){
            WritableMessage msg = new WritableMessage(recipients, message);
            messageClient.send(msg);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //User left room
        sendSystemMessage(ParseUser.getCurrentUser().getUsername() + " has left the room");
        ParseUser.getCurrentUser().put("currentRoom", "0");
        ParseUser.getCurrentUser().saveInBackground();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //User paused activity
        ParseUser.getCurrentUser().put("currentRoom", "0");
        ParseUser.getCurrentUser().saveInBackground();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //User back in activity
        ParseUser.getCurrentUser().put("currentRoom", gameCode);
        ParseUser.getCurrentUser().saveInBackground();
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
