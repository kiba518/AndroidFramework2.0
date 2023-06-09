package com.kiba.framework.activity;
import android.os.Bundle;
import android.view.KeyEvent;
import com.kiba.framework.R;
import com.kiba.framework.activity.base.BaseActivity;
import com.kiba.framework.fragment.anim.fireworks.FireworksFragment;


public class MainActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        replaceFragment(new Flower3Fragment());
        replaceFragment(new FireworksFragment());
//        replaceFragment(new OpenGLFragment());
//        replaceFragment(new WaterFragment());
//        replaceFragment(new MainFragment());
//        replaceFragment(new FlowerFragment());
//        replaceFragment(new ZoomFragment());


    }
    /**
     * 菜单、返回键响应
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //moveTaskToBack(true);
        }
        return true;
    }
}