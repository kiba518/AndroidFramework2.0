package com.kiba.framework.utils;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;


import com.kiba.framework.MyApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public  class LogUtils {
    public static final int ASSERT = 7;
    public static final int DEBUG = 3;
    public static final int ERROR = 6;
    public static final int INFO = 4;
    public static final int VERBOSE = 2;
    public static final int WARN = 5;
    /**
     * logcat里日志的最大长度.
     */
    private static final int MAX_LOG_LENGTH = 4000;
    //region 写入文件的日志
    public  static void LogHelperError(String Title, Throwable throwable)  {
        Toast.makeText(MyApplication.context, "loghelper捕获到异常" , Toast.LENGTH_SHORT).show();
        String time = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());
        String fileName = "ZJDCrashException-log-" + time + ".txt";
        StringBuilder result=new StringBuilder();
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        throwable.printStackTrace(pw);
        pw.close();
        result.append(writer.toString());
        // 有无SD卡
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory().getPath() + "/ZJDCrashException/";
            File dir = new File(path);
            if (!dir.exists()) dir.mkdirs();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(path + fileName,true);
                byte[] c=new byte[2];
                c[0]=0x0d;
                c[1]=0x0a;//用于输入换行符的字节码
                String t=new String(c);
                String errorStr=t+result.toString();
                fos.write(errorStr.getBytes());
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

    public  static void LogHelperDebug(String Title, String Message)  {
        String time = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());
        String fileName = "ZJDCrashDebug-log-" + time + ".txt";
        StringBuilder result=new StringBuilder();
        result.append(Message+"====="+Title+"====="+new Date().toString());
        // 有无SD卡
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory().getPath() + "/ZJDCrashDebug/";
            File dir = new File(path);
            if (!dir.exists()) dir.mkdirs();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(path + fileName,true);
                byte[] c=new byte[2];
                c[0]=0x0d;
                c[1]=0x0a;//用于输入换行符的字节码
                String t=new String(c);
                String debugStr=t+result.toString();
                fos.write(debugStr.getBytes());
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
    //endregion
    //region 普通日志
    /**
     * 打印出错堆栈信息
     */
    /**
     * 打印出错信息
     *
     * @param msg
     */
    public static void e(String msg) {
        e(msg,null);
    }
    public static void e(Throwable t) {
        log(ERROR,null,t);
    }
    public static void e(String msg, Throwable t) {

        log(ERROR,msg,t);
    }
    public static void d(String message) {

        log(DEBUG, message, null);
    }

    public static void log(int priority,String message, Throwable t) {

        if (message != null && message.length() == 0) {
            message = null;
        }
        if (message == null) {
            if (t == null) {
                return; // Swallow message if it's null and there's no throwable.
            }
            message = getStackTraceString(t);
        } else {
            if (t != null) {
                message += "\n" + getStackTraceString(t);
            }
        }

        log(priority, message);
    }
    /**
     * 使用LogCat输出日志，字符长度超过4000则自动换行.
     *
     * @param priority 优先级
     * @param message  信息
     */
    private static void log(int priority, String message) {
        String tag ="普通日志";
        int subNum = message.length() / MAX_LOG_LENGTH;
        if (subNum > 0) {
            int index = 0;
            for (int i = 0; i < subNum; i++) {
                int lastIndex = index + MAX_LOG_LENGTH;
                String sub = message.substring(index, lastIndex);
                logSub(priority, tag, sub);
                index = lastIndex;
            }
            logSub(priority, tag, message.substring(index, message.length()));
        } else {
            logSub(priority, tag, message);
        }
    }

    /**
     * 使用LogCat输出日志.
     *
     * @param priority 优先级
     * @param tag      标签
     * @param sub      信息
     */
    private static void logSub(int priority, @NonNull String tag, @NonNull String sub) {
        switch (priority) {
            case Log.DEBUG:
                Log.d(tag, sub);
                break;
            case Log.INFO:
                Log.i(tag, sub);
                break;
            case Log.WARN:
                Log.w(tag, sub);
                break;
            case Log.ERROR:
                Log.e(tag, sub);
                break;
            case Log.ASSERT:
                Log.wtf(tag, sub);
                break;
            default:
                Log.v(tag, sub);
                break;
        }
    }
    private static String getStackTraceString(Throwable t) {
        // Don't replace this with Log.getStackTraceString() - it hides
        // UnknownHostException, which is not what we want.
        StringWriter sw = new StringWriter(256);
        PrintWriter pw = new PrintWriter(sw, false);
        t.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    //endregion
}
