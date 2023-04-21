package com.kiba.framework.fragment.anim.fireworks;

import android.widget.Button;
import android.widget.RelativeLayout;

import com.kiba.framework.R;
import com.kiba.framework.fragment.base.BaseFragment;

import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;


public class FireworksFragment extends BaseFragment {
    @BindView(R.id.btn_start)
    Button btn_start;
    // 撒花特效
    @BindView(R.id.rlt_animation_layout)
    RelativeLayout rlt_animation_layout;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_flower3;
    }

    @Override
    protected void onCreate() {


    }

    @OnClick(R.id.btn_start)
    public void onStartClick() {
        Fireworks vui = new Fireworks(this.getContext());
        //v.setBackgroundColor(getResources().getColor(R.color.black));
        rlt_animation_layout.addView(vui);
        Random random = new Random();
       int width = this.getRootView().getWidth();
      int height=  this.getRootView().getHeight();
        Runnable runnable1=new Runnable(){
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    vui.shot(random.nextInt(width), random.nextInt(height));
                }
                vui.postDelayed(this, 1000);
            }

        };
        new Thread(runnable1).run();



//        vui.postDelayed(() -> {
//            vui.shot(220,290);
//        }, 1000);


    }

}