package com.java.luolingxiao.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.java.luolingxiao.database.entity.SavedNews;

import java.util.List;

@Dao
public interface SavedNewsDao {
    @Insert
    void insert(SavedNews... savedNews);

    @Delete
    void delete(SavedNews... savedNews);

    @Query("select * from saved_news where channel == :channel")
    List<SavedNews> getSavedNews(String channel);
}
