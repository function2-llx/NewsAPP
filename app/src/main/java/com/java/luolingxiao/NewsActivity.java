package com.java.luolingxiao;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSONObject;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.java.luolingxiao.api.NewsApi;
import com.java.luolingxiao.api.UserApi;
import com.java.luolingxiao.bean.NewsBean;
import com.java.luolingxiao.widget.Utils;
import com.mob.moblink.MobLink;
import com.mob.moblink.Scene;
import com.mob.moblink.SceneRestorable;
import com.stx.xhb.xbanner.XBanner;
import com.stx.xhb.xbanner.entity.LocalImageInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import de.hdodenhof.circleimageview.CircleImageView;

public class NewsActivity extends DefaultSwipeBackActivity
    implements SceneRestorable {

    private Toolbar toolbar;

    CollapsingToolbarLayout toolbarLayout;
    AppBarLayout appBar;
    TextView newsDetailFromTv;
    TextView newsDetailBodyTv;
    CircleImageView circleImageView;

    XBanner mXBanner;

    @Override
    public void onReturnSceneData(Scene scene) {
        HashMap<String, Object> params = scene.getParams();
        HashMap<String, Object> news = (HashMap<String, Object>) Objects.requireNonNull(Objects.requireNonNull(params.get("news")));
        sharedJson = new JSONObject(news);
    }

    // 必须重写该方法，防止MobLink在某些情景下无法还原
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        MobLink.updateNewIntent(getIntent(), this);
    }

    public static void startAction(Context mContext, NewsBean newsbean) {
        Intent intent = new Intent(mContext, NewsActivity.class);
        intent.putExtra("NewsBean", newsbean.getNewsJson().toString());
        mContext.startActivity(intent, null);
    }

    void shareWechat(Platform wechat, Platform.ShareParams shareParams, NewsBean newsBean) {
        shareParams.setTitle(newsBean.getTitle());
        shareParams.setText(newsBean.getAbstract());
        if (!newsBean.getImageUrls().isEmpty()) {
            shareParams.setImageUrl(newsBean.getImageUrls().get(0));
        }

        shareParams.setUrl(newsBean.getShareUrl());
        System.err.println(newsBean.getShareUrl());
        shareParams.setShareType(Platform.SHARE_WEBPAGE);
        wechat.share(shareParams);
    }

    void shareWechatMoments(Platform wechat, Platform.ShareParams shareParams, NewsBean newsBean) {
        shareParams.setTitle(newsBean.getTitle());
        shareParams.setText(newsBean.getAbstract());
        if (!newsBean.getImageUrls().isEmpty()) {
            shareParams.setImageUrl(newsBean.getImageUrls().get(0));
        }

        shareParams.setUrl(newsBean.getShareUrl());
        System.err.println(newsBean.getShareUrl());
        shareParams.setShareType(Platform.SHARE_WEBPAGE);
        wechat.share(shareParams);
    }

    void shareQQ(Platform qq, Platform.ShareParams shareParams, NewsBean newsBean) {
        shareParams.setTitle(newsBean.getTitle());
        shareParams.setText(newsBean.getAbstract());
        if (!newsBean.getImageUrls().isEmpty()) {
            shareParams.setImageUrl(newsBean.getImageUrls().get(0));
        }
        shareParams.setTitleUrl(newsBean.getShareUrl());
        shareParams.setShareType(Platform.SHARE_WEBPAGE);
        qq.share(shareParams);
    }

    void shareQZone(Platform qq, Platform.ShareParams shareParams, NewsBean newsBean) {
        shareParams.setTitle(newsBean.getTitle());
        shareParams.setText(newsBean.getAbstract());
        if (!newsBean.getImageUrls().isEmpty()) {
            shareParams.setImageUrl(newsBean.getImageUrls().get(0));
        }
        shareParams.setTitleUrl(newsBean.getShareUrl());
        shareParams.setShareType(Platform.SHARE_WEBPAGE);
        qq.share(shareParams);
    }

    private JSONObject sharedJson;

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedJson != null) {
            NewsBean newsBean = NewsBean.parse(sharedJson);
            sharedJson = null;
            startAction(this, newsBean);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }
    NewsBean newsBean = null;

    private void updateIcons() {
        toolbar.getMenu().findItem(R.id.action_local_favorite).setIcon(getDataRepository().isLocalFavorite(newsBean) ? R.mipmap.action_local_favorite : R.mipmap.action_local_unfavorite);
        if(UserApi.getInstance().isAuthorized()) toolbar.getMenu().findItem(R.id.action_user_favorite).setIcon(UserApi.getInstance().isFavorite(newsBean) ? R.drawable.action_user_favorite : R.drawable.action_user_unfavorite);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_news_detail);

        Utils.widthPixels = getResources().getDisplayMetrics().widthPixels;
        Utils.heightPixels = getResources().getDisplayMetrics().heightPixels;
        Utils.density = getResources().getDisplayMetrics().density;


//        EventBus.getDefault().register(this);

        if (getIntent().getStringExtra("NewsBean") != null) {
            newsBean = NewsBean.parse((JSONObject) Objects.requireNonNull(JSONObject.parse(getIntent().getStringExtra("NewsBean"))));
        } else {
            newsBean = NewsBean.parse(sharedJson);
            sharedJson = null;
        }

        List<String> images = newsBean.getImageUrls();

        toolbarLayout = findViewById(R.id.toolbar_layout);
        toolbar = findViewById(R.id.toolbar_settings);
//        setSupportActionBar(toolbar);
        appBar = findViewById(R.id.app_bar);
        if (images.size() == 0) {
//            appBar.getLayoutParams().height = Utils.;
        }


        appBar.requestLayout();
        newsDetailFromTv = findViewById(R.id.time);
        newsDetailFromTv.setText(newsBean.getPublisher() + "\n" + newsBean.getPublishTime().toString());
        circleImageView = findViewById(R.id.profile_image);
        Bitmap bitmap = Utils.reshapeImage(((BitmapDrawable)getResources().getDrawable(R.mipmap.ic_care_normal, getTheme())).getBitmap());
        if (newsBean.getPublisher().equals("海外网")) {
            bitmap = Utils.reshapeImage(((BitmapDrawable)getResources().getDrawable(R.drawable.news_haiwai, getTheme())).getBitmap());
        } else if (newsBean.getPublisher().equals("胶东在线")) {
            bitmap = Utils.reshapeImage(((BitmapDrawable)getResources().getDrawable(R.drawable.news_jiaodongzaixian, getTheme())).getBitmap());
        } else if (newsBean.getPublisher().equals("澎湃新闻")) {
            bitmap = Utils.reshapeImage(((BitmapDrawable)getResources().getDrawable(R.drawable.news_pengpai, getTheme())).getBitmap());
        } else if (newsBean.getPublisher().equals("人民网")) {
            bitmap = Utils.reshapeImage(((BitmapDrawable)getResources().getDrawable(R.drawable.news_renmin, getTheme())).getBitmap());
        } else if (newsBean.getPublisher().equals("新浪新闻")) {
            bitmap = Utils.reshapeImage(((BitmapDrawable)getResources().getDrawable(R.drawable.news_sina, getTheme())).getBitmap());
        } else if (newsBean.getPublisher().equals("搜狐新闻")) {
            bitmap = Utils.reshapeImage(((BitmapDrawable)getResources().getDrawable(R.drawable.news_sohu, getTheme())).getBitmap());
        } else if (newsBean.getPublisher().equals("网易新闻新闻")) {
            bitmap = Utils.reshapeImage(((BitmapDrawable)getResources().getDrawable(R.drawable.news_wangyi, getTheme())).getBitmap());
        } else if (newsBean.getPublisher().equals("新华网")) {
            bitmap = Utils.reshapeImage(((BitmapDrawable)getResources().getDrawable(R.drawable.news_xinhua, getTheme())).getBitmap());
        } else {
            circleImageView.setLayoutParams(new android.widget.LinearLayout.LayoutParams(0, 0));
        }
        circleImageView.setImageBitmap(bitmap);
        newsDetailBodyTv = findViewById(R.id.news_detail_body_tv);
//        progressBar = findViewById(R.id.progress_bar);
//
        mXBanner = findViewById(R.id.xbanner);

        List<LocalImageInfo> data = new ArrayList<>();
        for (int i = 0; i < images.size(); ++i) {
            data.add(new LocalImageInfo(R.mipmap.ic_care_normal));
        }
        mXBanner.setBannerData(data);
        mXBanner.setAutoPlayAble(true);

        //加载广告图片
        mXBanner.loadImage((banner, model, view, position) -> {
            ImageView imageView = (ImageView) view;
//                imageView.setImageResource(R.mipmap.ic_care_normal);
            NewsApi.requestImage(images.get(position), new NewsApi.ImageCallback() {
                @Override
                public void onReceived(Bitmap bitmap1) {
                    imageView.setImageBitmap(bitmap1);
                }

                @Override
                public void onException(Exception e) {

                }
            });
        });

        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        toolbar.inflateMenu(R.menu.news_detail);

        updateIcons();

//        MenuItem localFavoriteItem = toolbar.getMenu().findItem(R.id.action_local_favorite);
//        if (getDataRepository().isLocalFavorite(newsBean)) {
//            localFavoriteItem.setIcon(R.mipmap.action_local_favorite);
//        } else {
//            localFavoriteItem.setIcon(R.mipmap.action_local_unfavorite);
//        }

        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_share: {
                    OnekeyShare oks = new OnekeyShare();
                    oks.setShareContentCustomizeCallback((platform, shareParams) -> {
                        if (platform.getName().equals(Wechat.NAME)) {
                            shareWechat(platform, shareParams, newsBean);
                        } else if (platform.getName().equals(QQ.NAME)) {
                            shareQQ(platform, shareParams, newsBean);
                        } else if (platform.getName().equals(QZone.NAME)) {
                            shareQZone(platform, shareParams, newsBean);
                        } else if (platform.getName().equals(WechatMoments.NAME)) {
                            shareWechatMoments(platform, shareParams, newsBean);
                        }
                    });
                    oks.disableSSOWhenAuthorize();
                    oks.show(NewsActivity.this);
                }
                break;

                case R.id.action_local_favorite: {
                    getDataRepository().setFavorite(newsBean, !getDataRepository().isLocalFavorite(newsBean));
                    updateIcons();
//                    if (getDataRepository().isLocalFavorite(newsBean)) {
//                        getDataRepository().setFavorite(newsBean, false);
////                        item.setIcon(R.mipmap.action_local_unfavorite);
//                    } else {
//                        getDataRepository().setFavorite(newsBean, true);
////                        item.setIcon(R.mipmap.action_local_favorite);
//                    }
////                    getDataRepository().insertNews(newsBean);
                }
                break;

                case R.id.action_user_favorite: {
                    UserApi api = UserApi.getInstance();
                    if (!api.isAuthorized()) {
                        Toast.makeText(this, "登录后使用云端收藏功能哦", Toast.LENGTH_SHORT).show();
                    } else {
                        api.setFavorite(newsBean, !api.isFavorite(newsBean), new UserApi.SetFavoriteCallback() {
                            @Override
                            public void onSuccess() {
                                updateIcons();
                                Toast.makeText(NewsActivity.this, api.isFavorite(newsBean) ? "成功收藏到云端" : "成功从云端收藏删除", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onException(Exception e) {
                                Toast.makeText(NewsActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                break;
            }
            return true;
        });
        String mNewsTitle = newsBean.getTitle();
        String newsBody = newsBean.getContent().replace("\\n", "\n");
        setToolBarLayout(mNewsTitle);
        setBody(newsBody);

        TextView textView = findViewById(R.id.title);
        textView.setText(mNewsTitle);
    }


    private void setToolBarLayout(String newsTitle) {
        toolbarLayout.setTitle(newsTitle);
        toolbarLayout.setExpandedTitleColor(getColor(R.color.expanded_title));
        toolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.primary_text_white));
    }


    private void setBody(String newsBody) {
        newsDetailBodyTv.setText(newsBody);
//        newsDetailBodyTv.setText(Html.fromHtml(newsBody));
    }

}
