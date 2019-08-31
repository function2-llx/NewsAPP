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
    @ColumnInfo(name = "news_id")
    private String newsId;

    @NonNull
    @ColumnInfo(name = "category")
    private String category;

    @NonNull
    @ColumnInfo(name = "favorite")
    private boolean favorite;

    @ColumnInfo(name = "read")
    private boolean read;

    @NonNull
    @ColumnInfo(name = "content")
    private String content;

    private SavedNews(@NonNull NewsBean newsBean) {
        this.newsId = newsBean.getNewsId() ;
        this.category = newsBean.getCategory();
        this.favorite = newsBean.isFavorite();
        this.read = newsBean.isRead();
        this.content = newsBean.getNewsJson().toString();
    }

    // support room
    public SavedNews(@NonNull String newsId, @NonNull String category, boolean favorite, boolean read, @NonNull String content) {
        this.newsId = newsId;
        this.category = category;
        this.favorite = favorite;
        this.read = read;
        this.content = content;
    }

    public static SavedNews encode(NewsBean newsBean) {
        return new SavedNews(newsBean);
    }

    public static List<SavedNews> encode(NewsBean... newsBeans) {
        List<SavedNews> ret = new ArrayList<>();
        for (NewsBean newsBean: newsBeans) {
            ret.add(encode(newsBean));
        }
        return ret;
    }

    @NonNull public String getNewsId() { return newsId; }
    public boolean isRead() { return read; }
    @NonNull public boolean isFavorite() { return favorite; }
    @NonNull public String getContent() {
        return content;
    }
    @NonNull public String getCategory() { return category; }
}
