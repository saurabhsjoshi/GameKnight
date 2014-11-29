package com.collegecode.gameknight;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toolbar;

import com.collegecode.gameknight.objects.Secrets;
import com.parse.ParseUser;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;

/**
 * Created by saurabh on 14-11-06.
 */
public abstract class BaseActivity extends Activity {

    //SinchClient
    private static SinchClient sinchClient = null;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(getLayoutResource());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null)
            setActionBar(toolbar);
    }

    /* Builds the Sinch Client*/
    public static void buildClient(Context context , SinchClientListener listener){
        sinchClient = Sinch.getSinchClientBuilder().context(context)
                .applicationKey(Secrets.appKey)
                .applicationSecret(Secrets.appSecret)
                .environmentHost("sandbox.sinch.com")
                .userId(ParseUser.getCurrentUser().getUsername())
                .build();
        sinchClient.setSupportMessaging(true);
        sinchClient.setSupportCalling(true);
        sinchClient.setSupportActiveConnectionInBackground(true);
        sinchClient.setSupportPushNotifications(true);
        sinchClient.addSinchClientListener(listener);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.start();
    }

    public static SinchClient getSinchClient(){
        return sinchClient;
    }

    protected abstract int getLayoutResource();

    protected void showBack(boolean show){
        if(getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(show);
    }

    public BaseActivity getBaseActivity(){
        return this;
    }

    protected void setTitle(String title){
        toolbar.setTitle(title);
    }
}
