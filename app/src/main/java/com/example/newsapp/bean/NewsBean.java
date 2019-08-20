package com.example.newsapp.bean;


import android.graphics.Bitmap;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.example.newsapp.DeFaultActivity;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NewsBean {
    private String title, category, content, publisher;
    private NewsDateTime publishTime;
    private JSONObject newsJson;
    private String url;
    private List<String> keywords;
    private List<Bitmap> images;
    private List<String> imageUrls;

    private NewsBean() {}
    public JSONObject getNewsJson() { return newsJson; }
    public List<String> getImageUrls() { return imageUrls; }
    public List<Bitmap> getImages() { return images; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getContent() { return content; }
    public String getPublisher() { return publisher; }
    public List<String> getKeywords() { return this.keywords; }
    public NewsDateTime getPublishTime() { return publishTime; }
    public String getValue(String key) { return newsJson.getString(key); }
    public void addImage(Bitmap bitmap) { this.images.add(bitmap); }

    private static int imageCnt = 0;

    public static NewsBean parse(JSONObject newsJson, boolean parseImage) {
        NewsBean news = new NewsBean();
        news.newsJson = newsJson;
        news.title = newsJson.getString("title");
        news.category = newsJson.getString("category");
        news.content = newsJson.getString("content");
        news.publisher = newsJson.getString("publisher");
        news.publishTime = NewsDateTime.parse(newsJson.getString("publishTime"));
        String imageUrlsRaw = newsJson.getString("image");
        if (imageUrlsRaw.length() > 2) {
            news.imageUrls = Arrays.asList(imageUrlsRaw.substring(1, imageUrlsRaw.length() - 1).split(", "));
        } else {
            news.imageUrls = Collections.emptyList();
        }
        news.images = new ArrayList<>();
        if (parseImage) {
            DeFaultActivity.getAnyActivity().runOnUiThread(() -> {
                synchronized (DeFaultActivity.getAnyActivity()) {
                    imageCnt++;
                }
                Toast.makeText(DeFaultActivity.getAnyActivity(), "iamge size: " + imageCnt, Toast.LENGTH_SHORT).show();
            });
            for (String url: news.imageUrls) {
                Request<Bitmap> request = NoHttp.createImageRequest(url);
                Response<Bitmap> response = NoHttp.startRequestSync(request);
                imageCnt++;
//                DeFaultActivity.getAnyActivity().runOnUiThread(() -> {
//                    Toast.makeText(DeFaultActivity.getAnyActivity(), "image " + imageCnt + ", url=" + url, Toast.LENGTH_SHORT).show();
//                });
                if (response.getException() == null) {
                    news.images.add(response.get());
                } else {
                    response.getException().printStackTrace();
                }
            }
        }
        news.keywords = new ArrayList<>();
        for (Object object: newsJson.getJSONArray("keywords")) {
            news.keywords.add(((JSONObject)object).getString("word"));
        }
        return news;
    }

    @NonNull
    @Override
    public String toString() {
        return newsJson.toString();
    }

    public static NewsBean parse(String jsonString, boolean parseImage) {
        return parse((JSONObject)JSONObject.parse(jsonString), parseImage);
    }

    public String getAbstract() {
        if (content.length() <= 100)
            return content;
        return content.substring(0, 100) + "...";
    }

    public String getNewsId() {
        return newsJson.getString("newsId");
    }
}
