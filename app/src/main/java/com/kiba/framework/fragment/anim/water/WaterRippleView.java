package com.kiba.framework.fragment.anim.water;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.view.View;

public class WaterRippleView extends View {

    // 波纹颜色
    private static final int WAVE_PAINT_COLOR = 0x880000aa;
    // y = Asin(wx+b)+h
    private static final float STRETCH_FACTOR_A = 20;
    private static final int OFFSET_Y = 0;
    // 第一条水波移动速度
    private static final int TRANSLATE_X_SPEED_ONE = 7;
    // 第二条水波移动速度
    private static final int TRANSLATE_X_SPEED_TWO = 5;
    private float cycleFactorW;

    private int totalWidth, totalHeight;
    private float[] yPositions;
    private float[] resetOneYPositions;
    private float[] resetTwoYPositions;
    private int xOffsetSpeedOne;
    private int xOffsetSpeedTwo;
    private int xOneOffset;
    private int xTwoOffset;

    private Paint wavePaint;
    private DrawFilter drawFilter;

    /**
     *
     *  <com.kiba.framework.fragment.anim.water.WaterRippleView
     *    android:layout_width="fill_parent"
     *    android:layout_height="fill_parent"
     *    />
     */
    public WaterRippleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 将dp转化为px，用于控制不同分辨率上移动速度基本一致
        xOffsetSpeedOne = UiUtils.dipToPx(context, TRANSLATE_X_SPEED_ONE);
        xOffsetSpeedTwo = UiUtils.dipToPx(context, TRANSLATE_X_SPEED_TWO);

        // 初始绘制波纹的画笔
        wavePaint = new Paint();
        // 去除画笔锯齿
        wavePaint.setAntiAlias(true);
        // 设置风格为实线
        wavePaint.setStyle(Style.FILL);
        // 设置画笔颜色
        wavePaint.setColor(WAVE_PAINT_COLOR);
        drawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);//canvas 设置抗锯齿
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 从canvas层面去除绘制时锯齿
        canvas.setDrawFilter(drawFilter);
        resetPositonY();
        for (int i = 0; i < totalWidth; i++) {

            // 减400只是为了控制波纹绘制的y的在屏幕的位置，大家可以改成一个变量，然后动态改变这个变量，从而形成波纹上升下降效果
            // 绘制第一条水波纹
            canvas.drawLine(i, totalHeight - resetOneYPositions[i] - 400, i,
                    totalHeight, wavePaint);

            // 绘制第二条水波纹
            canvas.drawLine(i, totalHeight - resetTwoYPositions[i] - 400, i,
                    totalHeight, wavePaint);
        }

        // 改变两条波纹的移动点
        xOneOffset += xOffsetSpeedOne;
        xTwoOffset += xOffsetSpeedTwo;

        // 如果已经移动到结尾处，则重头记录
        if (xOneOffset >= totalWidth) {
            xOneOffset = 0;
        }
        if (xTwoOffset > totalWidth) {
            xTwoOffset = 0;
        }

        // 引发view重绘，一般可以考虑延迟20-30ms重绘，空出时间片
        postInvalidate();
    }

    private void resetPositonY() {
        // mXOneOffset代表当前第一条水波纹要移动的距离
        int yOneInterval = yPositions.length - xOneOffset;
        // 使用System.arraycopy方式重新填充第一条波纹的数据
        System.arraycopy(yPositions, xOneOffset, resetOneYPositions, 0,
                yOneInterval);
        System.arraycopy(yPositions, 0, resetOneYPositions, yOneInterval,
                xOneOffset);

        int yTwoInterval = yPositions.length - xTwoOffset;
        System.arraycopy(yPositions, xTwoOffset, resetTwoYPositions, 0,
                yTwoInterval);
        System.arraycopy(yPositions, 0, resetTwoYPositions, yTwoInterval,
                xTwoOffset);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 记录下view的宽高
        totalWidth = w;
        totalHeight = h;
        // 用于保存原始波纹的y值
        yPositions = new float[totalWidth];
        // 用于保存波纹一的y值
        resetOneYPositions = new float[totalWidth];
        // 用于保存波纹二的y值
        resetTwoYPositions = new float[totalWidth];

        // 将周期定为view总宽度
        cycleFactorW = (float) (2 * Math.PI / totalWidth);

        // 根据view总宽度得出所有对应的y值
        for (int i = 0; i < totalWidth; i++) {
            yPositions[i] = (float) (STRETCH_FACTOR_A
                    * Math.sin(cycleFactorW * i) + OFFSET_Y);
        }
    }

}

