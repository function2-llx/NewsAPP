package com.java.luolingxiao;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.util.Util;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.java.luolingxiao.api.NewsApi;
import com.java.luolingxiao.bean.NewsBean;
import com.java.luolingxiao.bean.NewsDetail;
import com.java.luolingxiao.widget.Utils;
import com.stx.xhb.xbanner.XBanner;
import com.stx.xhb.xbanner.entity.LocalImageInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import de.hdodenhof.circleimageview.CircleImageView;

public class NewsActivity extends DefaultSwipeBackActivity {


    ImageView newsDetailPhotoIv;
    View maskView;

    Toolbar toolbar;

    CollapsingToolbarLayout toolbarLayout;
    AppBarLayout appBar;
    TextView newsDetailFromTv;
    TextView newsDetailBodyTv;
    ProgressBar progressBar;
    CircleImageView circleImageView;

    FloatingActionButton fab;
    private String postId;
    private String mNewsTitle;
    private String mShareLink;
    XBanner mXBanner;

    public static void startAction(Context mContext, NewsBean newsbean) {
        Intent intent = new Intent(mContext, NewsActivity.class);
//        intent.putExtra(AppConstant.NEWS_POST_ID, postId);
        intent.putExtra("NewsBean", newsbean.getNewsJson().toString());
        mContext.startActivity(intent, null);
    }
//    public static void startAction(Context mContext, View view, NewsBean newsbean) {
//        Intent intent = new Intent(mContext, NewsActivity.class);
////        intent.putExtra(AppConstant.NEWS_POST_ID, postId);
//        intent.putExtra("NewsBean", newsbean.getNewsJson().toString());
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            ActivityOptions options = ActivityOptions
//                    .makeSceneTransitionAnimation((Activity) mContext,view, AppConstant.TRANSITION_ANIMATION_NEWS_PHOTOS);
//            mContext.startActivity(intent, options.toBundle());
//        } else {
//
//            //让新的Activity从一个小的范围扩大到全屏
//            ActivityOptionsCompat options = ActivityOptionsCompat
//                    .makeScaleUpAnimation(view, view.getWidth() / 2, view.getHeight() / 2, 0, 0);
//            ActivityCompat.startActivity((Activity) mContext, intent, options.toBundle());
//        }
//    }


    void shareWechat(Platform wechat, Platform.ShareParams shareParams, NewsBean newsBean) {
        shareParams.setTitle(newsBean.getTitle());
        shareParams.setText(newsBean.getAbstract());
        if (!newsBean.getImageUrls().isEmpty()) {
            shareParams.setImageUrl(newsBean.getImageUrls().get(0));
        }

        shareParams.setUrl(newsBean.getShareUrl());
        System.err.println(newsBean.getShareUrl());
//        Toast.makeText(this, newsBean.getShareUrl(), Toast.LENGTH_SHORT).show();
//        shareParams.setUrl(newsBean.getNewsJson().getString("url"));
        shareParams.setShareType(Platform.SHARE_WEBPAGE);
        wechat.share(shareParams);
    }

    void shareQQ(Platform qq, Platform.ShareParams shareParams, NewsBean newsBean) {
        shareParams.setTitle(newsBean.getTitle());
        shareParams.setText(newsBean.getAbstract());
        if (!newsBean.getImageUrls().isEmpty()) {
            shareParams.setImageUrl(newsBean.getImageUrls().get(0));
        }
//        if (newsBean.getImages().size() > 0) {
//            shareParams.setImageData(newsBean.getImages().get(0));
//        }
//        shareParams.setTitleUrl(newsBean.getNewsJson().getString("url"));
        shareParams.setTitleUrl(newsBean.getShareUrl());
        shareParams.setShareType(Platform.SHARE_WEBPAGE);
        qq.share(shareParams);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NewsBean newsBean = NewsBean.parse((JSONObject) JSONObject.parse(getIntent().getStringExtra("NewsBean")));
        List<String> images = newsBean.getImageUrls();
        if (images.size() == 0) {
            setContentView(R.layout.act_news_detail_no_picture);
        } else {
            setContentView(R.layout.act_news_detail);
        }

        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        toolbarLayout = findViewById(R.id.toolbar_layout);
        toolbar = findViewById(R.id.toolbar);
        appBar = findViewById(R.id.app_bar);
        newsDetailFromTv = findViewById(R.id.news_detail_from_tv);
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
        progressBar = findViewById(R.id.progress_bar);
        newsDetailPhotoIv = findViewById(R.id.news_detail_photo_iv);
        maskView = findViewById(R.id.mask_view);
        ImageView imageView = findViewById(R.id.news_detail_photo_iv);

        mXBanner = (XBanner) findViewById(R.id.xbanner);
//        mXBanner.setBannerData(null);


//        if (images.size() == 0) {
//            //must be CoordinatorLayout.LayoutParams
//            appBarLayout.setLayoutParams(new CoordinatorLayout.LayoutParams(appBarLayout.getLayoutParams().width, 56));
//        }

        List<LocalImageInfo> data = new ArrayList<>();
        for (int i = 0; i < images.size(); ++i) {
            data.add(new LocalImageInfo(R.mipmap.ic_care_normal));
        }
        mXBanner.setBannerData(data);
        mXBanner.setAutoPlayAble(true);

        //加载广告图片
        mXBanner.loadImage(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                ImageView imageView = (ImageView) view;
//                imageView.setImageResource(R.mipmap.ic_care_normal);
                NewsApi.requestImage(images.get(position), new NewsApi.ImageCallback() {
                    @Override
                    public void onReceived(Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onException(Exception e) {

                    }
                });
            }
        });


//        if (newsBean.getImages().size() > 0) {
//            imageView.setImageBitmap(newsBean.getImages().get(0));
//        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else {
                    finish();
                }
            }
        });
        toolbar.inflateMenu(R.menu.news_detail);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_share:
                        OnekeyShare oks = new OnekeyShare();
                        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                            @Override
                            public void onShare(Platform platform, Platform.ShareParams shareParams) {
                                if (platform.getName().equals(Wechat.NAME)) {
                                    shareWechat(platform, shareParams, newsBean);
                                } else if (platform.getName().equals(QQ.NAME)) {
                                    shareQQ(platform, shareParams, newsBean);
                                }
                            }
                        });
                        oks.disableSSOWhenAuthorize();
                        oks.show(NewsActivity.this);
                        break;
//                    case R.id.action_web_view:
//                        NewsBrowserActivity.startAction(NewsActivity.this, mShareLink, mNewsTitle);
//                        break;
//                    case R.id.action_browser:
//                        Intent intent = new Intent();
//                        intent.setAction("android.intent.action.VIEW");
//                        if (canBrowse(intent)) {
//                            Uri uri = Uri.parse(mShareLink);
//                            intent.setData(uri);
//                            startActivity(intent);
//                        }
//                        break;
                }
                return true;
            }
        });
        fab = findViewById(R.id.fab);
        //分享
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mShareLink == null) {
                    mShareLink = "";
                }
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_contents, mNewsTitle, mShareLink));
                startActivity(Intent.createChooser(intent, getTitle()));
            }
        });

//        mShareLink = newsDetail.getShareLink();
        mNewsTitle = newsBean.getTitle();
//        String newsSource = newsDetail.getSource();
//        String newsTime = TimeUtil.formatDate(newsDetail.getPtime());
        String newsBody = newsBean.getContent().replace("\\n", "\n");

        setToolBarLayout(mNewsTitle);
        //mNewsDetailTitleTv.setText(newsTitle);
        setBody(newsBody);
        onCompleted();
    }


    private void setToolBarLayout(String newsTitle) {
        toolbarLayout.setTitle(newsTitle);
        toolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, R.color.white));
        toolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.primary_text_white));
    }


    private void setBody(String newsBody) {
        newsDetailBodyTv.setText(newsBody);
//        newsDetailBodyTv.setText(Html.fromHtml(newsBody));
    }

    private boolean isShowBody(String newsBody, int imgTotal) {
        return imgTotal >= 2 && newsBody != null;
    }


    private boolean canBrowse(Intent intent) {
        return intent.resolveActivity(getPackageManager()) != null && mShareLink != null;
    }

    public void onCompleted() {
        progressBar.setVisibility(View.GONE);
//        fab.setVisibility(View.VISIBLE);
    }


}
