package com.example.newsapp.bean;


import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.example.newsapp.database.entity.SavedNews;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewsBean {
    private String title, category, content, publisher;
    private NewsDateTime publishTime;
    private JSONObject newsJson;
    private List<String> imageUrls;
    private List<String> keywords;
//    private List<Drawable> images = new ArrayList<>();

    private NewsBean() {}
    public JSONObject getNewsJson() { return newsJson; }
    public List<String> getImageUrls() { return imageUrls; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getContent() { return content; }
    public String getPublisher() { return publisher; }
    NewsDateTime getPublishTime() { return publishTime; }
    String getValue(String key) { return newsJson.getString(key); }

    public static NewsBean parse(JSONObject newsJson) {
        NewsBean news = new NewsBean();
        news.newsJson = newsJson;
        news.title = newsJson.getString("title");
        news.category = newsJson.getString("category");
        news.content = newsJson.getString("content");
        news.publisher = newsJson.getString("publisher");
        news.publishTime = NewsDateTime.parse(newsJson.getString("publishTime"));
        String urls = newsJson.getString("image");
        news.imageUrls = Arrays.asList(urls.substring(1, urls.length() - 1).split(", "));
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

    public static NewsBean parse(String jsonString) {
        return parse((JSONObject)JSONObject.parse(jsonString));
    }

    public NewsBean parseSavedNews(SavedNews savedNews) {
        NewsBean newsBean = parse(savedNews.getContent());
        return newsBean;
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
