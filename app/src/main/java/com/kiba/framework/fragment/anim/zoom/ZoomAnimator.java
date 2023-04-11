package com.kiba.framework.fragment.anim.zoom;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.kiba.framework.R;

public class ZoomAnimator extends LinearLayout {

    public ZoomAnimator(Context context) {
        super(context);
        init();
    }

    public ZoomAnimator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        ImageView img =new ImageView(this.getContext());
        img.setImageDrawable(this.getResources().getDrawable(R.drawable.reddiamond));
        this.addView(img);
        this.setGravity(Gravity.CENTER);

    }

    private void start() {
        //旋转
        setObjectAnimatorOfFloat("rotation", 0, 180, -360, 0);
        //垂直翻转translationX
        setObjectAnimatorOfFloat("translationX", 0, 180, 360, 0);
        //水平翻转translationY
        setObjectAnimatorOfFloat("translationY", 0, -180, 360, 0);
        //横向放大
        setObjectAnimatorOfFloat("scaleX", 0, 1, 2, 1);
        //竖向放大
        setObjectAnimatorOfFloat("scaleY", 0, 1, 2, 1);
    }

    private void setObjectAnimatorOfFloat(String propertyName, float... values) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, propertyName, values);
        objectAnimator.setDuration(1000);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);//无限循环
        objectAnimator.start();
    }

    public void scaleAnimation(){
        //横向放大
        setObjectAnimatorOfFloat("scaleX", 0, 1);
        //竖向放大
        setObjectAnimatorOfFloat("scaleY", 0, 1);
    }

}
