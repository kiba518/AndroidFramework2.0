package com.kiba.framework.activity.base;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.kiba.framework.MyApplication;
import com.kiba.framework.R;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BaseActivity extends KActivity {
    public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //返回值【/storage/emulated/0/Android/data/com.kiba.framework/files】
    public String FilesPath_External;

    //返回值【/storage/emulated/0】
    public String FilesPath_Internal;
    public Context baseContext;


    Unbinder mUnbinder;
    public FragmentManager fragmentManager = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FilesPath_External = getExternalFilesDir("").getAbsolutePath();
        FilesPath_Internal  = Environment.getExternalStorageDirectory().getPath();
        fragmentManager = this.getSupportFragmentManager();
        super.onCreate(savedInstanceState);
        mUnbinder = ButterKnife.bind(this);
        showPermissions();
        baseContext = this;
        MyApplication.activityList.add(this);
        setStatusBarColor();
    }

    /**
     * 改变状态栏背景色（4.4-5.0的处理）
     */
    void setStatusBarColor() {
        Window window = this.getWindow();
        //取消状态栏透明
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //添加Flag把状态栏设为可绘制模式
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(getResources().getColor(R.color.c_black_1));
        //设置系统状态栏处于可见状态
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        //让view不根据系统窗口来调整自己的布局
        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (null != mChildView) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
            ViewCompat.requestApplyInsets(mChildView);
        }
    }
    /**
     * 改变状态栏背景色（5.0以上的处理：）
     */
    void translucentStatusBar(boolean hideStatusBarBackground) {
        Window window = this.getWindow();
        //添加Flag把状态栏设为可绘制模式
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (hideStatusBarBackground) {
            //如果为全透明模式，取消设置Window半透明的Flag
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //设置状态栏为透明
            window.setStatusBarColor(Color.TRANSPARENT);
            //设置window的状态栏不可见
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            //如果为半透明模式，添加设置Window半透明的Flag
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //设置系统状态栏处于可见状态
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
        //view不根据系统窗口来调整自己的布局
        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (null != mChildView) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
            ViewCompat.requestApplyInsets(mChildView);
        }
    }
    @Override
    protected void onDestroy() {
        mUnbinder.unbind();
        super.onDestroy();
    }

    private static final int PERMISSION_REQ_CODE = 100;

    //请求权限
    public void showPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.CHANGE_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.CHANGE_NETWORK_STATE,
            }, PERMISSION_REQ_CODE);
        } else {
            // PERMISSION_GRANTED
        }
    }

    //Android6.0申请权限的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQ_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // PERMISSION_GRANTED
                }
                break;
            default:
                break;
        }
    }

    public void ShowMessage_Snackbar(View view, String msg) {
        Snackbar.make(view ,msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
    public void ShowMessage_Toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    public void Confirm_original(String msg , ICallback_Boolean callback ){

        if(msg.isEmpty())
        {
            msg="是否确认";
        }
        AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(this);
        alertdialogbuilder.setMessage(msg);
        //设置确定按钮
        alertdialogbuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    callback.Call(true);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        //设置取消按钮
        alertdialogbuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    callback.Call(false);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        final AlertDialog alertdialog1 = alertdialogbuilder.create();
        alertdialog1.show();

    }
    public void Confirm_original(ICallback_Boolean callback){
        String msg="是否确认";
        AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(this);
        alertdialogbuilder.setMessage(msg);
        //设置确定按钮
        alertdialogbuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {
                    callback.Call(true);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        //设置取消按钮
        alertdialogbuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    callback.Call(false);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        final AlertDialog alertdialog1 = alertdialogbuilder.create();
        alertdialog1.show();

    }

    public interface ICallback_Boolean {
        public void Call(Boolean con) throws ClassNotFoundException;
    }


    /**
     * fragment跳转页面
     */
    public void replaceFragment(Fragment fragment) {
        replaceFragment(fragment, fragment.getClass().getName(),null);
    }
    public void replaceFragment(Fragment fragment, @NonNull final FragmentResultListener listener) {
        replaceFragment(fragment, fragment.getClass().getName(),listener);
    }


    public void replaceFragment(Fragment fragment,String name,final FragmentResultListener listener) {
        FragmentTransaction trans = fragmentManager.beginTransaction();
        //fragment.setTargetFragment()
        if (null != listener) {
            fragmentManager.setFragmentResultListener(name, this, listener);
        }
        trans.replace(R.id.fragment_container, fragment, name);//传入fragment的tag
        trans.addToBackStack(null);
        trans.commitAllowingStateLoss();
        //trans.commit();
    }

    /**
     * fragment返回上一页面
     */
    public void FragmentManagerPopBackStack(){
        int entryCount =fragmentManager.getBackStackEntryCount();
        if (entryCount> 1 ){
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            FragmentManagerPopBackStack();
        }
        return true;
    }

    public void StartService(Class<?> cls)
    {
        Intent serverDateService=new Intent(this, cls);
        this.startService(serverDateService);
    }

    public boolean serverIsRunning(String componentName) {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);

        List<ActivityManager.RunningServiceInfo> runningServices
                = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (runningServices.size() <= 0) {
            return false;
        }

        for (ActivityManager.RunningServiceInfo serviceInfo : runningServices) {
            if (componentName.equals(serviceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}

