package com.collegecode.gameknight;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;

import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

/**
 * Created by saurabh on 14-12-01.
 */
public class CallService extends Service implements
        CallClientListener, CallListener{

    //SinchClient instance from Activity
    private CallClient sinchCallClient;

    //Binder for Activity to use
    private final IBinder callBinder = new CallBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try{

        }catch (Exception ignore){}
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return callBinder;
    }

    public class CallBinder extends Binder{
        CallService getService(){
            return CallService.this;
        }
    }

    public void setCallClient(SinchClient sinchClient){
        sinchCallClient = sinchClient.getCallClient();
        sinchCallClient.addCallClientListener(this);
    }

    public void makeCall(String username){
        Call call = sinchCallClient.callUser(username);
        call.addCallListener(this);
    }

    @Override
    public void onIncomingCall(CallClient callClient, Call call) {
        MediaPlayer player = MediaPlayer.create(this,
                Settings.System.DEFAULT_RINGTONE_URI);
        player.start();
    }

    @Override
    public void onCallProgressing(Call call) {

    }

    @Override
    public void onCallEstablished(Call call) {

    }

    @Override
    public void onCallEnded(Call call) {

    }

    @Override
    public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {

    }
}
