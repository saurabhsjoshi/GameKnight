package com.collegecode.gameknight;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
 * Created by saurabh on 14-11-30.
 */
public class PrivateChat extends BaseActivity {
    private ListView listView;
    private ChatRoomListAdapter chatRoomListAdapter;
    private ArrayList<ChatInterface> chatList;

    private MessageClient messageClient;
    private boolean isInGame = false;
    private ImageView img_game;
    private EditText txt_message;

    private Context context;
    private String sender;

    private String gameCode;
    private ParseObject game;
    private ProgressDialog progressDialog;

    private CallService callService;
    private Intent callIntent;

    private ServiceConnection callServiceConnection;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_chatroom;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setTitle("Private Chat");
        super.showBack(true);
        gameCode = getIntent().getStringExtra("gameCode");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        progressDialog.show();
        connectToService();

        TextView txt_title, txt_dev;
        ImageButton  btn_send;

        context = this;
        sender = getIntent().getExtras().getString("Sender");

        listView = (ListView) findViewById(R.id.list);
        img_game = (ImageView) findViewById(R.id.img_game);
        txt_title = (TextView) findViewById(R.id.txt_game);
        txt_dev = (TextView) findViewById(R.id.txt_developer);
        btn_send = (ImageButton) findViewById(R.id.btn_send);
        txt_message = (EditText) findViewById(R.id.txt_msg);
        img_game = (ImageView) findViewById(R.id.img_game);

        initMessaging();

        chatList = new ArrayList<ChatInterface>();
        chatRoomListAdapter = new ChatRoomListAdapter(context, chatList);
        listView.setAdapter(chatRoomListAdapter);


        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", sender);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                Picasso.with(context).load(parseUsers.get(0).getString("profilePicture")).into(img_game);
            }
        });

        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Games");
        query2.whereEqualTo("gameCode", gameCode);
        query2.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                game = parseObjects.get(0);
            }
        });

        txt_title.setText(sender);
        txt_dev.setText("");
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserMessage();
            }
        });

        //Set users current Room
        ParseUser.getCurrentUser().put("currentRoom", 0);
        ParseUser.getCurrentUser().saveInBackground();

        sendSystemMessage(ParseUser.getCurrentUser().getUsername() + " has joined the room");
        chatList.add(new ChatSystem(context, "You have joined the room"));
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

    private void sendMessage(String recipients, String message){
        WritableMessage msg = new WritableMessage(recipients, message);
        messageClient.send(msg);
        chatRoomListAdapter.notifyDataSetChanged();
        listView.setSelection(chatRoomListAdapter.getCount() - 1);
    }

    private void sendSystemMessage(String message){
        final Message msg = new Message("0", message);
        sendMessage(sender, GameKnightApi.createJSONMessage(msg));
        chatRoomListAdapter.notifyDataSetChanged();
        listView.setSelection(chatRoomListAdapter.getCount() - 1);
    }

    private void sendUserMessage(){
        String m =  txt_message.getText().toString().trim();
        if(m.length() != 0){
            final Message msg = new Message("1",m);
            txt_message.setText("");
            sendMessage(sender, GameKnightApi.createJSONMessage(msg));
            chatList.add(new ChatMe(context, msg.message));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_private_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_call:
                callService.makeCall();
                return true;
            case R.id.action_play:
                isInGame = true;
                startActivity(getPackageManager().getLaunchIntentForPackage(game.getString("packageName")));
                sendSystemMessage(ParseUser.getCurrentUser().getUsername() + " is in the game");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void connectToService(){
        callServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                CallService.CallBinder binder = (CallService.CallBinder) service;
                callService = binder.getService();
                if(progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                callService.setCallClient(getSinchClient(), sender, context);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        if(callIntent == null){
            callIntent = new Intent(this, CallService.class);
            bindService(callIntent, callServiceConnection, Context.BIND_AUTO_CREATE);
            startService(callIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //User left room
        unbindService(callServiceConnection);
        stopService(callIntent);
        sendSystemMessage(ParseUser.getCurrentUser().getUsername() + " has left the room");
    }

    @Override
    protected void onResume() {
        if(isInGame)
            sendSystemMessage(ParseUser.getCurrentUser().getUsername() + " has returned from the game.");
        super.onResume();
    }
}
