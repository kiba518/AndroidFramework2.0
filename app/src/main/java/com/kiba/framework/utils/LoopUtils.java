package com.kiba.framework.utils;

import android.os.Handler;
import android.os.Looper;

public class LoopUtils {
    public static Handler handlerUI = new Handler(Looper.getMainLooper());

    /**
     *
     * @param runnable
     * @param delayMillis 间隔
     */
    public static void loopHandlerThread(Runnable runnable,long delayMillis){

        Runnable runnable1=new Runnable(){
            @Override
            public void run() {
                runnable.run();
                handlerUI.postDelayed(this, delayMillis);
            }

        };
        new Thread(runnable1).run();

    }

    /**
     * 使用线程做循环
     * @param runnable
     */
    public static void LoopThread(Runnable runnable, long delayMillis){
        new Thread(){
            @Override
            public void run() {
                while (true)
                {
                    try {
                        Thread.sleep(delayMillis);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runnable.run();
                }
            }
        }.start();

    }

}
//调用
//LoopHelper.loopHandler(()->{
//etPhoneNumber.setText(df.format(ServerDateService.serverDate));
//Log.i("测试电话号码", etPhoneNumber.getEditValue());
//},1000);
//LoopHelper.loopHandlerThread(()->{
//etPhoneNumber.setText(df.format(ServerDateService.serverDate));
//Log.i("测试电话号码", etPhoneNumber.getEditValue());
//},1000);
//LoopHelper.LoopThread(()->{
//runOnUiThread(()->{
//etPhoneNumber.setText(df.format(ServerDateService.serverDate));
//Log.i("测试电话号码", etPhoneNumber.getEditValue());
//});
//},1000);