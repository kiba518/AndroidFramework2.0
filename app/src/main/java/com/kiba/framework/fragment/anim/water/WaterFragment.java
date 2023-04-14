package com.kiba.framework.fragment.anim.water;

import android.widget.Button;

import com.kiba.framework.R;
import com.kiba.framework.fragment.anim.Shake;
import com.kiba.framework.fragment.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;


public class WaterFragment extends BaseFragment {

    @BindView(R.id.btn_shake)
    Button btn_shake;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_water;
    }

    @Override
    protected void onCreate() {


    }

    @OnClick(R.id.btn_shake)
    public void onShake() {
        Shake.nope(btn_shake);
    }


}