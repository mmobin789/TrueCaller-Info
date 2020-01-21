package com.magicbio.truename;

/**
 * Created by Ahmed Bilal on 12/7/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.widget.Toast;

import com.magicbio.truename.services.InComingCallPop;

import java.util.Date;

public class CallReceiver extends PhonecallReceiver {
    MediaRecorder recorder = new MediaRecorder();

    @Override
    protected void onIncomingCallReceived(final Context ctx, String number, Date start) {
        ctx.stopService(InComingCallPop.getIntent(ctx));
        final Intent intent = InComingCallPop.getIntent(ctx);
        intent.putExtra("number", number);
        intent.putExtra("ptype", 0);
        startService(intent, ctx);
        //callRecorder();
        //
    }

    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start) {
        //
    }

    @Override
    protected void onIncomingCallEnded(final Context ctx, String number, Date start, Date end) {
        ctx.stopService(InComingCallPop.getIntent(ctx));
        final Intent i = InComingCallPop.getIntent(ctx);
        i.putExtra("number", number);
        i.putExtra("ptype", 1);
        startService(i, ctx);
        ApplicationControler.setLastCall(number, start, ctx);
        // stopRecorder();

        //
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        ctx.stopService(InComingCallPop.getIntent(ctx));
        final Intent intent = InComingCallPop.getIntent(ctx);
        intent.putExtra("number", number);
        intent.putExtra("ptype", 0);
        startService(intent, ctx);
        Toast.makeText(ctx, number, Toast.LENGTH_LONG).show();
//        callRecorder();
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        ctx.stopService(InComingCallPop.getIntent(ctx));
        final Intent i = InComingCallPop.getIntent(ctx);
        i.putExtra("number", number);
        i.putExtra("ptype", 1);
        startService(i, ctx);
        ApplicationControler.setLastCall(number, start, ctx);
        // stopRecorder();
        //
    }

    @Override
    protected void onMissedCall(final Context ctx, String number, final Date start) {
        ctx.stopService(InComingCallPop.getIntent(ctx));
        final Intent i = InComingCallPop.getIntent(ctx);
        i.putExtra("number", number);
        i.putExtra("ptype", 1);
        startService(i, ctx);
        ApplicationControler.setLastCall(number, start, ctx);
        //stopRecorder();
        //
    }

    public void startService(Intent i, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(i);
        } else {
            context.startService(i);
        }
    }

    public void callRecorder() {
        recorder.reset();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile("TrueName/" + new Date().toString() + ".mp3");
        try {
            recorder.prepare();
        } catch (java.io.IOException e) {
            recorder = null;
            return;
        }
        recorder.start();
    }

    public void stopRecorder() {
        recorder.stop();
    }
}