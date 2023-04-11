package com.kiba.framework.fragment.anim.ring;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
/*
  ---------------------------------一个画笔画圆圈--------------------------------
 *1.初始化画笔，给画笔设置颜色、不透明度（0-255，0完全透明）、样式为线，抗锯齿，宽度。圆半径
 * 2.重写touch事件：按下时获取圆心，重新初始化一个画笔，开启handler循环。
 * 3.Handler接收信息进行处理
 * 4.flushState()：修改画笔不透明度依次降低，线宽度增加，圆半径增加。
 * 5.invalidate();使得onDraw方法执行，利用canvas.drawCircle(x,y,radius,paint);画圆。
 */

public class MyRring extends View{

    private int cx;//圆环圆心的X坐标

    private int cy;//圆环圆心的Y坐标

    private Paint paint;

    private float radius;//圆环的半径

    private float strokeWidth;//线条的厚度

    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            //重新设置图样式
            flushState();
            //刷新页面，执行onDraw()方法
            invalidate();

            if(paint.getAlpha() !=0){
                handler.sendEmptyMessageDelayed(0, 100);
            }
        };
    };
    private Path mPath;

    public MyRring(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    //------------------------初始化画笔--------------------
    private void initView() {
        //初始化paint
        paint = new Paint();
        paint.setAntiAlias(true); // 抗矩齿
        paint.setColor(Color.RED);
        paint.setStyle(Style.STROKE); //刻画，画线条
        paint.setStrokeWidth(strokeWidth); //设置条线的厚度
        paint.setAlpha(255); //设置透明度 ，0--255  0代表完全透明

        this.radius =0;
        strokeWidth = 0;
        mPath=new Path();
    }

    //-----------------------触摸事件----------------------
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 点击，获得圆环的中心
                cx = (int) event.getX();
                cy = (int) event.getY();
                //初始化画笔
                initView();
                handler.sendEmptyMessage(0);
                break;
        }
        return true;
    }


    //--------------------------刷新状态-------------------------
    private void flushState() {
        this.radius+=10;
        this.strokeWidth = radius/4;
        paint.setStrokeWidth(strokeWidth);

        int nextAlpha = paint.getAlpha()-20;
        if(nextAlpha<=20){
            nextAlpha = 0;
        }
        paint.setAlpha(nextAlpha);
    }

    @Override
    //-------------------------绘制我们的内容----------------------
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制圆环
        drawStar(mPath,cx,cy,radius,radius*2,30);//旋转30°
        canvas.drawPath(mPath, paint);
    }
    //------------------------画五角星------------------------------
    /*
     *cxt:画笔
     *x,y:圆心坐标
     *r:小圆半径
     *R:大圆半径
     *rot:旋转角度
     */
    public void drawStar(Path cxt, float x, float y, float r, float R, float rot){
        //path默认开始点为（0，0），所以要先移动到第一个点上
        cxt.moveTo((float) Math.cos( (18-rot)/180 * Math.PI) * R + x,(float) -Math.sin( (18-rot)/180 * Math.PI) * R + y); //改变接下来操作的起点位置为（x,y）
        for(int i = 0; i < 5; i ++){
            //R：外圆半径
            float f=(float) Math.cos( (18 + i*72 - rot)/180 * Math.PI) * R + x;//Math.cos余弦，返回值在 -1.0 到 1.0 之间；
            float f1=(float) -Math.sin( (18 + i*72 - rot)/180 * Math.PI) * R + y;//Math.sin正弦，返回值在 -1.0 到 1.0 之间；
            cxt.lineTo(f,f1 );

            //r:内圆半径
            float f2=(float) Math.cos( (54 + i*72 - rot)/180 * Math.PI) * r + x;
            float f3=(float) -Math.sin( (54 + i*72 - rot)/180 * Math.PI) * r + y;
            Log.d(f+"   ,"+f1+"","      -" +f2+"   ,"+f3+"");
            cxt.lineTo(f2,f3) ;
        }
        cxt.close();//闭合path，如果path的终点和起始点不是同一个点的话，close()连接这两个点，形成一个封闭的图形
    }
    @Override
    /**
     * 大小的测量按系统的默认规则
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}