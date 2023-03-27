package com.kiba.framework.fragment.base;


import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;

import com.google.android.material.snackbar.Snackbar;
import com.kiba.framework.activity.base.BaseActivity;
import com.kiba.framework.utils.KeyboardUtils;


import java.text.SimpleDateFormat;

public abstract class BaseFragment extends KFragment {
    public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //返回值【/storage/emulated/0/Android/data/com.kiba.framework/files】
    public String FilesPath_External;

    //返回值【/storage/emulated/0】
    public String FilesPath_Internal;
    public Context context;


    @Override
    protected void init_base() {
        FilesPath_External = this.getActivity().getExternalFilesDir("").getAbsolutePath();
        FilesPath_Internal = Environment.getExternalStorageDirectory().getPath();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        context = this.getContext();
    }

    /**
     * 关闭当前fragment所在的activity,请谨慎使用！！！
     */
    protected void finishActivity() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            getActivity().finish();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        //屏幕旋转时刷新一下title
        super.onConfigurationChanged(newConfig);
        ViewGroup root = (ViewGroup) getRootView();
//        if (root.getChildAt(0) instanceof TitleBar) {
//            root.removeViewAt(0);
//            initTitle();
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void hideCurrentPageSoftInput() {
        if (getActivity() == null) {
            return;
        }
        // 记住，要在xml的父布局加上android:focusable="true" 和 android:focusableInTouchMode="true"
        KeyboardUtils.hideSoftInputClearFocus(getActivity().getCurrentFocus());
    }



    public void ShowMessage_Snackbar(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    public void ShowMessage_Toast(String msg) {
        Toast.makeText(this.getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public void Confirm_original(String msg, ICallback_Boolean callback) {

        if (msg.isEmpty()) {
            msg = "是否确认";
        }
        AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(this.getActivity());
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

    public void Confirm_original(ICallback_Boolean callback) {
        String msg = "是否确认";
        AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(this.getActivity());
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

    public void replaceFragment(Fragment fragment) {
        BaseActivity ba = (BaseActivity) this.getActivity();
        ba.replaceFragment(fragment);
    }

    public void replaceFragment(Fragment fragment, @NonNull final FragmentResultListener listener) {
        BaseActivity ba = (BaseActivity) this.getActivity();
        ba.replaceFragment(fragment, listener);
    }
    public FragmentManager getfragmentManager()
    {
        BaseActivity ba = (BaseActivity) this.getActivity();
        return ba.fragmentManager;
    }
    public void setFragmentResult(Bundle bundle)
    {
        getfragmentManager().setFragmentResult(this.getClass().getName(), bundle);
    }

    /**
     * 返回上一个fragment
     */
    @Override
    public void popToBack() {
        BaseActivity ba = (BaseActivity) this.getActivity();
        ba.FragmentManagerPopBackStack();
    }
}

