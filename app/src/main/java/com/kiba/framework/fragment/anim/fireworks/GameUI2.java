package com.kiba.framework.fragment.anim.fireworks;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.kiba.framework.R;

public class GameUI2 extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder holder;
    private RenderThread renderThread;
    private boolean isDraw = false;// 控制绘制的开关
    /**
     * 画笔
     */
    private Paint mPaint;
    int pixls2;
    int pix0[];
    int  m_nAppX;
    int  m_nAppY;

    public GameUI2(Context context) {
        super(context);
        holder = this.getHolder();
        holder.addCallback(this);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//打开抗锯齿，用于绘制不规则图像
        mPaint.setColor(context.getResources().getColor(R.color.c_golden_2));
        renderThread = new RenderThread();
        this.post(new Runnable() {
            @Override
            public void run() {
                   m_nAppX =  getWidth() / 2;
                   m_nAppY =getHeight() / 2;
                int pixls = m_nAppX * m_nAppY;
                pixls2 = pixls - m_nAppX * 2;
                pix0 = new int[pixls];
            }
        });
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isDraw = true;
        renderThread.start();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isDraw = false;

    }

    /**
     * 绘制界面的线程
     *
     * @author Administrator
     */
    private class RenderThread extends Thread {
        @Override
        public void run() {
            // 不停绘制界面
            while (isDraw) {
                drawUI();
            }
            super.run();
        }
    }

    /**
     * 界面绘制
     */
    public void drawUI() {
        Canvas canvas = holder.lockCanvas();
        try {
            float[] pos = new float[2];
            pos[0] = 100;
            pos[1] = 100;

            canvas.drawCircle(pos[0], pos[1], 10, mPaint);
            //drawCanvas(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawCanvas(Canvas canvas) {
        // 在 canvas 上绘制需要的图形
    }


}