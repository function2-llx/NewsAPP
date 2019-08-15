package com.example.newsapp.api;

import android.graphics.Bitmap;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.newsapp.bean.NewsBean;
import com.example.newsapp.bean.NewsDateTime;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.StringRequest;

import java.util.ArrayList;
import java.util.List;

public class NewsApi {
    public interface NewsCallback {
        void onReceived(List<NewsBean> newsBeanList);
        void onException(Exception e);
    }

    private static class FastJsonRequest extends Request<JSONObject> {

        public FastJsonRequest(String url) {
            this(url, RequestMethod.GET);
        }

        public FastJsonRequest(String url, RequestMethod requestMethod) {
            super(url, requestMethod);
            setAccept(Headers.HEAD_VALUE_CONTENT_TYPE_JSON);
        }

        @Override
        public JSONObject parseResponse(Headers responseHeaders, byte[] responseBody) {
            String result = StringRequest.parseResponseString(responseHeaders, responseBody);
            return JSON.parseObject(result);
        }
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

    public static void requestNews(SearchParams params, boolean imagesRequired,NewsCallback callback) {
        Handler handler = new Handler();
        new Thread(() -> {
            FastJsonRequest request = params.toFastJsonRequest();
            Response<JSONObject> response = NoHttp.startRequestSync(request);
            if (response.getException() != null) {
                handler.post(() -> callback.onException(response.getException()));
            } else {
                JSONObject json = response.get();
                JSONArray newsJsonArray = json.getJSONArray("data");
                List<NewsBean> newsBeanList = new ArrayList<>();
                for (Object newsJson: newsJsonArray) {
                    newsBeanList.add(NewsBean.parse((JSONObject)newsJson, imagesRequired));
                }
                handler.post(() -> callback.onReceived(newsBeanList) );
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
            Response<Bitmap> response = NoHttp.startRequestSync(request);
            if (response.getException() != null) {
                handler.post(() -> callback.onException(response.getException()));
            } else {
                Bitmap bitmap = response.get();
                handler.post(() -> callback.onReceived(bitmap));
            }
        }).start();
    }
}
