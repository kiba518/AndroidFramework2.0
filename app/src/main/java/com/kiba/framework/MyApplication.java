package com.kiba.framework;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDex;
import android.util.Log;

import androidx.viewbinding.BuildConfig;

import com.kiba.framework.utils.DateUtils;
import com.kiba.framework.utils.LogUtils;
import com.kiba.framework.utils.ToastUtils;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class MyApplication extends Application {

    public static Context context;//全局上下文
    public static List<Activity> activityList = new ArrayList<Activity>();//用于存放所有启动的Activity的集合
    public static ApplicationInfo applicationInfo;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {

        Log.d("项目启动", "项目启动: " + DateUtils.getTime());
        super.onCreate();

        context = getApplicationContext();

        PackageManager packageManager = getApplicationContext().getPackageManager();
        try {
            packageManager = getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
            LogUtils.LogHelperError("获取applicationInfo报错", e);
        }

        CrashExceptionHandler.getInstance().init(this);

        //解决4.x运行崩溃的问题
        MultiDex.install(this);
        ToastUtils.init(this);
    }

    private boolean isDebug() {
        return BuildConfig.DEBUG;
    }
    public static String GetProperties(String propertyName) {
        Properties props = new Properties();
        String serviceUrl = null;
        try {
            InputStream in =context.getAssets().open("appConfig.properties");
            props.load(in);
            String vaule = props.getProperty(propertyName);
            serviceUrl = new String(vaule.getBytes("ISO-8859-1"), "gbk");
        } catch (IOException e) {
            e.printStackTrace();
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("错误");
            dialog.setMessage("读取配置文件失败");
            dialog.setCancelable(false);
            removeALLActivity();
        }
        return serviceUrl;
    }
    /**
     * 销毁所有的Activity
     */
    public static void removeALLActivity() {
        //通过循环，把集合中的所有Activity销毁
        for (Activity activity : activityList) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        MyApplication.activityList.clear();
    }

}
