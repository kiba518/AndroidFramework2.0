package com.kiba.framework.activity.base;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.kiba.framework.fragment.base.KFragment;
import com.kiba.framework.utils.LogUtils;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

/**
 * 页面跳转都通过KActivity 嵌套Fragment来实现,动态替换fragment只需要指定相应的参数。 避免Activity 需要再manifest中注册的问题。
 * 1.管理应用中所有KActivity 实例。 2.管理KActivity 实例和fragment的跳转
 *
 */
public class KActivity extends AppCompatActivity {
    /**
     * 应用中所有KActivity的引用
     */
    private static List<WeakReference<KActivity>> sActivities = new ArrayList<>();
    /**
     * 当前activity的引用
     */
    private WeakReference<KActivity> mCurrentActivity = null;

    private View rootView= null;
    public View getRootView() {
        return rootView;
    }
    /**
     * 返回最上层的activity
     *
     * @return 栈顶Activity
     */
    public static KActivity getTopActivity() {
        if (sActivities != null) {
            int size = sActivities.size();
            if (size >= 1) {
                WeakReference<KActivity> ref = sActivities.get(size - 1);
                if (ref != null) {
                    return ref.get();
                }
            }
        }
        return null;
    }

    /**
     * 广播退出时清理activity列表
     */
    public static void unInit() {
        if (sActivities != null) {
            sActivities.clear();
        }
    }



    //======================页面返回退出==========================//

    /**
     * 是否是主线程
     *
     * @return 是否是主线程
     */
    private boolean isMainThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    /**
     * 结束activity，设置是否显示动画
     *
     * @param activity      KActivity对象
     * @param showAnimation 是否显示动画
     */
    private void finishActivity(KActivity activity, boolean showAnimation) {
        if (activity != null) {
            activity.finish();
            //从activity列表中移除当前实例
            sActivities.remove(mCurrentActivity);

            if (showAnimation) {
                //动画
                int[] animations = null;
                if (animations != null && animations.length >= 4) {
                    overridePendingTransition(animations[2], animations[3]);
                }
            }
        }
    }


    /**
     * 是否位于栈顶
     *
     * @param fragmentTag fragment的tag
     * @return 指定Fragment是否位于栈顶
     */
    public boolean isFragmentTop(String fragmentTag) {
        int size = sActivities.size();
        if (size > 0) {
            WeakReference<KActivity> reference = sActivities.get(size - 1);
            KActivity activity = reference.get();
            if (activity != null && activity == this) {
                FragmentManager manager = activity.getSupportFragmentManager();
                if (manager != null) {
                    int count = manager.getBackStackEntryCount();
                    if (count >= 1) {
                        FragmentManager.BackStackEntry entry = manager.getBackStackEntryAt(count - 1);
                        return fragmentTag.equalsIgnoreCase(entry.getName());
                    }
                }
            }
        }
        return false;
    }

    /**
     * 是否使用过fragment
     * 循环使用过的activity，在其中寻找使用过的fragment
     *
     * @param pageName page的名字
     * @return 是否找到对应Fragment
     */
    public boolean findPage(String pageName) {
        int size = sActivities.size();
        boolean hasFind = false;
        for (int j = size - 1; j >= 0; j--) {
            WeakReference<KActivity> reference = sActivities.get(j);
            if (reference != null) {
                KActivity activity = reference.get();
                if (activity == null) {
                    LogUtils.d("item is null");
                    continue;
                }
                FragmentManager manager = activity.getSupportFragmentManager();
                int count = manager.getBackStackEntryCount();
                for (int i = count - 1; i >= 0; i--) {
                    String name = manager.getBackStackEntryAt(i).getName();
                    if (name != null && name.equalsIgnoreCase(pageName)) {
                        hasFind = true;
                        break;
                    }
                }
                if (hasFind) {
                    break;
                }
            }
        }
        return hasFind;
    }

    //==================startActivity=======================//

    @Override
    public void startActivity(Intent intent) {
        if (intent == null) {
            LogUtils.e("[startActivity failed]: intent == null");
            return;
        }
        if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
            try {
                super.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e(e);
            }
        } else {
            LogUtils.e("[resolveActivity failed]: " + (intent.getComponent() != null ? intent.getComponent() : intent.getAction()) + " do not register in manifest");
        }
    }


    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (intent == null) {
            LogUtils.e("[startActivity failed]: intent == null");
            return;
        }
        if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
            try {
                super.startActivityForResult(intent, requestCode);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e(e);
            }
        } else {
            LogUtils.e("[resolveActivity 失败]: " + (intent.getComponent() != null ? intent.getComponent() : intent.getAction()) + " 未在manifest注册");
        }
    }


    /**
     * 移除无用fragment
     *
     * @param fragmentLists 移除的fragment列表
     */
    public void removeUnlessFragment(List<String> fragmentLists) {
        if (isFinishing()) {
            return;
        }
        FragmentManager manager = getSupportFragmentManager();
        if (manager != null) {
            FragmentTransaction transaction = manager.beginTransaction();
            for (String tag : fragmentLists) {
                Fragment fragment = manager.findFragmentByTag(tag);
                if (fragment != null) {
                    transaction.remove(fragment);
                }
            }
            transaction.commitAllowingStateLoss();
            int count = manager.getBackStackEntryCount();
            if (count == 0) {
                finish();
            }
        }
    }


    //==================生命周期=======================//
    /**
     * @return 获取布局的id
     */
    protected int getLayoutId() {
        return -1;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layoutId = getLayoutId();
        if (layoutId != -1) {
            this.rootView = View.inflate(this, layoutId, null);
            setContentView(this.rootView);
        } else {
            throw new MissingResourceException("未使用getLayoutId()函数初始化view",this.getClass().getName(),"未初始化view");
        }
        if (savedInstanceState != null) {
            //恢复数据，需要用注解SaveWithActivity
            loadActivitySavedData(savedInstanceState);
        }
        //当前activity弱引用
        mCurrentActivity = new WeakReference<>(this);
        //当前activity增加到activity列表中
        sActivities.add(mCurrentActivity);
        //打印所有activity情况
        printAllActivities();

    }


    /**
     * 如果当前activity中只有一个activity，则关闭activity，否则父类处理
     */
    @Override
    public void onBackPressed() {
        if (this.getSupportFragmentManager().getBackStackEntryCount() == 1) {
            this.finishActivity(this, true);

        } else {
            super.onBackPressed();
        }
    }

    /**
     * 打印，调试用
     */
    protected void printAllActivities() {
        LogUtils.d("------------KActivity print all------------activities size:" + sActivities.size());
        for (WeakReference<KActivity> ref : sActivities) {
            if (ref != null) {
                KActivity item = ref.get();
                if (item != null) {
                    LogUtils.d(item.toString());
                }
            }
        }
    }



    @Override
    protected void onStop() {
        if (isFinishing()) {

        }
        super.onStop();
    }

    //==============数据保存=================//

    /**
     * 保存数据
     *
     * @param outState Bundle对象
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Field[] fields = this.getClass().getDeclaredFields();
        Field.setAccessible(fields, true);
        Annotation[] ans;
        for (Field f : fields) {
            ans = f.getDeclaredAnnotations();
            for (Annotation an : ans) {
                if (an instanceof SaveWithActivity) {
                    try {
                        Object o = f.get(this);
                        if (o == null) {
                            continue;
                        }
                        String fieldName = f.getName();
                        if (o instanceof Integer) {
                            outState.putInt(fieldName, f.getInt(this));
                        } else if (o instanceof String) {
                            outState.putString(fieldName, (String) f.get(this));
                        } else if (o instanceof Long) {
                            outState.putLong(fieldName, f.getLong(this));
                        } else if (o instanceof Short) {
                            outState.putShort(fieldName, f.getShort(this));
                        } else if (o instanceof Boolean) {
                            outState.putBoolean(fieldName, f.getBoolean(this));
                        } else if (o instanceof Byte) {
                            outState.putByte(fieldName, f.getByte(this));
                        } else if (o instanceof Character) {
                            outState.putChar(fieldName, f.getChar(this));
                        } else if (o instanceof CharSequence) {
                            outState.putCharSequence(fieldName, (CharSequence) f.get(this));
                        } else if (o instanceof Float) {
                            outState.putFloat(fieldName, f.getFloat(this));
                        } else if (o instanceof Double) {
                            outState.putDouble(fieldName, f.getDouble(this));
                        } else if (o instanceof String[]) {
                            outState.putStringArray(fieldName, (String[]) f.get(this));
                        } else if (o instanceof Parcelable) {
                            outState.putParcelable(fieldName, (Parcelable) f.get(this));
                        } else if (o instanceof Serializable) {
                            outState.putSerializable(fieldName, (Serializable) f.get(this));
                        } else if (o instanceof Bundle) {
                            outState.putBundle(fieldName, (Bundle) f.get(this));
                        }
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        e.printStackTrace();
                        LogUtils.e(e);
                    }
                }
            }
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * 恢复数据
     *
     * @param savedInstanceState Bundle对象
     */
    private void loadActivitySavedData(Bundle savedInstanceState) {
        Field[] fields = this.getClass().getDeclaredFields();
        Field.setAccessible(fields, true);
        Annotation[] ans;
        for (Field f : fields) {
            ans = f.getDeclaredAnnotations();
            for (Annotation an : ans) {
                if (an instanceof SaveWithActivity) {
                    try {
                        String fieldName = f.getName();
                        @SuppressWarnings("rawtypes")
                        Class cls = f.getType();
                        if (cls == int.class || cls == Integer.class) {
                            f.setInt(this, savedInstanceState.getInt(fieldName));
                        } else if (String.class.isAssignableFrom(cls)) {
                            f.set(this, savedInstanceState.getString(fieldName));
                        } else if (Serializable.class.isAssignableFrom(cls)) {
                            f.set(this, savedInstanceState.getSerializable(fieldName));
                        } else if (cls == long.class || cls == Long.class) {
                            f.setLong(this, savedInstanceState.getLong(fieldName));
                        } else if (cls == short.class || cls == Short.class) {
                            f.setShort(this, savedInstanceState.getShort(fieldName));
                        } else if (cls == boolean.class || cls == Boolean.class) {
                            f.setBoolean(this, savedInstanceState.getBoolean(fieldName));
                        } else if (cls == byte.class || cls == Byte.class) {
                            f.setByte(this, savedInstanceState.getByte(fieldName));
                        } else if (cls == char.class || cls == Character.class) {
                            f.setChar(this, savedInstanceState.getChar(fieldName));
                        } else if (CharSequence.class.isAssignableFrom(cls)) {
                            f.set(this, savedInstanceState.getCharSequence(fieldName));
                        } else if (cls == float.class || cls == Float.class) {
                            f.setFloat(this, savedInstanceState.getFloat(fieldName));
                        } else if (cls == double.class || cls == Double.class) {
                            f.setDouble(this, savedInstanceState.getDouble(fieldName));
                        } else if (String[].class.isAssignableFrom(cls)) {
                            f.set(this, savedInstanceState.getStringArray(fieldName));
                        } else if (Parcelable.class.isAssignableFrom(cls)) {
                            f.set(this, savedInstanceState.getParcelable(fieldName));
                        } else if (Bundle.class.isAssignableFrom(cls)) {
                            f.set(this, savedInstanceState.getBundle(fieldName));
                        }
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        e.printStackTrace();
                        LogUtils.e(e);
                    }
                }
            }
        }
    }
    /**
     * 获得当前活动fragment
     *
     * @return 当前活动Fragment对象
     */
    public KFragment getActiveFragment() {
        if (this.isFinishing()) {
            return null;
        }
        FragmentManager manager = this.getSupportFragmentManager();
        if (manager != null) {
            int count = manager.getBackStackEntryCount();
            if (count > 0) {
                String tag = manager.getBackStackEntryAt(count - 1).getName();
                return (KFragment) manager.findFragmentByTag(tag);
            }
        }
        return null;
    }
    /**
     * 注解了该注解数据会被保存
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface SaveWithActivity {

    }

    //========================= 生命周期 ==============================//

    /**
     * 如果fragment中处理了则fragment处理否则activity处理
     *
     * @param keyCode keyCode码
     * @param event   KeyEvent对象
     * @return 是否处理事件
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        KFragment activeFragment = getActiveFragment();
        boolean isHandled = false;
        if (activeFragment != null) {
            isHandled = activeFragment.onKeyDown(keyCode, event);
        }
        return isHandled || super.onKeyDown(keyCode, event);
    }

    /**
     * 如果fragment中处理了则fragment处理否则activity处理
     *
     * @param ev 触摸事件
     * @return 是否处理事件
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        KFragment activeFragment = getActiveFragment();
        boolean isHandled = false;
        if (activeFragment != null) {
            isHandled = activeFragment.dispatchTouchEvent(ev);
        }
        return isHandled || super.dispatchTouchEvent(ev);
    }


}
