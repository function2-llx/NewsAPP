package com.example.newsapp;

import android.os.Handler;

import androidx.annotation.NonNull;

import com.example.newsapp.database.AppDatabase;
import com.example.newsapp.database.entity.SavedNews;
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

    private abstract static class AsyncTask {
        protected void doInBackGround() {}
        protected void onComplete() {}

        public void start() {
            Handler handler = new Handler();
            new Thread(() -> {
                doInBackGround();
                handler.post(this::onComplete);
            });
        }
    };

    public void getAllSearchRecords(GetAllSearchRecordsCallback callback) {
        Handler handler = new Handler();
        new Thread(() -> {
            List<String> records = new ArrayList<>();
            for (SearchRecord record: database.searchRecordDao().getAll()) {
                records.add(record.getRecord());
            }
            handler.post(() -> callback.onReceiveRecords(records));
        }).start();
    }

    public void insertSearchRecords(@NonNull String... records) {
        new Thread(() -> {
            for (String record: records) {
                database.searchRecordDao().insert(new SearchRecord(record));
            }
        }).start();
    }

    public void deleteSearchRecords(@NonNull String... records) {
        new Thread(() -> {
            for (String record: records) {
                database.searchRecordDao().delete(new SearchRecord(record));
            }
        }).start();
    }

    public void insertSavedNews(SavedNews... savedNews) {
        new AsyncTask() {
            @Override
            protected void doInBackGround() {
                database.savedNewsDao().insert(savedNews);
            }
        }.start();
    }

    public void deleteSavedNews(SavedNews... savedNews) {
        new AsyncTask() {
            @Override
            protected void doInBackGround() {
                database.savedNewsDao().delete(savedNews);
            }
        }.start();
    }

    public interface OnReceiveSavedNewsCallback {
        void onReceive(List<SavedNews> savedNews);
    }

    public void getSavedNews(String category, OnReceiveSavedNewsCallback callback) {
        new AsyncTask() {
            private List<SavedNews> savedNews;
            @Override
            protected void doInBackGround() {
                savedNews = database.savedNewsDao().getSavedNews(category);
            }

            @Override
            protected void onComplete() {
                callback.onReceive(savedNews);
            }
        }.start();
    }
}
