package com.java.luolingxiao.api;

import android.app.Activity;

import com.java.luolingxiao.bean.NewsBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;

public class UserApi {
    private static final String ip = "149.28.67.105";
    private static final String port = "8080";

    public static void logout() {
        getPlatForm().removeAccount(true);
        favorites.clear();
    }
    public static boolean isAuthorized() { return getPlatForm().isAuthValid(); }
    private static Platform getPlatForm() { return ShareSDK.getPlatform(QQ.NAME); }

    public static void authorize(Activity activity, PlatformActionListener listener) {
        if (isAuthorized()) return;
        Platform platform = getPlatForm();
        ShareSDK.setActivity(activity);
        platform.SSOSetting(false);
        platform.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int index, HashMap<String, Object> hashMap) {
                // 获取 favorite
//                FastJsonRequest request = new FastJsonRequest("http://" + ip + ":" + port + "/database/favorite");
//                request.add("id", getUserId())
//                        .add("type", "get");
//
//                Response<JSONObject> response = NoHttp.startRequestSync(request);
//                if (response.getException() != null) {
//                    platform.removeAccount(true);
//                    activity.runOnUiThread(() -> {
//                        Toast.makeText(activity, "你收藏服务器呢，写了吗？", Toast.LENGTH_SHORT).show();
//                    });
//                    listener.onError(platform, index, new Exception("无法获取收藏信息"));
//                    return;
//                }
//                JSONArray array = response.get().getJSONArray("data");
//                for (int i = 0; i < array.size(); i++) {
//                    favorites.add(NewsBean.parse(array.getJSONObject(i)));
//                }
                listener.onComplete(platform, index, hashMap);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                listener.onError(platform, i, throwable);
            }

            @Override
            public void onCancel(Platform platform, int i) {
                listener.onCancel(platform, i);
            }
        });
        platform.authorize();
    }

    public static String getUsername() { return getPlatForm().getDb().getUserName(); }
    public static String getPortraitUrl() { return getPlatForm().getDb().getUserIcon(); }
    public static String getUserId() { return getPlatForm().getDb().getUserId(); }

    private static List<NewsBean> favorites = new ArrayList<>();


}
