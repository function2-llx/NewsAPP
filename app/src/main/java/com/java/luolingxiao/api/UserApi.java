package com.java.luolingxiao.api;

import android.app.Activity;
import android.os.Handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.luolingxiao.DefaultActivity;
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
    public static final String ip = "149.28.67.105";
//    public static final String ip = "115.182.62.169";
//    private static final String ip = "183.172.238.14";
    public static final int port = 8080;

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
                try {
                    getFavoriteSync();
                    listener.onComplete(platform, index, hashMap);
                } catch (Exception e) {
                    logout();
                    listener.onError(platform, index, e);
                }
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

    public interface SetFavoriteCallback {
        void onSuccess();
        void onException(Exception e);
    }

    public void setFavorite(NewsBean newsBean, boolean favorite, SetFavoriteCallback callback) {
        Handler handler = new Handler();
        new Thread(() -> {
            FastJsonRequest request = new FastJsonRequest(String.format(Locale.getDefault(), "http://%s:%d/favorite/%s", ip, port, favorite ? "insert" : "remove"), RequestMethod.POST);
            request.add("id", getUserId()).add("newsJson", newsBean.toString());
            Response<JSONObject> response =  NoHttp.startRequestSync(request);
            if (response.getException() != null) {
                handler.post(() -> callback.onException(response.getException()));
            } else {
                if (favorite) {
                    userFavoriteCache.add(newsBean);
                } else {
                    userFavoriteCache.remove(newsBean);
                }
                handler.post(callback::onSuccess);
            }
        }).start();
    }

    public boolean isFavorite(NewsBean newsBean) {
        return userFavoriteCache.contains(newsBean);
    }

    public List<NewsBean> getFavoriteSync() throws Exception{
        if (userFavoriteCache == null) {
            synchronized (this) {
                if (userFavoriteCache == null) {
                    userFavoriteCache = new HashSet<>();
                    FastJsonRequest request = new FastJsonRequest(String.format(Locale.getDefault(), "http://%s:%d/favorite/get", ip, port));
                    request.add("id", getUserId());
                    Response<JSONObject> response = NoHttp.startRequestSync(request);
                    if (response.getException() != null) throw response.getException();
                    JSONArray data = response.get().getJSONArray("data");
                    for (int i = 0; i < data.size(); i++) {
                        userFavoriteCache.add(NewsBean.parse(data.getJSONObject(i)));
                    }
                    DefaultActivity.getAnyActivity().runOnUiThread(() -> DefaultActivity.getAnyActivity().getDataRepository().insertNews(new ArrayList<>(userFavoriteCache)));
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
                        try {
                            instance.getFavoriteSync();
                        } catch (Exception e) {
                            instance.logout();
                        }
                    }
                }
            }
        }
        return instance;
    }
}
