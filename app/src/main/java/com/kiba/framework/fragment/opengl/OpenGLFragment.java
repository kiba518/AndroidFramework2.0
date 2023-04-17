package com.kiba.framework.fragment.opengl;

import android.widget.TextView;

import com.kiba.framework.R;

import com.kiba.framework.fragment.base.BaseFragment;

import butterknife.BindView;


public class OpenGLFragment extends BaseFragment {

    @BindView(R.id.tvtext)
    TextView tvtext;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_opengl;
    }

    @Override
    protected void onCreate() {

        int i = 1;
        tvtext.setText("5654654654");
    }


}