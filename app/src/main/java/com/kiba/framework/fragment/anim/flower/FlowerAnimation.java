package com.kiba.framework.fragment.anim.flower;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;

import com.kiba.framework.R;

/**
 * 撒花 用到的知识点： 1、android属性动画 2、Path路径绘制 3、贝塞尔曲线
 */
public class FlowerAnimation extends View implements AnimatorUpdateListener {

    /**
     * 动画改变的属性值
     */
    private float phase1 = 0f;
    private float phase2 = 0f;
    private float phase3 = 0f;

    /**
     * 小球集合
     */
    private List<Flower> fllowers1 = new ArrayList<Flower>();
    private List<Flower> fllowers2 = new ArrayList<Flower>();
    private List<Flower> fllowers3 = new ArrayList<Flower>();

    /**
     * 动画播放的时间
     */
    private int time = 4000;
    /**
     * 动画间隔
     */
    private int delay = 400;

    int[] ylocations = { -100, -50, -25, 0 };

    /**
     * 资源ID
     */
    public FlowerAnimation(Context context) {
        super(context);
        init(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        this.setLayoutParams(params);
        // this.resId = resId;
    }

    @SuppressLint("ResourceAsColor")
    @SuppressWarnings("deprecation")
    private void init(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = (int) (wm.getDefaultDisplay().getHeight() * 3 / 2f);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);//打开抗锯齿，用于绘制不规则图像
        pathMeasure = new PathMeasure();

        builderFollower(fllowerCount, fllowers1);
        builderFollower(fllowerCount, fllowers2);
        builderFollower(fllowerCount, fllowers3);

    }

    /**
     * 宽度
     */
    private int width = 0;
    /**
     * 高度
     */
    private int height = 0;

    /**
     * 曲线高度个数分割
     */
    private int quadCount = 10;
    /**
     * 曲度
     */
    private float intensity = 0.2f;

    /**
     * 第一批个数
     */
    private int fllowerCount = 4;

    /**
     * 创建花
     */
    private void builderFollower(int count, List<Flower> fllowers) {

        int max = (int) (width * 3 / 4f);
        int min = (int) (width / 4f);
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            int s = random.nextInt(max) % (max - min + 1) + min;
            Path path = new Path();
            CPoint CPoint = new CPoint(s, ylocations[random.nextInt(3)]);
            List<CPoint> points = builderPath(CPoint);
            drawFllowerPath(path, points);
            Flower fllower = new Flower();
            fllower.setPath(path);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.reddiamond);//.copy(Bitmap.Config.ARGB_8888,true);
            Bitmap bitmap2 =  alterSizeBitmap(bitmap,40,40);
            fllower.setResId(bitmap2);
            fllowers.add(fllower);
        }

    }
    public Bitmap alterSizeBitmap(Bitmap bitmap,int newWidth,int newHeight) {

        //计算压缩的比率
        float scaleWidth = ((float) newWidth) / bitmap.getWidth();
        float scaleHeight = ((float) newHeight) / bitmap.getHeight();
        //获取想要缩放的matrix
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        //获取新的bitmap
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

    }


    /**
     * 画曲线
     *
     * @param path
     * @param points
     */
    private void drawFllowerPath(Path path, List<CPoint> points) {
        if (points.size() > 1) {
            for (int j = 0; j < points.size(); j++) {

                CPoint point = points.get(j);

                if (j == 0) {
                    CPoint next = points.get(j + 1);
                    point.dx = ((next.x - point.x) * intensity);
                    point.dy = ((next.y - point.y) * intensity);
                } else if (j == points.size() - 1) {
                    CPoint prev = points.get(j - 1);
                    point.dx = ((point.x - prev.x) * intensity);
                    point.dy = ((point.y - prev.y) * intensity);
                } else {
                    CPoint next = points.get(j + 1);
                    CPoint prev = points.get(j - 1);
                    point.dx = ((next.x - prev.x) * intensity);
                    point.dy = ((next.y - prev.y) * intensity);
                }

                // create the cubic-spline path
                if (j == 0) {
                    path.moveTo(point.x, point.y);
                } else {
                    CPoint prev = points.get(j - 1);
                    path.cubicTo(prev.x + prev.dx, (prev.y + prev.dy), point.x
                            - point.dx, (point.y - point.dy), point.x, point.y);
                }
            }
        }
    }

    /**
     * 曲线摇摆的幅度
     */
    private int range = (int) TypedValue
            .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources()
                    .getDisplayMetrics());

    /**
     * 画路径
     *
     * @param point
     * @return
     */
    private List<CPoint> builderPath(CPoint point) {
        List<CPoint> points = new ArrayList<CPoint>();
        Random random = new Random();
        for (int i = 0; i < quadCount; i++) {
            if (i == 0) {
                points.add(point);
            } else {
                CPoint tmp = new CPoint(0, 0);
                if (random.nextInt(100) % 2 == 0) {
                    tmp.x = point.x + random.nextInt(range);
                } else {
                    tmp.x = point.x - random.nextInt(range);
                }
                tmp.y = (int) (height / (float) quadCount * i);
                points.add(tmp);
            }
        }
        return points;
    }

    /**
     * 画笔
     */
    private Paint mPaint;

    /**
     * 测量路径的坐标位置
     */
    private PathMeasure pathMeasure = null;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawFllower(canvas, fllowers1);
        drawFllower(canvas, fllowers2);
        drawFllower(canvas, fllowers3);

    }

    /**
     * 高度往上偏移量,把开始点移出屏幕顶部
     */
    private float dy = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            40, getResources().getDisplayMetrics());

    /**
     * @param canvas
     * @param fllowers
     */
    private void drawFllower(Canvas canvas, List<Flower> fllowers) {
        for (Flower fllower : fllowers) {
            float[] pos = new float[2];
            // canvas.drawPath(fllower.getPath(),mPaint);//绘制路径
            pathMeasure.setPath(fllower.getPath(), false);
            pathMeasure.getPosTan(height * fllower.getValue(), pos, null);
            //region 绘制圆球
            //mPaint.setColor(this.getResources().getColor(R.color.c_golden_2));
            //mPaint.setStyle(Paint.Style.STROKE);
            //canvas.drawCircle(pos[0], pos[1], 10, mPaint);
            //endregion
            Bitmap bitmap = fllower.getResId();
            canvas.drawBitmap(bitmap, pos[0], pos[1] - dy, null);//绘制图片


        }
    }

    ObjectAnimator mAnimator1;
    ObjectAnimator mAnimator2;
    ObjectAnimator mAnimator3;

    public void startAnimation() {
        if (mAnimator1 != null && mAnimator1.isRunning()) {
            mAnimator1.cancel();
        }
        mAnimator1 = ObjectAnimator.ofFloat(this, "phase1", 0f, 1f);
        mAnimator1.setDuration(time);
        mAnimator1.addUpdateListener(this);

        mAnimator1.start();
        mAnimator1.setInterpolator(new AccelerateInterpolator(1f));

        if (mAnimator2 != null && mAnimator2.isRunning()) {
            mAnimator2.cancel();
        }
        mAnimator2 = ObjectAnimator.ofFloat(this, "phase2", 0f, 1f);
        mAnimator2.setDuration(time);
        mAnimator2.addUpdateListener(this);
        mAnimator2.start();
        mAnimator2.setInterpolator(new AccelerateInterpolator(1f));
        mAnimator2.setStartDelay(delay);

        if (mAnimator3 != null && mAnimator3.isRunning()) {
            mAnimator3.cancel();
        }
        mAnimator3 = ObjectAnimator.ofFloat(this, "phase3", 0f, 1f);
        mAnimator3.setDuration(time);
        mAnimator3.addUpdateListener(this);
        mAnimator3.start();
        mAnimator3.setInterpolator(new AccelerateInterpolator(1f));
        mAnimator3.setStartDelay(delay * 2);
    }

    /**
     * 跟新小球的位置
     *
     * @param value
     * @param fllowers
     */
    private void updateValue(float value, List<Flower> fllowers) {
        for (Flower fllower : fllowers) {
            fllower.setValue(value);
        }
    }

    /**
     * 动画改变回调
     */
    @Override
    public void onAnimationUpdate(ValueAnimator arg0) {

        updateValue(getPhase1(), fllowers1);
        updateValue(getPhase2(), fllowers2);
        updateValue(getPhase3(), fllowers3);
        Log.i(tag, getPhase1() + "");
        invalidate();
    }

    public float getPhase1() {
        return phase1;
    }

    public void setPhase1(float phase1) {
        this.phase1 = phase1;
    }

    public float getPhase2() {
        return phase2;
    }

    public void setPhase2(float phase2) {
        this.phase2 = phase2;
    }

    public float getPhase3() {
        return phase3;
    }

    public void setPhase3(float phase3) {
        this.phase3 = phase3;
    }

    private String tag = this.getClass().getSimpleName();

    private class CPoint {

        public float x = 0f;
        public float y = 0f;

        /**
         * x-axis distance
         */
        public float dx = 0f;

        /**
         * y-axis distance
         */
        public float dy = 0f;

        public CPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

}