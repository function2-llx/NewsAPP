package com.example.newsapp;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSONObject;
import com.example.newsapp.api.NewsApi;
import com.example.newsapp.bean.AppConstant;
import com.example.newsapp.bean.NewsBean;
import com.example.newsapp.bean.NewsDetail;
import com.example.newsapp.widget.URLImageGetter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * des:普通新闻详情
 * Created by xsf
 * on 2016.09.16:57
 */
public class NewsActivity extends DefaultSwipeBackActivity {


    ImageView newsDetailPhotoIv;
    View maskView;

    Toolbar toolbar;

    CollapsingToolbarLayout toolbarLayout;
    AppBarLayout appBar;
    TextView newsDetailFromTv;
    TextView newsDetailBodyTv;
    ProgressBar progressBar;

    FloatingActionButton fab;
    private String postId;
    private URLImageGetter mUrlImageGetter;
    private String mNewsTitle;
    private String mShareLink;


    public static void startAction(Context mContext, View view, NewsBean newsbean) {
        Intent intent = new Intent(mContext, NewsActivity.class);
//        intent.putExtra(AppConstant.NEWS_POST_ID, postId);
        intent.putExtra("NewsBean", newsbean.getNewsJson().toString());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation((Activity) mContext,view, AppConstant.TRANSITION_ANIMATION_NEWS_PHOTOS);
            mContext.startActivity(intent, options.toBundle());
        } else {

            //让新的Activity从一个小的范围扩大到全屏
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeScaleUpAnimation(view, view.getWidth() / 2, view.getHeight() / 2, 0, 0);
            ActivityCompat.startActivity((Activity) mContext, intent, options.toBundle());
        }

    }

    void shareWechat(Platform wechat, Platform.ShareParams shareParams, NewsBean newsBean) {
        shareParams.setTitle(newsBean.getTitle());
        shareParams.setText(newsBean.getAbstract());
        if (!newsBean.getImageUrls().isEmpty()) {
            shareParams.setImageUrl(newsBean.getImageUrls().get(0));
        }
        shareParams.setUrl("www.baidu.com");
        shareParams.setShareType(Platform.SHARE_WEBPAGE);
//        shareParams.setShareType(Platform.SHARE_APPS);
        wechat.share(shareParams);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_news_detail);
        postId = getIntent().getStringExtra(AppConstant.NEWS_POST_ID);
        NewsBean newsBean = NewsBean.parse((JSONObject)JSONObject.parse(getIntent().getStringExtra("NewsBean")));
        toolbarLayout = findViewById(R.id.toolbar_layout);
        toolbar = findViewById(R.id.toolbar);
        appBar = findViewById(R.id.app_bar);
        newsDetailFromTv = findViewById(R.id.news_detail_from_tv);
        newsDetailBodyTv = findViewById(R.id.news_detail_body_tv);
        progressBar = findViewById(R.id.progress_bar);
        newsDetailPhotoIv = findViewById(R.id.news_detail_photo_iv);
        maskView = findViewById(R.id.mask_view);
        ImageView imageView = findViewById(R.id.news_detail_photo_iv);

        if (newsBean.getImageUrls().size() > 0) {
            NewsApi.requestImage(newsBean.getImageUrls().get(0), new NewsApi.ImageCallback() {
                @Override
                public void onReceived(Bitmap bitmap) {
                    imageView.setImageBitmap(bitmap);
                }

                @Override
                public void onException(Exception e) {

                }
            });

        }
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
                                }
                            }
                        });
//                        oks.setImageArray();
//                        oks.setImageUrl(newsBean.getImageUrls().get(0));
//                        oks.setSite("咕鸽时事");
//                        oks.setTitleUrl("www.baidu.com");
//                        oks.setImageData(ge);
//                        oks.setUrl("http://sharesdk.cn");
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
        String newsBody = newsBean.getContent();
        String NewsImgSrc = newsBean.getImageUrls().size() > 0? newsBean.getImageUrls().get(0) : "";

        setToolBarLayout(mNewsTitle);
        //mNewsDetailTitleTv.setText(newsTitle);
//        newsDetailFromTv.setText(getString(R.string.news_from, newsSource, newsTime));
//        setNewsDetailPhotoIv(NewsImgSrc);
        setBody(newsBody);
        onCompleted();
    }


//    @Override
//    public void initView() {
//        SetTranslanteBar();
//        postId = getIntent().getStringExtra(AppConstant.NEWS_POST_ID);
//        mPresenter.getOneNewsDataRequest(postId);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    finishAfterTransition();
//                } else {
//                    finish();
//                }
//            }
//        });
//        toolbar.inflateMenu(R.menu.news_detail);
//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()) {
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
//                }
//                return true;
//            }
//        });
//        //分享
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mShareLink == null) {
//                    mShareLink = "";
//                }
//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.setType("text/plain");
//                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share));
//                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_contents, mNewsTitle, mShareLink));
//                startActivity(Intent.createChooser(intent, getTitle()));
//            }
//        });
//    }

//    @Override
//    public void returnOneNewsData(NewsDetail newsDetail) {
//        mShareLink = newsDetail.getShareLink();
//        mNewsTitle = newsDetail.getTitle();
//        String newsSource = newsDetail.getSource();
//        String newsTime = TimeUtil.formatDate(newsDetail.getPtime());
//        String newsBody = newsDetail.getBody();
//        String NewsImgSrc = getImgSrcs(newsDetail);
//
//        setToolBarLayout(mNewsTitle);
//        //mNewsDetailTitleTv.setText(newsTitle);
//        newsDetailFromTv.setText(getString(R.string.news_from, newsSource, newsTime));
//        setNewsDetailPhotoIv(NewsImgSrc);
//        setNewsDetailBodyTv(newsDetail, newsBody);
//    }

    private void setToolBarLayout(String newsTitle) {
        toolbarLayout.setTitle(newsTitle);
        toolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, R.color.white));
        toolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.primary_text_white));
    }

//    private void setNewsDetailPhotoIv(String imgSrc) {
////        Glide.with(this).load(imgSrc)
////                .fitCenter()
////                .error(com.jaydenxiao.common.R.drawable.ic_empty_picture)
////                .crossFade().into(newsDetailPhotoIv);
//    }

//    private void setNewsDetailBodyTv(final NewsDetail newsDetail, final String newsBody) {
//        mRxManager.add(Observable.timer(500, TimeUnit.MILLISECONDS)
//                .compose(RxSchedulers.<Long>io_main())
//                .subscribe(new Subscriber<Long>() {
//                    @Override
//                    public void onCompleted() {
//                        progressBar.setVisibility(View.GONE);
//                        fab.setVisibility(View.VISIBLE);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        progressBar.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onNext(Long aLong) {
//                        setBody(newsDetail, newsBody);
//                    }
//                }));
//    }

    private void setBody(NewsDetail newsDetail, String newsBody) {
        int imgTotal = newsDetail.getImg().size();
        if (isShowBody(newsBody, imgTotal)) {
//              mNewsDetailBodyTv.setMovementMethod(LinkMovementMethod.getInstance());//加这句才能让里面的超链接生效,实测经常卡机崩溃
            mUrlImageGetter = new URLImageGetter(newsDetailBodyTv, newsBody, imgTotal);
            newsDetailBodyTv.setText(Html.fromHtml(newsBody, mUrlImageGetter, null));
        } else {
            newsDetailBodyTv.setText(Html.fromHtml(newsBody));
        }
    }

    private void setBody(String newsBody) {
        newsDetailBodyTv.setText(Html.fromHtml(newsBody));
    }

    private boolean isShowBody(String newsBody, int imgTotal) {
        return imgTotal >= 2 && newsBody != null;
    }

    private String getImgSrcs(NewsDetail newsDetail) {
        List<NewsDetail.ImgBean> imgSrcs = newsDetail.getImg();
        String imgSrc;
        if (imgSrcs != null && imgSrcs.size() > 0) {
            imgSrc = imgSrcs.get(0).getSrc();
        } else {
            imgSrc = getIntent().getStringExtra(AppConstant.NEWS_IMG_RES);
        }
        return imgSrc;
    }

    private boolean canBrowse(Intent intent) {
        return intent.resolveActivity(getPackageManager()) != null && mShareLink != null;
    }

    public void onCompleted() {
        progressBar.setVisibility(View.GONE);
//        fab.setVisibility(View.VISIBLE);
    }

//    @Override
//    public void showLoading(String title) {
//
//    }
//
//    @Override
//    public void stopLoading() {
//
//    }
//
//    @Override
//    public void showErrorTip(String msg) {
//
//    }

}
