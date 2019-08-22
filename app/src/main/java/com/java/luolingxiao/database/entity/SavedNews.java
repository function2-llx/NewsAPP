package com.java.luolingxiao.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.java.luolingxiao.bean.NewsBean;

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

    public SavedNews(@NonNull NewsBean newsBean, @NonNull String channel, boolean read) {
        this.newsId = newsBean.getNewsId();
        this.channel = channel;
        this.read = read;
        this.content = newsBean.getNewsJson().toString();
    }

    public SavedNews(String newsId, String channel, boolean read, String content) {
        this.newsId = newsId;
        this.channel = channel;
        this.read = read;
        this.content = content;
    }

    public String getNewsId() { return newsId; }
    public boolean hasRead() { return read; }
    @NonNull public String getContent() {
        return content;
    }
    @NonNull public String getChannel() { return channel; }
}
