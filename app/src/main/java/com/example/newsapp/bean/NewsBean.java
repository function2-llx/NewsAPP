package com.example.newsapp.bean;


import com.alibaba.fastjson.JSONObject;

public class NewsBean {
    private String title, category, content, publisher;
    private NewsDateTime publishTime;
    private JSONObject newsJson;

    private NewsBean() {}
    public JSONObject getNewsJson() { return newsJson; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getContent() { return content; }
    public String getPublisher() { return publisher; }
    NewsDateTime getPublishTime() { return publishTime; }
    String getJsonString(String key) { return newsJson.getString(key); }

    public static NewsBean parse(JSONObject newsJson) {
        NewsBean news = new NewsBean();
        news.newsJson = newsJson;
        news.title = newsJson.getString("title");
        news.category = newsJson.getString("category");
        news.content = newsJson.getString("content");
        news.publisher = newsJson.getString("publisher");
        news.publishTime = NewsDateTime.parse(newsJson.getString("publishTime"));

        return news;
    }
}
