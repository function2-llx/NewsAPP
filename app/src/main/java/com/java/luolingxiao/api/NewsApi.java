package com.java.luolingxiao.api;

import android.graphics.Bitmap;
import android.os.Handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java.luolingxiao.bean.NewsBean;
import com.java.luolingxiao.bean.NewsDateTime;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import java.util.Arrays;
import java.util.List;

public class NewsApi {
    private static RequestQueue imageRequestQueue;
    public interface NewsCallback {
        void onReceived(List<NewsBean> newsBeanList);
        void onException(Exception e);
    }

    public static RequestQueue getImageRequestQueue() {
        if (imageRequestQueue == null) {
            synchronized (NewsApi.class) {
                if (imageRequestQueue == null) {
                    imageRequestQueue = NoHttp.newRequestQueue(50);
                }
            }
        }
        return imageRequestQueue;
    }

    public static class SearchParams {
        private int size = 10;
        private NewsDateTime start, end;
        private String words, category;

        public SearchParams setSize(int size) { this.size = size; return this; }
        public SearchParams setStartDate(NewsDateTime start) { this.start = start; return this; }
        public SearchParams setEndDate(NewsDateTime end) { this.end = end; return this; }
        public SearchParams setWords(String words) { this.words = words; return this; }
        public SearchParams setCategory(String category) { this.category = category; return this; }

        private static final String DEFAULT_URL = "https://api2.newsminer.net/svc/news/queryNewsList";
        FastJsonRequest toFastJsonRequest() {
            FastJsonRequest request = new FastJsonRequest(DEFAULT_URL);
            request.add("size", size);
            if (start != null) request.add("startDate", start.toString());
            if (end != null) request.add("endDate", end.toString());
            if (words != null) request.add("words", words);
            if (category != null) request.add("categories", category);
            return request;
        }
    }

    public static void requestNews(SearchParams params, NewsCallback callback) {
        Handler handler = new Handler();
        new Thread(() -> {
            FastJsonRequest request = params.toFastJsonRequest();
            Response<JSONObject> response = NoHttp.startRequestSync(request);
            if (response.getException() != null) {
                handler.post(() -> callback.onException(response.getException()));
            } else {
                JSONObject json = response.get();
                JSONArray newsJsonArray = json.getJSONArray("data");
                NewsBean[] newsBeans = new NewsBean[newsJsonArray.size()];
                Thread[] threads = new Thread[newsJsonArray.size()];
                for (int i = 0; i < threads.length; i++) {
                    int id = i;
                    threads[i] = new Thread(() -> {
                        JSONObject newsJson = newsJsonArray.getJSONObject(id);
                        newsBeans[id] = NewsBean.parse(newsJson);
                    });
                    threads[i].start();
                }
                try {
                    for (Thread thread: threads) thread.join();
                    handler.post(() -> callback.onReceived(Arrays.asList(newsBeans)) );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public interface ImageCallback {
        void onReceived(Bitmap bitmap);
        void onException(Exception e);
    }

    public static void requestImage(String url, ImageCallback callback) {
        Handler handler = new Handler();
        new Thread(() -> {
            Request<Bitmap> request = NoHttp.createImageRequest(url);
            getImageRequestQueue().add(0, request, new OnResponseListener<Bitmap>() {
                @Override
                public void onStart(int what) {

                }

                @Override
                public void onSucceed(int what, Response<Bitmap> response) {
                    handler.post(() -> callback.onReceived(response.get()));
                }

                @Override
                public void onFailed(int what, Response<Bitmap> response) {
                    handler.post(() -> callback.onException(response.getException()));
                }

                @Override
                public void onFinish(int what) {

                }
            });
        }).start();
    }
}

