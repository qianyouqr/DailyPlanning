package com.android.dailyplanning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
//为了接收手机开机发送的广播
public class MyReceiver extends BroadcastReceiver {

    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {  //开机广播，手机开机后系统就会发送该广播
            Intent i = new Intent(context, NotifyService.class);
            context.startService(i);
        }
    }
}
