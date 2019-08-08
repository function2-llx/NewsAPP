package com.example.newsapp.bean;


import com.alibaba.fastjson.JSONObject;

public class NewsBean {
    public String title, category, content, publisher;
    private NewsDateTime publishTime;

    private NewsBean() {}

    public static NewsBean parse(JSONObject newsJson) {
        NewsBean news = new NewsBean();
        news.title = newsJson.getString("title");
        news.category = newsJson.getString("category");
        news.content = newsJson.getString("content");
        news.publisher = newsJson.getString("publisher");
        news.publishTime = NewsDateTime.parse(newsJson.getString("publishTime"));

        return news;
    }
}
