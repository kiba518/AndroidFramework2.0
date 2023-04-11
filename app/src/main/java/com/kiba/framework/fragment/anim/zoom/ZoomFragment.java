package com.kiba.framework.fragment.anim.zoom;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.kiba.framework.R;
import com.kiba.framework.fragment.anim.flower.FlowerAnimation;
import com.kiba.framework.fragment.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;


public class ZoomFragment extends BaseFragment {
    @BindView(R.id.btn_start)
    Button btn_start;

    @BindView(R.id.zoom)
    ZoomAnimator ZoomAnimator;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_zoom;
    }

    @Override
    protected void onCreate() {


    }

    @OnClick(R.id.btn_start)
    public void onStartClick()
    {
        ZoomAnimator.scaleAnimation();
    }

}