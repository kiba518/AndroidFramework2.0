package com.kiba.framework.fragment.base;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kiba.framework.utils.Utils;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 全局基类BaseFragment
 *
 * @author kiba
 * @since 2023/2/28 下午3:49
 */
public abstract class KFragment extends Fragment {
    /**
     * 所在activity
     */
    private WeakReference<Context> mAttachContext;
    /**
     * 根布局
     */
    protected View mRootView;
    protected Unbinder mUnbinder;


    //================生命周期处理===================//

    /**
     * 将Activity中dispatchTouchEvent在Fragment中实现，
     *
     * @param ev 点击事件
     * @return 是否处理
     */
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            onTouchDownAction(ev);
        }
        return false;
    }

    /**
     * 处理向下点击事件【默认在这里做隐藏输入框的处理，不想处理的话，可以重写该方法】
     *
     * @param ev 点击事件
     */
    protected void onTouchDownAction(MotionEvent ev) {
        if (getActivity() == null) {
            return;
        }
        if (Utils.isShouldHideInput(getActivity().getWindow(), ev)) {
            hideCurrentPageSoftInput();
        }
    }

    /**
     * 隐藏当前页面弹起的输入框【可以重写这里自定义自己隐藏输入框的方法】
     */
    protected void hideCurrentPageSoftInput() {
        if (getActivity() == null) {
            return;
        }
        Utils.hideSoftInput(getActivity().getCurrentFocus());
    }

    //================页面返回===================//

    @Nullable
    public Context getAttachContext() {
        if (mAttachContext != null) {
            return mAttachContext.get();
        }
        return null;
    }
    /**
     * 将Activity中onKeyDown在Fragment中实现，
     *
     * @param keyCode keyCode码
     * @param event   KeyEvent对象
     * @return 是否处理
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    //======================生命周期=======================//

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAttachContext = new WeakReference<>(context);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 布局的资源id
     *
     * @return
     */
    @LayoutRes
    protected abstract int getLayoutId();

    /**
     * 初始化控件
     */
    protected abstract void initViews();

    /**
     * base初始化控件
     */
    protected abstract void init_base();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflateView(inflater, container);
        mUnbinder = ButterKnife.bind(this, mRootView);
        init_base();//先调用baseFragment的init
        initViews();
        return mRootView;
    }

    /**
     * 加载控件
     *
     * @param inflater
     * @param container
     * @return
     */
    protected View inflateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(getLayoutId(), container, false);
    }

    public void popToBack() {
    }

    @Override
    public void onDestroyView() {
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        super.onDestroyView();

    }

    /**
     * 获取根布局
     *
     * @return 根布局
     */
    public View getRootView() {
        return mRootView;
    }



    @Override
    public void onDetach() {
        mAttachContext = null;
        super.onDetach();
    }

    protected <T extends View> T findViewById(int id) {
        return mRootView.findViewById(id);
    }

}