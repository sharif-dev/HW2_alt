package com.example.hw2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ShakeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MainActivity mainActivity = (MainActivity) context;

        float AccelAbs = intent.getFloatExtra(MessageNames.SHAKE_ACCELERATION,0);
        mainActivity.wakeScreenUp(AccelAbs);

    }
}
