package com.example.hw2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class HorizontalService extends Service implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mGravity;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity

    private long startTimeinMillis;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTimeinMillis = System.currentTimeMillis();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mGravity = mSensorManager
                .getDefaultSensor(Sensor.TYPE_GRAVITY);
        mSensorManager.registerListener(this, mGravity,
                SensorManager.SENSOR_DELAY_UI, new Handler());

        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        double length = Math.sqrt(x*x + y*y + z*z);
        double z_unit = z/length;
        onHorizontalOrientationChanged(Math.acos(z_unit)*180/Math.PI);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this,mGravity);
    }

    public void onHorizontalOrientationChanged(double angleInDegrees){
//        System.out.println(angleInDegrees);
        Intent intent = new Intent();
        intent.putExtra(MessageNames.HOR_DEGREE, angleInDegrees);
        intent.setAction(MessageNames.HOR_BROADCAST);
        sendBroadcast(intent);
    }
}
