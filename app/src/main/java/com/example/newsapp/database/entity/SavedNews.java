package com.example.newsapp.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.newsapp.bean.NewsBean;

@Entity(tableName = "saved_news")
public class SavedNews {
    @NonNull
    @PrimaryKey
    private String newsId;

    @NonNull
    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "read")
    private boolean read;

    @NonNull
    @ColumnInfo(name = "content")
    private String content;

    public SavedNews(@NonNull NewsBean newsBean, @NonNull String category, boolean read) {
        this.newsId = newsBean.getNewsId();
        this.category = category;
        this.read = read;
        this.content = newsBean.getNewsJson().toString();
    }

    public SavedNews(String newsId, String category, boolean read, String content) { }

    public String getNewsId() { return newsId; }
    public boolean hasRead() { return read; }
    @NonNull public String getContent() {
        return content;
    }
    @NonNull public String getCategory() { return category; }
}
