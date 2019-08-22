package com.java.luolingxiao.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.java.luolingxiao.database.dao.SavedNewsDao;
import com.java.luolingxiao.database.dao.SearchRecordDao;
import com.java.luolingxiao.database.entity.SavedNews;
import com.java.luolingxiao.database.entity.SearchRecord;


@Database(entities = {SearchRecord.class, SavedNews.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance = null;

    public abstract SearchRecordDao searchRecordDao();
    public abstract SavedNewsDao savedNewsDao();

    public static AppDatabase getInstance(final Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "app_database").build();
                }
            }
        }
        return instance;
    }
}
