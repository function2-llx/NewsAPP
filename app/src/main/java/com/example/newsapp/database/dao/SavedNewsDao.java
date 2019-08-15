package com.example.newsapp.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.newsapp.database.entity.SavedNews;

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
