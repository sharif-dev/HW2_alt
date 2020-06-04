package com.example.hw2;

import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MainActivity extends AppCompatActivity {

    SettingData settingData;
    String settingFileName = "settings";

    Switch unlock_on_shake_switch;
    SeekBar shake_sense_seekBar;
    TextView shake_sense_textView;

    Switch lock_on_hor_switch;
    SeekBar lock_hor_angle_seekBar;
    TextView lock_hor_angle_textView;


    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mComponentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter shakeFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        shakeFilter.addAction(MessageNames.SHAKE_BROADCAST);
        this.registerReceiver(new ShakeReceiver(), shakeFilter);

        IntentFilter horFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        horFilter.addAction(MessageNames.HOR_BROADCAST);
        this.registerReceiver(new HorizontalReceiver(), horFilter);

        mDevicePolicyManager = (DevicePolicyManager)getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        mComponentName = new ComponentName(this, LockAdminReceiver.class);


        unlock_on_shake_switch = findViewById(R.id.unlock_on_shake_switch);
        shake_sense_seekBar = findViewById(R.id.shake_sense_seekBar);
        shake_sense_textView = findViewById(R.id.shake_sense_textView);

        lock_on_hor_switch = findViewById(R.id.lock_on_hor_switch);
        lock_hor_angle_seekBar = findViewById(R.id.lock_hor_angle_seekBar);
        lock_hor_angle_textView = findViewById(R.id.lock_hor_angle_textView);

        unlock_on_shake_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingData.set_unlock_on_shake(isChecked);
                System.out.println(isChecked);
                if (isChecked){
                    startService(new Intent(MainActivity.this, ShakeService.class));
                } else {
                    stopService(new Intent(MainActivity.this, ShakeService.class));

                }
            }
        });

        lock_on_hor_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingData.set_lock_on_hor(isChecked);
                if (isChecked){
                    startService(new Intent(MainActivity.this, HorizontalService.class));
                    boolean isAdmin = mDevicePolicyManager.isAdminActive(mComponentName);
                    if (!isAdmin){
                        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
//                        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,description);
                        startActivityForResult(intent, MessageNames.ADMIN_REQUEST);
                    }
                } else {
                    stopService(new Intent(MainActivity.this, HorizontalService.class));
                }

            }
        });

        shake_sense_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float temp = progress;
                temp = (temp*9/100) + 1;
                settingData.set_shake_sensitivity(Math.round(temp));
                shake_sense_textView.setText(String.valueOf(settingData.shake_sensitivity));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        lock_hor_angle_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float temp = progress;
                temp = temp*45/100;
                settingData.set_lock_hor_angle(Math.round(temp));
                lock_hor_angle_textView.setText(String.valueOf(settingData.lock_hor_angle));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Object obj = null;
        try {
            FileInputStream fis = getApplicationContext().openFileInput(settingFileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            obj = ois.readObject();
        } catch (Exception e){
            e.printStackTrace();
        }

        if (obj != null){
            settingData = (SettingData) obj;
        } else {
            settingData = new SettingData();
        }

        setUIValues();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MessageNames.ADMIN_REQUEST){
            if (requestCode == RESULT_CANCELED){
                lock_on_hor_switch.setChecked(false);
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(settingFileName, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(settingData);

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void setUIValues(){
        unlock_on_shake_switch.setChecked(settingData.unlock_on_shake);
        lock_on_hor_switch.setChecked(settingData.lock_on_hor);

        float temp = settingData.shake_sensitivity;
        temp = (temp -1 )*(100/9);

        shake_sense_seekBar.setProgress(Math.round(temp));
        shake_sense_textView.setText(String.valueOf(settingData.shake_sensitivity));

        temp = settingData.lock_hor_angle;
        temp = temp*100/45;

        lock_hor_angle_seekBar.setProgress(Math.round(temp));
        lock_hor_angle_textView.setText(String.valueOf(settingData.lock_hor_angle));

    }


    private int isMyServiceRunning(Class<?> serviceClass) {
        int i = 0;
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                i += 1;
            }
        }
        return i;
    }

    public void wakeScreenUp(float AccelAbs){
        if (AccelAbs*settingData.shake_sensitivity >= 10) {
            PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
            if(!pm.isInteractive()){
                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                        |PowerManager.ACQUIRE_CAUSES_WAKEUP
                        |PowerManager.ON_AFTER_RELEASE,"MyLock:lock");
                wl.acquire(10000);
                PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock:he");

                wl_cpu.acquire(10000);
            }
        }
    }

    public void lockScreen(double angleInDegrees){

        if (angleInDegrees - settingData.lock_hor_angle <= 5) {
            boolean isAdmin = mDevicePolicyManager.isAdminActive(mComponentName);
            if (isAdmin) {
                mDevicePolicyManager.lockNow();
            } else {
                System.out.println("not admin");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(settingFileName, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(settingData);

        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
