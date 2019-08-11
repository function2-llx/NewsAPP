package com.example.newsapp;

import android.os.Handler;

import androidx.annotation.NonNull;

import com.example.newsapp.database.AppDatabase;
import com.example.newsapp.database.entity.SearchRecord;

import java.util.ArrayList;
import java.util.List;

public class DataRepository {
    private static DataRepository instance = null;
    private AppDatabase database;

    private DataRepository(AppDatabase database) {
        this.database = database;
    }

    public static DataRepository getInstance(AppDatabase database) {
        if (instance == null) {
            synchronized (DataRepository.class) {
                if (instance == null) {
                    instance = new DataRepository(database);
                }
            }
        }
        return instance;
    }

    public interface GetAllSearchRecordsCallback {
        void onReceiveRecords(List<String> records);
    }
    public void getAllSearchRecords(GetAllSearchRecordsCallback callback) {
        Handler handler = new Handler();
        new Thread(() -> {
            List<String> records = new ArrayList<>();
            for (SearchRecord record: database.searchHistoryDao().getAll()) {
                records.add(record.getRecord());
            }
            handler.post(() -> callback.onReceiveRecords(records));
        }).start();
    }

    public void insertSearchRecords(@NonNull String... records) {
        new Thread(() -> {
            for (String record: records) {
                database.searchHistoryDao().insert(new SearchRecord(record));
            }
        }).start();
    }

    public void deleteSearchRecords(@NonNull String... records) {
        new Thread(() -> {
            for (String record: records) {
                database.searchHistoryDao().delete(new SearchRecord(record));
            }
        }).start();
    }
}
