package com.java.luolingxiao;

import android.os.Handler;

import androidx.annotation.NonNull;

import com.java.luolingxiao.bean.NewsBean;
import com.java.luolingxiao.database.AppDatabase;
import com.java.luolingxiao.database.entity.SavedNews;
import com.java.luolingxiao.database.entity.SearchRecord;

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

    public void insertNews(String channel, NewsBean... newsBeans) {
        new SimpleAsyncTask() {
            @Override
            protected void doInBackGround() {
                List<SavedNews> savedNews = new ArrayList<>();
                for (NewsBean newsBean: newsBeans) {
                    savedNews.add(SavedNews.encode(channel, newsBean));
                }
                database.savedNewsDao().insert(savedNews);
            }
        }.start();
    }

    public void insertNews(String channel, List<NewsBean> newsBeans) {
        insertNews(channel, newsBeans.toArray(new NewsBean[0]));
    }

    public void deleteNews(String channel, NewsBean... newsBeans) {
        new SimpleAsyncTask() {
            @Override
            protected void doInBackGround() {
                List<SavedNews> newsBeanList = SavedNews.encode(channel, newsBeans);
                database.savedNewsDao().delete(newsBeanList);
            }
        }.start();
    }

    public void deleteNews(String channel, List<NewsBean> newsBeans) {
        deleteNews(channel, newsBeans.toArray(new NewsBean[0]));
    }

    public interface OnReceiveSavedNewsCallback {
        void onReceive(List<NewsBean> savedNews);
    }

    public void getSavedNews(String channel, OnReceiveSavedNewsCallback callback) {
        new SimpleAsyncTask() {
            private List<NewsBean> newsBeans;
            @Override
            protected void doInBackGround() {
                newsBeans = NewsBean.decode(database.savedNewsDao().getSavedNews(channel));
            }

            @Override
            protected void onComplete() {
                callback.onReceive(newsBeans);
            }
        }.start();
    }
}
