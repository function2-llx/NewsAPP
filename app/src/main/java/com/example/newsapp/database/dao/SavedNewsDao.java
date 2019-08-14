package com.example.newsapp.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;

import com.example.newsapp.database.entity.SavedNews;

@Dao
public interface SavedNewsDao {
    @Insert
    void insert(SavedNews... savedNews);

    @Delete
    void delete(SavedNews... savedNews);


//    List<SavedNews>
}
