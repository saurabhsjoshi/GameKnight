package com.collegecode.gameknight;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

import com.collegecode.gameknight.objects.Constants;
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

    private boolean isMute = false;

    private Context context;
    private String sender;
    private String gameCode;

    Call call;

    //Binder for Activity to use
    private final IBinder callBinder = new CallBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try{
            if(intent.getAction().equals(Constants.ACTION.HANGUP_ACTION)){
                call.hangup();
                stopForeground(true);
            }
            else if(intent.getAction().equals(Constants.ACTION.MUTE_ACTION)){
                AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                if(isMute){
                    isMute = false;
                    am.setMicrophoneMute(false);
                }
                else{
                    isMute = true;
                    am.setMicrophoneMute(true);
                }
                setUpAsForeground();
            }

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

    public void setCallClient(SinchClient sinchClient, String sender, Context context){
        sinchCallClient = sinchClient.getCallClient();
        sinchCallClient.addCallClientListener(this);
        this.context = context;
        this.sender = sender;
    }

    public void makeCall(){
        call = sinchCallClient.callUser(sender);
        call.addCallListener(this);
        Toast.makeText(this, "Calling " + sender, Toast.LENGTH_SHORT).show();
        setUpAsForeground();
    }

    @Override
    public void onIncomingCall(CallClient callClient, final Call call) {
        this.call = call;
        final MediaPlayer player = MediaPlayer.create(this,
                Settings.System.DEFAULT_RINGTONE_URI);
        player.start();
        AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                .setTitle("Incoming call")
                .setCancelable(false)
                .setMessage("Would you like to pick up call from " + sender + "?")
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        player.reset();
                        player.release();
                        call.answer();
                        setUpAsForeground();
                    }
                })
                .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        player.reset();
                        player.release();
                        call.hangup();
                    }
                })
                .setIcon(R.drawable.ic_action_call);

        dialog.create().show();
    }

    /* Will be implemented in future */

    @Override
    public void onCallProgressing(Call call) {
    }

    @Override
    public void onCallEstablished(Call call) {
    }

    @Override
    public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {

    }

    @Override
    public void onCallEnded(Call call) {
        stopForeground(true);
        Toast.makeText(this, "Call ended", Toast.LENGTH_SHORT).show();
    }


    void setUpAsForeground(){
        Intent hangupIntent = new Intent(this, CallService.class);
        hangupIntent.setAction(Constants.ACTION.HANGUP_ACTION);
        PendingIntent pHangupIntent = PendingIntent.getService(this, 0,
                hangupIntent, 0);

        Intent muteIntent = new Intent(this, CallService.class);
        muteIntent.setAction(Constants.ACTION.MUTE_ACTION);
        PendingIntent pMuteIntent = PendingIntent.getService(this, 0, muteIntent,0);

        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle(sender)
                .setTicker("In call")
                .setContentText("Ongoing call")
                .setSmallIcon(R.drawable.ic_action_call)
                .addAction(R.drawable.ic_action_end_call, "End Call", pHangupIntent)
                .setAutoCancel(false)
                .setOngoing(true);

        if(isMute)
            builder.addAction(R.drawable.ic_action_mic, "Unmute",pMuteIntent);
        else
            builder.addAction(R.drawable.ic_action_mic_muted, "Mute",pMuteIntent);

        startForeground(Constants.NOTIFICATION_ID,builder.build());
    }

}
