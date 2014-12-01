package com.collegecode.gameknight;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.collegecode.gameknight.adapters.GameListAdapter;
import com.collegecode.gameknight.objects.Constants;
import com.collegecode.gameknight.objects.OnItemClickListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;

import java.util.ArrayList;
import java.util.List;


public class Home extends BaseActivity {
    boolean isActivie = true;

    GameKnightApi gka;
    private RecyclerView mRecyclerView;
    private GameListAdapter adapter;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        gka = new GameKnightApi();

        /* Check if new user */
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences.getBoolean("newUser", true))
        {
            startActivity(new Intent(this, NewUser.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            isActivie = false;
            finish();
        }

        /* User is signed in, create Sinch Client */
        else{
            if(isActivie && getSinchClient() == null)
                buildClient(getApplicationContext() , new SinchClientListener() {
                    @Override
                    public void onClientStarted(SinchClient sinchClient) {
                        Log.i(Constants.logTag, "Client started");
                    }

                    @Override
                    public void onClientStopped(SinchClient sinchClient) {
                    }

                    @Override
                    public void onClientFailed(SinchClient sinchClient, SinchError sinchError) {
                        Log.i(Constants.logTag, sinchError.getMessage());
                    }

                    @Override
                    public void onRegistrationCredentialsRequired(SinchClient sinchClient,
                                                                  final ClientRegistration clientRegistration) {
                        final ProgressDialog progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("Loading...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        gka.getAuthorizedSignatureForUser(new GameKnightApi.onTaskCompleted() {
                            @Override
                            public void onCompleted(String signature, long sequence, Exception e) {
                                clientRegistration.register(signature, sequence);
                                progressDialog.dismiss();
                            }
                        });

                    }

                    @Override
                    public void onLogMessage(int i, String s, String s2) {

                    }
                });

        }

        mRecyclerView = (RecyclerView)findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Games");
        query.orderByAscending("gameTitle");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                adapter = new GameListAdapter(context, new ArrayList<ParseObject>(parseObjects), new OnItemClickListener() {
                    @Override
                    public void onClick(View v,Object clickedViewTag) {
                        ChatRoom.launch(getBaseActivity(),
                                v.findViewById(R.id.img_game),
                                ((ParseObject) clickedViewTag).getString("gameCode"));
                    }
                });
                mRecyclerView.setAdapter(adapter);
            }
        });

        /*if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }*/
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_home;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //getSinchClient().terminate();
    }


}
