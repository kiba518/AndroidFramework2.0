package com.kiba.framework.utils;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import com.kiba.framework.MyApplication;


public class PowerUtils {

    /**
     * 检查屏幕是否点亮
     */
    private boolean checkScreenOn() {
        PowerManager pm = (PowerManager) MyApplication.getContext().getSystemService(MyApplication.context.POWER_SERVICE);
        boolean screen = pm.isScreenOn();
        if(screen) {
            Log.e("屏幕状态","开启"); }
        else
            Log.e("屏幕状态","关闭");
        return  screen;
    }
}
