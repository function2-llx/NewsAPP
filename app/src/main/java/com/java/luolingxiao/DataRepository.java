package com.java.luolingxiao;

import android.os.Handler;

import androidx.annotation.NonNull;

import com.java.luolingxiao.bean.NewsBean;
import com.java.luolingxiao.database.AppDatabase;
import com.java.luolingxiao.database.entity.SavedNews;
import com.java.luolingxiao.database.entity.SearchRecord;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataRepository {
    private static DataRepository instance = null;
    private AppDatabase database;

    private Set<NewsBean> localFavoriteCache = null;

    private DataRepository(AppDatabase database) { this.database = database; }

    public static DataRepository getInstance(AppDatabase database) {
        if (instance == null) {
            synchronized (DataRepository.class) {
                if (instance == null) {
                    instance = new DataRepository(database);
                    instance.getLocalFavoritesSync();
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

    public List<String> getAllSearchRecordsSync() {
        List<String> records = new ArrayList<>();
        Thread thread = new Thread(() -> {
            for (SearchRecord record: database.searchRecordDao().getAll()) {
                records.add(record.getRecord());
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Throwable ignored) {}
        return records;
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

    public void setRead(NewsBean newsBean, boolean read) {
        new Thread(() -> database.savedNewsDao().setRead(newsBean.getNewsId(), read)).start();
    }

    public void setFavorite(NewsBean newsBean, boolean favorite) {
        new Thread(() -> database.savedNewsDao().setFavorite(newsBean.getNewsId(), favorite)).start();
        if (favorite) {
            localFavoriteCache.add(newsBean);
        } else {
            localFavoriteCache.remove(newsBean);
        }
    }

    public boolean isLocalFavorite(NewsBean newsBean) {
        return localFavoriteCache.contains(newsBean);
    }

    public void insertNews(NewsBean... newsBeans) {
        new SimpleAsyncTask() {
            @Override
            protected void doInBackGround() {
                List<SavedNews> savedNews = new ArrayList<>();
                for (NewsBean newsBean: newsBeans) {
                    savedNews.add(SavedNews.encode(newsBean));
                }
                database.savedNewsDao().insert(savedNews);
            }
        }.start();
    }

    public void insertNews(List<NewsBean> newsBeans) {
        insertNews(newsBeans.toArray(new NewsBean[0]));
    }

    public void deleteNews(NewsBean... newsBeans) {
        new SimpleAsyncTask() {
            @Override
            protected void doInBackGround() {
                List<SavedNews> newsBeanList = SavedNews.encode(newsBeans);
                database.savedNewsDao().delete(newsBeanList);
            }
        }.start();
    }

    public void deleteNews(List<NewsBean> newsBeans) {
        deleteNews(newsBeans.toArray(new NewsBean[0]));
    }

    public interface OnReceiveSavedNewsCallback {
        void onReceive(List<NewsBean> savedNews);
    }

    private static abstract class GetNewsAsyncTask extends SimpleAsyncTask{
        List<NewsBean> newsBeans;
        private OnReceiveSavedNewsCallback callback;

        GetNewsAsyncTask(OnReceiveSavedNewsCallback callback) {
            this.callback = callback;
        }

        @Override
        protected void onComplete() {
            callback.onReceive(newsBeans);
        }
    }

    public void getSavedNews(int limit, int offset, OnReceiveSavedNewsCallback callback) {
        new GetNewsAsyncTask(callback) {
            @Override
            protected void doInBackGround() {
                newsBeans = NewsBean.decode(database.savedNewsDao().getSavedNews(limit, offset));
            }
        }.start();
    }

    public void getSavedNewsByCategory(String category, int limit, int offset, OnReceiveSavedNewsCallback callback) {
        if (category.equals("首页")) {
            getSavedNews(limit, offset, callback);
        } else {
            new GetNewsAsyncTask(callback) {
                @Override
                protected void doInBackGround() {
                    newsBeans = NewsBean.decode(database.savedNewsDao().getSavedNewsByCategory(category, limit, offset));
                }
            }.start();
        }
    }

    public List<NewsBean> getLocalFavoritesSync() {
        if (localFavoriteCache == null) {
            synchronized (this) {
                if (localFavoriteCache == null) {
                    localFavoriteCache = new HashSet<>();
                    Thread thread = new Thread(() -> localFavoriteCache.addAll(NewsBean.decode(database.savedNewsDao().getFavoriteSavedNews())));
                    thread.start();
                    try  {
                        thread.join();
                    } catch (Throwable ignored) {}
                }
            }
        }
        return new ArrayList<>(localFavoriteCache);
    }

    public List<NewsBean> getReadsSync() {
        List<NewsBean> reads = new ArrayList<>();
        Thread thread = new Thread(() -> {
            reads.addAll(NewsBean.decode(database.savedNewsDao().getReads()));
        });
        thread.start();
        try { thread.join(); } catch (Throwable ignored) {}
        return reads;
    }

    void clearReads(Runnable callback) {
        new SimpleAsyncTask() {
            @Override
            protected void doInBackGround() {
                database.savedNewsDao().clearReads();
            }

            @Override
            protected void onComplete() {
                callback.run();
            }
        }.start();
    }
}