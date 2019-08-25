//package com.java.luolingxiao;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.Html;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import com.alibaba.fastjson.JSONObject;
//import com.java.luolingxiao.bean.AppConstant;
//import com.java.luolingxiao.bean.NewsBean;
//import com.java.luolingxiao.bean.NewsDetail;
//import com.stx.xhb.xbanner.XBanner;
//import com.stx.xhb.xbanner.entity.LocalImageInfo;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class NewsActivity3 extends DefaultSwipeBackActivity {
//
//
//    ImageView newsDetailPhotoIv;
//
//    TextView newsDetailFromTv;
//    TextView newsDetailBodyTv;
//    ProgressBar progressBar;
//
//    XBanner mXBanner;
//    private String postId;
//    private String mNewsTitle;
//    private String mShareLink;
//
//    public static void startAction(Context mContext, NewsBean newsbean) {
//        Intent intent = new Intent(mContext, NewsActivity3.class);
////        intent.putExtra(AppConstant.NEWS_POST_ID, postId);
//        intent.putExtra("NewsBean", newsbean.getNewsJson().toString());
//        mContext.startActivity(intent, null);
//    }
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_news);
//        postId = getIntent().getStringExtra(AppConstant.NEWS_POST_ID);
//        NewsBean newsBean = NewsBean.parse((JSONObject)JSONObject.parse(getIntent().getStringExtra("NewsBean")));
//        newsDetailFromTv = findViewById(R.id.news_detail_from_tv);
//        newsDetailBodyTv = findViewById(R.id.news_detail_body_tv);
//        progressBar = findViewById(R.id.progress_bar);
//
//        mXBanner = (XBanner) findViewById(R.id.xbanner);
////        mXBanner.setBannerData(null);
//
//        List<LocalImageInfo> data = new ArrayList<>();
//        data.add(new LocalImageInfo(R.mipmap.ic_care_normal));
//        data.add(new LocalImageInfo(R.mipmap.ic_care_normal));
//        data.add(new LocalImageInfo(R.mipmap.ic_care_normal));
//        data.add(new LocalImageInfo(R.mipmap.ic_care_normal));
//        mXBanner.setBannerData(data);
//        mXBanner.setAutoPlayAble(true);
//
//        //加载广告图片
//        mXBanner.loadImage(new XBanner.XBannerAdapter() {
//            @Override
//            public void loadBanner(XBanner banner, Object model, View view, int position) {
//                ImageView imageView = (ImageView)view;
//                imageView.setImageResource(data.);
//                //1、此处使用的Glide加载图片，可自行替换自己项目中的图片加载框架
//                //2、返回的图片路径为Object类型，你只需要强转成你传输的类型就行，切记不要胡乱强转！
////                Glide.with(MainActivity.this).load(((AdvertiseEntity.OthersBean)
////                        model).getThumbnail()).placeholder(R.drawable.default_image).error(R.drawable.default_image).into((ImageView) view);
//            }
//        });
//
////        if (newsBean.getImages().size() > 0) {
////            imageView.setImageBitmap(newsBean.getImages().get(0));
////        }
//
//        mNewsTitle = newsBean.getTitle();
//        String newsBody = newsBean.getContent();
//
//        //mNewsDetailTitleTv.setText(newsTitle);
////        newsDetailFromTv.setText(getString(R.string.news_from, newsSource, newsTime));
////        setNewsDetailPhotoIv(NewsImgSrc);
//        setBody(newsBody);
//        onCompleted();
//    }
//
//
//    private void setBody(String newsBody) {
//        newsDetailBodyTv.setText(Html.fromHtml(newsBody));
//    }
//
//    private boolean isShowBody(String newsBody, int imgTotal) {
//        return imgTotal >= 2 && newsBody != null;
//    }
//
//    private String getImgSrcs(NewsDetail newsDetail) {
//        List<NewsDetail.ImgBean> imgSrcs = newsDetail.getImg();
//        String imgSrc;
//        if (imgSrcs != null && imgSrcs.size() > 0) {
//            imgSrc = imgSrcs.get(0).getSrc();
//        } else {
//            imgSrc = getIntent().getStringExtra(AppConstant.NEWS_IMG_RES);
//        }
//        return imgSrc;
//    }
//
//    private boolean canBrowse(Intent intent) {
//        return intent.resolveActivity(getPackageManager()) != null && mShareLink != null;
//    }
//
//    public void onCompleted() {
//        progressBar.setVisibility(View.GONE);
////        fab.setVisibility(View.VISIBLE);
//    }
//
//}
