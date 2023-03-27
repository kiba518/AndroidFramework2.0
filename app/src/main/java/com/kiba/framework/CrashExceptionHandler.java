package com.kiba.framework;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CrashExceptionHandler implements Thread.UncaughtExceptionHandler  {
    private static CrashExceptionHandler instance = null;
    private Thread.UncaughtExceptionHandler defaultHandler;
    private Context context;
    // 保存手机信息和异常信息
    private Map<String, String> message = new HashMap<>();

    public static CrashExceptionHandler getInstance() {
        if (instance == null) {
            synchronized (CrashExceptionHandler.class) {
                if (instance == null) {
                    synchronized (CrashExceptionHandler.class) {
                        instance = new CrashExceptionHandler();
                    }
                }
            }
        }
        return instance;
    }

    private CrashExceptionHandler() {
    }

    /**
     * 初始化默认异常捕获
     *
     * @param context context
     */
    public void init(Context context) {
        this.context = context;
        // 获取默认异常处理器
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 将此类设为默认异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (!handleException(e)) {
            // 未经过人为处理,则调用系统默认处理异常,弹出系统强制关闭的对话框
            if (null != defaultHandler) {
                Log.d("未捕获异常", "uncaughtException: 系统自动退出");
                defaultHandler.uncaughtException(t, e);
            }
        } else {
            // 已经人为处理,系统自己退出
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            Process.killProcess(Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 是否人为捕获异常
     *
     * @param e Throwable
     * @return true:已处理 false:未处理
     */
    private boolean handleException(Throwable e) {
        if (e == null) {// 异常是否为空
            return false;
        }
        new Thread() {// 在主线程中弹出提示
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(context, "捕获到异常", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();
        collectErrorMessages();
        saveErrorMessages(e);
        return false;
    }

    /**
     * 1.收集错误信息
     */
    private void collectErrorMessages() {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (null != pi) {
                String versionName = TextUtils.isEmpty(pi.versionName) ? "null" : pi.versionName;
                String versionCode = "" + pi.versionCode;
                message.put("versionName", versionName);
                message.put("versionCode", versionCode);
            }
            // 通过反射拿到错误信息
            Field[] fields = Build.class.getFields();
            if (null != fields  && fields.length > 0) {
                for (Field field : fields) {
                    field.setAccessible(true);
                    try {
                        message.put(field.getName(), field.get(null).toString());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 2.保存错误信息
     *
     * @param e Throwable
     */
    private void saveErrorMessages(Throwable e) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : message.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append("\n");
        }
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        e.printStackTrace(pw);
       /* Throwable cause = e.getCause();
        cause.printStackTrace(pw);*/

        // 循环取出Cause
/*        while (cause != null) {
            cause.printStackTrace(pw);
            cause = e.getCause();
        }*/
        pw.close();

        String result = writer.toString();
        sb.append(result);
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
        String fileName = "ZJDCrashException-log-" + time + "-" + System.currentTimeMillis() + ".txt";
        // 有无SD卡
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory().getPath() + "/ZJDCrashException/";
            File dir = new File(path);
            if (!dir.exists()) dir.mkdirs();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(path + fileName);
                fos.write(sb.toString().getBytes());
            } catch (Exception e1) {
                e1.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }
}

