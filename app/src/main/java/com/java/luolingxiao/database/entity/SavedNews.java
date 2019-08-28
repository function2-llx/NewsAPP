package com.java.luolingxiao.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.java.luolingxiao.bean.NewsBean;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "saved_news")
public class SavedNews {
    @NonNull
    @PrimaryKey
    private String newsId;

    // 也可以是收藏
    @NonNull
    @ColumnInfo(name = "channel")
    private String channel;

    @ColumnInfo(name = "read")
    private boolean read;

    @NonNull
    @ColumnInfo(name = "content")
    private String content;

    private SavedNews(@NonNull NewsBean newsBean, @NonNull String channel) {
        this.newsId = newsBean.getNewsId();
        this.channel = channel;
        this.read = newsBean.isRead();
        this.content = newsBean.getNewsJson().toString();
    }

    // support room
    public SavedNews(String newsId, String channel, boolean read, String content) {
        this.newsId = newsId;
        this.channel = channel;
        this.read = read;
        this.content = content;
    }

    public static SavedNews encode(String channel, NewsBean newsBean) {
        return new SavedNews(newsBean, channel);
    }

    public static List<SavedNews> encode(String channel, NewsBean... newsBeans) {
        List<SavedNews> ret = new ArrayList<>();
        for (NewsBean newsBean: newsBeans) {
            ret.add(encode(channel, newsBean));
        }
        return ret;
    }

    public String getNewsId() { return newsId; }
    public boolean isRead() { return read; }
    @NonNull public String getContent() {
        return content;
    }
    @NonNull public String getChannel() { return channel; }
}
