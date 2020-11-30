package com.rescue.hc.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.rescue.hc.ui.activity.SplashActivity;

public class SelfStartReceiver extends BroadcastReceiver {
    public SelfStartReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            Intent i = new Intent(context, SplashActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
