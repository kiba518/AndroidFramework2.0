package com.kiba.framework.fragment.anim.fireworks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.kiba.framework.R;

import java.util.Random;

public class Fireworks extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder holder;
    private RenderThread renderThread;


    private int m_nAppX;
    private int m_nAppY;
    private int m_centerX;
    private int m_centerY;
    private int m_mouseX;
    private int m_mouseY;
    private int m_sleepTime;
    private boolean isError;
    private boolean m_isPaintFinished;
    boolean isRunning;

    Thread runner;
    int pix0[];
    Bitmap offImage;

    int pixls;
    int pixls2;
    int bit_max = 100;
    int bit_sound = 2;

    boolean isInitialized;
    Random rand;
    int bits;
    double bit_px[];
    double bit_py[];
    double bit_vx[];
    double bit_vy[];
    int bit_sx[];
    int bit_sy[];
    int bit_l[];
    int bit_f[];
    int bit_p[];
    int bit_c[];
    int ru;
    int rv;
    Canvas mCanvas;

    /**
     * 画笔
     */
    private Paint mPaint;
    private Rect drawRect = new Rect(0, 0, 0, 0);
    public Fireworks(Context context) {
        super(context);
        holder = this.getHolder();
        holder.addCallback(this);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//打开抗锯齿，用于绘制不规则图像
        mPaint.setColor(context.getResources().getColor(R.color.c_golden_2));
        renderThread = new RenderThread();

        m_mouseX = 0;
        m_mouseY = 0;
        m_sleepTime = 5;
        isError = false;
        isInitialized = false;
        rand = new Random();
        bits = 10000;
        bit_px = new double[bits];
        bit_py = new double[bits];
        bit_vx = new double[bits];
        bit_vy = new double[bits];
        bit_sx = new int[bits];
        bit_sy = new int[bits];
        bit_l = new int[bits];
        bit_f = new int[bits];
        bit_p = new int[bits];
        bit_c = new int[bits];
        ru = 50;
        rv = 50;
        this.post(new Runnable() {
            @Override
            public void run() {

                setRect(new Rect(0, 0, getWidth(), getHeight()));
                if (getRect().width() > 10 && getRect().height() > 10)
                    init();
            }
        });
    }
    public void init() {
        m_nAppX = this.getRect().width() / 2;
        m_nAppY = this.getRect().height() / 2;
        drawRect = new Rect(0, 0, m_nAppX, m_nAppY);
        m_centerX = m_nAppX / 2;
        m_centerY = m_nAppY / 2;
        m_mouseX = m_centerX;
        m_mouseY = m_centerY;

        pixls = m_nAppX * m_nAppY;
        pixls2 = pixls - m_nAppX * 2;
        pix0 = new int[pixls];
        offImage = Bitmap.createBitmap(m_nAppX, m_nAppY,
                Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas();
        mCanvas.setBitmap(offImage);
        for (int i = 0; i < pixls; i++)
            pix0[i] = 0xff000000;

        for (int j = 0; j < bits; j++)
            bit_f[j] = 0;

        isInitialized = true;
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        renderThread.start();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    protected enum DrawStatus {
        NoWork, Drawing, Ending, Destroyed
    }

    ;
    protected DrawStatus mStatus = DrawStatus.NoWork;
    protected long starttime;
    // 每帧时间 60帧附近 第一次计算前使用 毫秒
    private float perframe = 16;

    private int count = 0;

    // 每隔多长时间测试一次帧时间
    private int mRefreshSpeed = 12;

    // 设定要求多长时间每帧 24帧每秒
    private float mFPS = 1000 / 12;
    private float sleep = mFPS;
    protected long mBegintime;
    /**
     * 画布矩形，锁定Canvas时（lockCanvas），使用该矩形固定画面大小
     */
    protected Rect mRect = new Rect(0, 0, 0, 0);

    public void setRect(Rect r) {
        mRect.set(r);
    }

    public Rect getRect() {
        return mRect;
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

            mStatus = DrawStatus.Drawing;
            starttime = System.currentTimeMillis();
            mBegintime = System.currentTimeMillis();
            // 建立两次缓存
            clear();
            clear();
            while (mStatus == DrawStatus.Drawing) {
                synchronized (holder) {
                    Canvas canvas = holder.lockCanvas(getRect());
                    try {
                        if (canvas != null) {
                            clear(canvas);
                            doWork(canvas);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (canvas != null)
                            holder.unlockCanvasAndPost(canvas);
                    }
                    change();
                }
                calculatePerframe();

            }
            if (mStatus != DrawStatus.Destroyed) {
                clear();
            }
            renderThread = null;
            endWork();
        }

    }
    public void endWork() {
        mStatus = DrawStatus.Ending;
    }
    protected void calculatePerframe() {
        try {
            Thread.sleep(m_sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    protected void change() {
        if (isInitialized) {
            for (int j = 0; j < pixls2; j++) {
                int k = pix0[j];
                int l = pix0[j + 1];
                int i1 = pix0[j + m_nAppX];
                int j1 = pix0[j + m_nAppX + 1];
                int i = (k & 0xff0000) >> 16;
                int k1 = ((((l & 0xff0000) >> 16) - i) * ru >> 8) + i;
                i = (k & 0xff00) >> 8;
                int l1 = ((((l & 0xff00) >> 8) - i) * ru >> 8) + i;
                i = k & 0xff;
                int i2 = (((l & 0xff) - i) * ru >> 8) + i;
                i = (i1 & 0xff0000) >> 16;
                int j2 = ((((j1 & 0xff0000) >> 16) - i) * ru >> 8) + i;
                i = (i1 & 0xff00) >> 8;
                int k2 = ((((j1 & 0xff00) >> 8) - i) * ru >> 8) + i;
                i = i1 & 0xff;
                int l2 = (((j1 & 0xff) - i) * ru >> 8) + i;
                int i3 = ((j2 - k1) * rv >> 8) + k1;
                int j3 = ((k2 - l1) * rv >> 8) + l1;
                int k3 = ((l2 - i2) * rv >> 8) + i2;
                pix0[j] = i3 << 16 | j3 << 8 | k3 | 0x77000000;

            }

            rend();

            mCanvas.drawBitmap(pix0, 0, m_nAppX, 0, 0, m_nAppX, m_nAppY, true,
                    mPaint);

//            Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
//            bitmap.copyPixelsFromBuffer(IntBuffer.wrap(pix0));
//            Rect rect1 = new Rect(0, 0, m_nAppX, m_nAppY);
//            Rect rectF = new Rect(0, 0, m_nAppX,m_nAppY);
//            mCanvas.drawBitmap(bitmap, rect1, rectF, mPaint);

        }
    }
    void rend() {
        boolean flag = false;
        boolean flag1 = false;
        boolean flag2 = false;
        for (int k = 0; k < bits; k++)
            switch (bit_f[k]) {
                default:
                    break;

                case 1: // '\001'
                    bit_vy[k] += rand.nextDouble() / 50D;
                    bit_px[k] += bit_vx[k];
                    bit_py[k] += bit_vy[k];
                    bit_l[k]--;
                    if (bit_l[k] == 0 || bit_px[k] < 0.0D || bit_py[k] < 0.0D
                            || bit_px[k] > (double) m_nAppX
                            || bit_py[k] > (double) (m_nAppY - 3)) {
                        bit_c[k] = 0xff000000;
                        bit_f[k] = 0;
                    } else if (bit_p[k] == 0) {
                        if ((int) (rand.nextDouble() * 2D) == 0)
                            bit_set((int) bit_px[k], (int) bit_py[k], -1);
                    } else {
                        bit_set((int) bit_px[k], (int) bit_py[k], bit_c[k]);
                    }
                    break;

                case 2: // '\002'
                    bit_sy[k] -= 5;
                    if ((double) bit_sy[k] <= bit_py[k]) {
                        bit_f[k] = 1;
                        flag2 = true;
                    }
                    if ((int) (rand.nextDouble() * 20D) == 0) {
                        int i = (int) (rand.nextDouble() * 2D);
                        int j = (int) (rand.nextDouble() * 5D);
                        bit_set(bit_sx[k] + i, bit_sy[k] + j, -1);
                    }
                    break;
            }

//        if (flag2 && bit_sound > 0)
//            // sm.playInMediaPlayer(R.raw.firework, null);
//            soundPool.play(id_sound1, 0);
    }

    void bit_set(int i, int j, int k) {
        try {
            int l = i + j * m_nAppX;
            pix0[l] = k;
        }
        catch (Exception ex){
            Log.d("bit set","i"+i +" j"+j+" k"+k+" l"+(i + j * m_nAppX));
        }
    }
    protected void doWork(Canvas canvas) {
        if (offImage != null)
            canvas.drawBitmap(offImage, drawRect, this.getRect(), mPaint);//剪切原图的左上角1/4，然后全屏绘制

    }
    protected void clear() {
        synchronized (holder) {
            Canvas canvas = this.holder.lockCanvas();
            try {
                clear(canvas);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null)
                    this.holder.unlockCanvasAndPost(canvas);
            }
        }
    }
    /**
     * 清空
     *
     * @param canvas
     */
    protected void clear(Canvas canvas) {
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    }

    public boolean shot( int x, int y) {


        x = x / 2;
        y = y / 2;
        if (x > m_nAppX)
            x = m_nAppX;
        if (y > m_nAppY)
            y = m_nAppY;
        if (x < 0)
            x = 0;
        if (y < 0)
            y = 0;
        this.dot(x, y);
        m_mouseX = x;
        m_mouseY = y;
        return true;
    }
    public void dot(int x, int y) {
        m_mouseX = x;
        m_mouseY = y;
        int k = (int) (rand.nextDouble() * 256D);
        int l = (int) (rand.nextDouble() * 256D);
        int i1 = (int) (rand.nextDouble() * 256D);
        int j1 = k << 16 | l << 8 | i1 | 0xff000000;
        int k1 = 0;
        for (int l1 = 0; l1 < bits; l1++) {
            if (bit_f[l1] != 0)
                continue;
            bit_px[l1] = m_mouseX;
            bit_py[l1] = m_mouseY;
            double d = rand.nextDouble() * 6.2800000000000002D;
            double d1 = rand.nextDouble();
            bit_vx[l1] = Math.sin(d) * d1;
            bit_vy[l1] = Math.cos(d) * d1;
            bit_l[l1] = (int) (rand.nextDouble() * 100D) + 100;
            bit_p[l1] = (int) (rand.nextDouble() * 3D);
            bit_c[l1] = j1;
            bit_sx[l1] = m_mouseX;
            bit_sy[l1] = m_nAppY - 5;
            bit_f[l1] = 2;
            if (++k1 == bit_max)
                break;
        }
    }
}