package com.java.luolingxiao.api;

import android.app.Activity;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;

public class UserApi {
    public static void logout() { getPlatForm().removeAccount(true); }
    public static boolean isAuthorized() { return getPlatForm().isAuthValid(); }
    private static Platform getPlatForm() { return ShareSDK.getPlatform(QQ.NAME); }

    public static void authorize(Activity activity, PlatformActionListener listener) {
        if (isAuthorized()) return;
        Platform platform = getPlatForm();
        ShareSDK.setActivity(activity);
        platform.SSOSetting(false);
        platform.setPlatformActionListener(listener);
        platform.authorize();
    }

    public static String getUsername() { return getPlatForm().getDb().getUserName(); }
    public static String getPortraitUrl() { return getPlatForm().getDb().getUserIcon(); }
}
