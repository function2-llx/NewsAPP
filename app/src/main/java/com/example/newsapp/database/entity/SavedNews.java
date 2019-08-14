package com.example.newsapp.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "saved_news")
public class SavedNews {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "category")
    @NonNull
    private String category;

    public SavedNews(@NonNull String content, @NonNull String category) {
        this.content = content;
        this.category = category;
    }

    @NonNull
    @ColumnInfo(name = "content")
    private String content;

    @NonNull public String getContent() {
        return content;
    }


}
