package com.java.luolingxiao.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.java.luolingxiao.database.entity.SavedNews;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;

@Dao
public interface SavedNewsDao {
    @Insert(onConflict = IGNORE)
    void insert(SavedNews... savedNews);

    @Insert(onConflict = IGNORE)
    void insert(List<SavedNews> savedNews);

    @Query("update saved_news set read = :read where news_id == :id")
    void setRead(String id, boolean read);

    @Query("update saved_news set favorite = :favorite where news_id == :id")
    void setFavorite(String id, boolean favorite);

    @Delete
    void delete(SavedNews... savedNews);

    @Delete
    void delete(List<SavedNews> savedNews);

    @Query("select * from saved_news limit :limit offset :offset")
    List<SavedNews> getSavedNews(int limit, int offset);

    @Query("select * from saved_news where category == :category limit :limit offset :offset")
    List<SavedNews> getSavedNewsByCategory(String category, int limit, int offset);

    @Query("select * from saved_news where favorite == 1")
    List<SavedNews> getFavoriteSavedNews();

    @Query("select * from saved_news where favorite == 1 limit :limit offset :offset")
    List<SavedNews> getFavoriteSavedNews(int limit, int offset);

    @Query("select * from saved_news where read == 1")
    List<SavedNews> getReads();

    @Query("update saved_news set read = 0")
    void clearReads();
}
