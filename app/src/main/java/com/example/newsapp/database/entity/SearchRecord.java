package com.example.newsapp.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "search_records")
public class SearchRecord {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "record")
    private String record;

    public SearchRecord(@NonNull String record) {
        this.record = record;
    }

    @NonNull
    public String getRecord() {
        return record;
    }
}
