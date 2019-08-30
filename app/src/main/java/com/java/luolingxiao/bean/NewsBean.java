package com.java.luolingxiao.bean;


import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.java.luolingxiao.database.entity.SavedNews;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class NewsBean {
    private String title, category, content, publisher;
    private NewsDateTime publishTime;
    private JSONObject newsJson;
    private String url;
    private List<String> keywords;
    private List<String> imageUrls;
    private boolean read;

    private class Keyword {
        private String word;
        private double score;
    }

    private NewsBean() {}
    public JSONObject getNewsJson() { return newsJson; }
    public List<String> getImageUrls() { return imageUrls; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getContent() { return content; }
    public String getPublisher() { return publisher; }
    public List<String> getKeywords() { return this.keywords; }
    public NewsDateTime getPublishTime() { return publishTime; }
    public String getValue(String key) { return newsJson.getString(key); }

    public void setContent(String content) {
        this.content = content;
    }
    //    public void addImage(Bitmap bitmap) { this.images.add(bitmap); }


    public static NewsBean parse(JSONObject newsJson) {
        NewsBean news = new NewsBean();
        news.newsJson = newsJson;
        news.title = newsJson.getString("title");
        news.category = newsJson.getString("category");
        news.content = newsJson.getString("content");
        news.publisher = newsJson.getString("publisher");
        news.publishTime = NewsDateTime.parse(newsJson.getString("publishTime"));
        news.read = false;
        String imageUrlsRaw = newsJson.getString("image");
        if (imageUrlsRaw.length() > 2) {
            news.imageUrls = Arrays.asList(imageUrlsRaw.substring(1, imageUrlsRaw.length() - 1).split(", "));
        } else {
            news.imageUrls = Collections.emptyList();
        }
        news.keywords = new ArrayList<>();
        for (Object object: newsJson.getJSONArray("keywords")) {
            news.keywords.add(((JSONObject)object).getString("word"));
        }
        return news;
    }

    public static NewsBean decode(SavedNews savedNews) {
        NewsBean newsBean = parse(savedNews.getContent());
        newsBean.read = savedNews.isRead();
        return newsBean;
    }

    public static List<NewsBean> decode(List<SavedNews> savedNews) {
        List<NewsBean> ret = new ArrayList<>();
        for (SavedNews news: savedNews) {
            ret.add(decode(news));
        }
        return ret;
    }

    public boolean isRead() { return read; }
    public boolean setRead(boolean read) { return this.read = read; }

    @NonNull
    @Override
    public String toString() {
        return newsJson.toString();
    }

    public static NewsBean parse(String jsonString) {
        return parse((JSONObject)JSONObject.parse(jsonString));
    }

    public static NewsBean parse(HashMap<String, Object> map) {
        NewsBean newsBean = new NewsBean();
        newsBean.imageUrls = (List<String>)map.get("image");
        newsBean.publishTime = NewsDateTime.parse(Objects.requireNonNull(map.get("publishTime")).toString());

        return newsBean;
    }

    public String getAbstract() {
        if (content.length() <= 100)
            return content;
        return content.substring(0, 100) + "...";
    }

    private static final String ip = "149.28.67.105";
    private static final String port = "8080";
    private static final String shareUrl = "http://" + ip + ":" + port + "/news?";
    public String getShareUrl() { return shareUrl + "publishTime=" + publishTime.toQueryValue() + "&newsID=" + getNewsId(); }

    public String getNewsId() {
        return newsJson.getString("newsID");
    }
}
