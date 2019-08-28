package com.java.luolingxiao.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.java.luolingxiao.database.entity.SavedNews;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface SavedNewsDao {
    @Insert(onConflict = REPLACE)
    void insert(SavedNews... savedNews);

    @Insert(onConflict = REPLACE)
    void insert(List<SavedNews> savedNews);

    @Delete
    void delete(SavedNews... savedNews);

    @Delete
    void delete(List<SavedNews> savedNews);

    @Query("select * from saved_news where channel == :channel")
    List<SavedNews> getSavedNews(String channel);
}
