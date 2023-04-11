package com.kiba.framework.fragment.anim.flower;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.kiba.framework.R;
import com.kiba.framework.fragment.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;


public class FlowerFragment extends BaseFragment {
    @BindView(R.id.btn_start)
    Button btn_start;
    // 撒花特效
    @BindView(R.id.rlt_animation_layout)
    RelativeLayout rlt_animation_layout;

    private FlowerAnimation flowerAnimation;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_flower;
    }

    @Override
    protected void onCreate() {
        // 撒花初始化
        flowerAnimation = new FlowerAnimation(this.getContext());
        rlt_animation_layout.addView(flowerAnimation);
    }

    @OnClick(R.id.btn_start)
    public void onStartClick()
    {
        rlt_animation_layout.setVisibility(View.VISIBLE);
        flowerAnimation.startAnimation();
    }

}