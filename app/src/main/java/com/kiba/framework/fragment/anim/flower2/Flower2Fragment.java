package com.kiba.framework.fragment.anim.flower2;

import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.kiba.framework.R;
import com.kiba.framework.fragment.anim.flower.FlowerAnimation;
import com.kiba.framework.fragment.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;


public class Flower2Fragment extends BaseFragment {
    @BindView(R.id.btn_start)
    Button btn_start;
    // 撒花特效
    @BindView(R.id.rlt_animation_layout)
    RelativeLayout rlt_animation_layout;


    DrawYH yh;
    SurfaceView v;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_flower2;
    }

    @Override
    protected void onCreate() {



    }

    @OnClick(R.id.btn_start)
    public void onStartClick() {
        SurfaceView v = new SurfaceView(this.getContext());
        HolderSurfaceView.getInstance().setSurfaceView(v);
        v.setBackgroundResource(R.drawable.btn_add_food);
        rlt_animation_layout.addView(v);
        yh = new DrawYH();
        yh.begin();
        v.setOnTouchListener(yh);
    }

}