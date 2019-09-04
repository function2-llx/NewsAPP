package com.java.luolingxiao.api;

import android.app.Activity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.luolingxiao.bean.NewsBean;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;

public class UserApi {
    private static final String ip = "149.28.67.105";
//    private static final String ip = "183.172.238.14";
    private static final int port = 8080;

    public void logout() {
        getPlatForm().removeAccount(true);
        userFavoriteCache = null;
    }
    public boolean isAuthorized() { return getPlatForm().isAuthValid(); }
    private Platform getPlatForm() { return ShareSDK.getPlatform(QQ.NAME); }

    public void authorize(Activity activity, PlatformActionListener listener) {
        if (isAuthorized()) return;
        Platform platform = getPlatForm();
        ShareSDK.setActivity(activity);
        platform.SSOSetting(false);
        platform.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int index, HashMap<String, Object> hashMap) {
                // 获取 favorite
                getFavoriteSync();
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

    public String getUsername() { return getPlatForm().getDb().getUserName(); }
    public String getPortraitUrl() { return getPlatForm().getDb().getUserIcon(); }
    public String getUserId() { return getPlatForm().getDb().getUserId(); }

    private Set<NewsBean> userFavoriteCache;

    public void setFavorite(NewsBean newsBean, boolean favorite) {
        new Thread(() -> {
            FastJsonRequest request = new FastJsonRequest(String.format(Locale.getDefault(), "http://%s:%d/favorite/%s", ip, port, favorite ? "insert" : "remove"), RequestMethod.POST);
            request.add("id", getUserId()).add("newsJson", newsBean.toString());
            NoHttp.startRequestSync(request);
        }).start();
        if (favorite) {
            userFavoriteCache.add(newsBean);
        } else {
            userFavoriteCache.remove(newsBean);
        }
    }

    public boolean isFavorite(NewsBean newsBean) {
        boolean ret = userFavoriteCache.contains(newsBean);
        return ret;
    }

    public List<NewsBean> getFavoriteSync() {
        if (userFavoriteCache == null) {
            synchronized (this) {
                if (userFavoriteCache == null) {
                    userFavoriteCache = new HashSet<>();
                    FastJsonRequest request = new FastJsonRequest(String.format(Locale.getDefault(), "http://%s:%d/favorite/get", ip, port));
                    request.add("id", getUserId());
                    Response<JSONObject> response = NoHttp.startRequestSync(request);
                    JSONArray data = response.get().getJSONArray("data");
                    for (int i = 0; i < data.size(); i++) {
                        userFavoriteCache.add(NewsBean.parse(data.getJSONObject(i)));
                    }
                }
            }
        }
        return new ArrayList<>(userFavoriteCache);
    }

    private static UserApi instance;

    public static UserApi getInstance() {
        if (instance == null) {
            synchronized (UserApi.class) {
                if (instance == null) {
                    instance = new UserApi();
                    if (instance.isAuthorized()) {
                        instance.getFavoriteSync();
                    }
                }
            }
        }
        return instance;
    }
}
