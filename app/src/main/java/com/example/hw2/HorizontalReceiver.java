package com.example.hw2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class HorizontalReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MainActivity mainActivity = (MainActivity) context;
        double degrees = intent.getDoubleExtra(MessageNames.HOR_DEGREE, 90);
        mainActivity.lockScreen(degrees);
    }
}
