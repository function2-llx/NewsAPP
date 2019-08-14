package com.example.newsapp.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "saved_news")
public class SavedNews {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "read")
    private boolean read;

    @NonNull
    @ColumnInfo(name = "category")
    private String category;

    @NonNull
    @ColumnInfo(name = "content")
    private String content;

    public SavedNews(int id, boolean read, @NonNull String content, @NonNull String category) {
        this.id = id;
        this.read = read;
        this.content = content;
        this.category = category;
    }

    public int getId() { return id; }
    public boolean isRead() { return read; }
    @NonNull public String getContent() {
        return content;
    }
    @NonNull public String getCategory() { return category; }
}
