package com.java.luolingxiao.bean;


import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONArray;
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
    private List<Keyword> keywords;
    private List<String> imageUrls;
    public double score = 0;

    public static class Keyword {
        public String word;
        public double score;

        public Keyword(String word, double score) {
            this.word = word;
            this.score = score;
        }
    }

//    private NewsBean() {}
    public JSONObject getNewsJson() { return newsJson; }
    public List<String> getImageUrls() { return imageUrls; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getContent() { return content; }
    public String getPublisher() { return publisher; }
//    public List<String> getKeywords() { return this.keywords; }
    public List<Keyword> getKeywords() { return this.keywords; }
    public NewsDateTime getPublishTime() { return publishTime; }
    public String getValue(String key) { return newsJson.getString(key); }

    public void setContent(String content) {
        this.content = content;
    }

    public static NewsBean parse(JSONObject newsJson) {
        NewsBean news = new NewsBean();
        news.newsJson = newsJson;
        news.title = newsJson.getString("title");
        news.category = newsJson.getString("category");
        news.content = newsJson.getString("content");
        news.publisher = newsJson.getString("publisher");
        news.publishTime = NewsDateTime.parse(newsJson.getString("publishTime"));
//        news.read = false;

        String imageUrlsRaw = newsJson.getString("image");
        if (imageUrlsRaw.length() > 2) {
            news.imageUrls = Arrays.asList(imageUrlsRaw.substring(1, imageUrlsRaw.length() - 1).split(", "));
        } else {
            news.imageUrls = Collections.emptyList();
        }

//        news.favorite = DeFaultActivity.getAnyActivity().getDataRepository().isLocalFavorite(news);
        news.keywords = new ArrayList<>();
        JSONArray keywords = newsJson.getJSONArray("keywords");
        for (int i = 0; i < keywords.size(); i++) {
            JSONObject keyword = keywords.getJSONObject(i);
            news.keywords.add(new Keyword(keyword.getString("word"), keyword.getDouble("score")));
        }
        return news;
    }

    public static NewsBean decode(SavedNews savedNews) {
        NewsBean newsBean = parse(savedNews.getContent());
//        newsBean.read = savedNews.isRead();
        return newsBean;
    }

    public static List<NewsBean> decode(List<SavedNews> savedNews) {
        List<NewsBean> ret = new ArrayList<>();
        for (SavedNews news: savedNews) {
            ret.add(decode(news));
        }
        return ret;
    }

    //    public boolean isFavorite() { return favorite; }
//    public void setFavorite(boolean favorite) { this.favorite = favorite; }

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
