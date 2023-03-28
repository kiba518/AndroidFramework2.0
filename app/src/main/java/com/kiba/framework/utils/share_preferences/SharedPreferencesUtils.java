package com.kiba.framework.utils.share_preferences;

import android.content.Context;
import com.kiba.framework.MyApplication;


import java.lang.reflect.Type;

/**
 * 应用信息存储类，使用SharedPreferences管理
 *
 * @author kiba518
 * @since 2023/01/11 下午5:16
 */
public class SharedPreferencesUtils extends BaseSPUtil {

    private static volatile SharedPreferencesUtils sInstance = null;

    private SharedPreferencesUtils(Context context) {
        super(context);
    }

    public static Context getContext() {
        return MyApplication.getContext();
    }
    /**
     * 获取单例
     *
     * @return
     */
    public static SharedPreferencesUtils getInstance() {
        if (sInstance == null) {
            synchronized (SharedPreferencesUtils.class) {
                if (sInstance == null) {
                    sInstance = new SharedPreferencesUtils(getContext());
                }
            }
        }
        return sInstance;
    }


    private final String IS_FIRST_OPEN_KEY = "is_first_open_key";

    private final String IS_AGREE_PRIVACY_KEY = "is_agree_privacy_key";

    private final String IS_LOGIN_KEY = "is_login_key";

    private final String USER_INFO = "user_info";

    private final String COMMISSION_USER_INFO = "Commission_user_info";

    /**
     * 是否是第一次启动
     */
    public boolean isFirstOpen() {
        return getBoolean(IS_FIRST_OPEN_KEY, true);
    }

    /**
     * 设置是否是第一次启动
     */
    public void setIsFirstOpen(boolean isFirstOpen) {
        putBoolean(IS_FIRST_OPEN_KEY, isFirstOpen);
    }

    /**
     * @return 是否同意隐私政策
     */
    public boolean isAgreePrivacy() {
        return getBoolean(IS_AGREE_PRIVACY_KEY, false);
    }

    public void setIsAgreePrivacy(boolean isAgreePrivacy) {
        putBoolean(IS_AGREE_PRIVACY_KEY, isAgreePrivacy);
    }

    public boolean hasToken() {
        String token = getString(IS_LOGIN_KEY,"" );
        if(token!="")
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean setLogin(String token) {
        putString(IS_LOGIN_KEY, token);
        return true;
    }
    public String getToken() {
        String token = getString(IS_LOGIN_KEY, "");
        return token;
    }

    public boolean setUserInfo(Object userInfo) {
        putObject(USER_INFO, userInfo);
        return true;
    }
    public <T> T getUserInfo(Type type) {
        return getObject(USER_INFO, type);
    }

    public boolean setCommissionUserInfo(Object commissionUserInfo) {
        putObject(COMMISSION_USER_INFO, commissionUserInfo);
        return true;
    }
    public <T> T getCommissionUserInfo(Type type) {
        return getObject(COMMISSION_USER_INFO, type);
    }
}
