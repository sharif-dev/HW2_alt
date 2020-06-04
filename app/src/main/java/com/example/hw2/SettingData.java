package com.example.hw2;

import java.io.Serializable;

public class SettingData implements Serializable {
    public boolean unlock_on_shake = false;
    public int shake_sensitivity = 1;

    public boolean lock_on_hor = false;
    public int lock_hor_angle = 0;

    public SettingData(boolean unlock_on_shake, int shake_sensitivity, boolean lock_on_hor, int lock_hor_angle){
        this.unlock_on_shake = unlock_on_shake;
        this.shake_sensitivity =shake_sensitivity;
        this.lock_on_hor = lock_on_hor;
        this.lock_hor_angle = lock_hor_angle;
    }

    public SettingData(){}

    public void set_lock_hor_angle(int lock_hor_angle) {
        this.lock_hor_angle = lock_hor_angle;
    }

    public void set_shake_sensitivity(int shake_sensitivity) {
        this.shake_sensitivity = shake_sensitivity;
    }

    public void set_lock_on_hor(boolean lock_on_hor) {
        this.lock_on_hor = lock_on_hor;
    }

    public void set_unlock_on_shake(boolean unlock_on_shake) {
        this.unlock_on_shake = unlock_on_shake;
    }
}
