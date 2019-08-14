package com.example.newsapp.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.newsapp.database.entity.SearchRecord;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface SearchRecordDao {
    @Insert(onConflict = REPLACE)
    void insert(SearchRecord... records);

    @Delete
    void delete(SearchRecord... records);

    @Query("SELECT * from search_records ORDER BY record ASC")
    List<SearchRecord> getAll();
}
