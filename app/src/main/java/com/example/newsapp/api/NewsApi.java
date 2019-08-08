package com.example.newsapp.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.newsapp.bean.NewsBean;
import com.example.newsapp.bean.NewsDateTime;
import com.yanzhenjie.nohttp.BasicRequest;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.RestRequest;
import com.yanzhenjie.nohttp.rest.StringRequest;


import java.util.ArrayList;
import java.util.List;

public class NewsApi {
    interface Callback {
        void onNewsReceived(List<NewsBean> newsBeanList);
    }

    static class FastJsonRequest extends Request<JSONObject> {

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
            return JSON.parseObject(result); // StringRequest就是少了这句话而已。
        }
    }
    public static class SearchParams {
        private int size;
        private NewsDateTime start, end;
        private String words, category;
        public SearchParams(int size, NewsDateTime start, NewsDateTime end, String words, String category) {
            this.size = size;
            this.start = start;
            this.end = end;
            this.words = words;
            this.category = category;
        }

        public SearchParams(int size, NewsDateTime start, NewsDateTime end, String words) { this(size, start, end, words, ""); }
        public SearchParams(int size, NewsDateTime start, NewsDateTime end) { this(size, start, end, ""); }
        public SearchParams(int size) {this(size, new NewsDateTime(), new NewsDateTime()); }
        public SearchParams() { this(10); }

        static final String DEFAULT_URL = "https://api2.newsminer.net/svc/news/queryNewsList";

//        String toUrl() {
//            return "https://api2.newsminer.net/svc/news/queryNewsList?size="
//                    + size + "&startDate=" + start + "&endDate" + end + "&words=" + words + "&categories=" +category;
//        }

        FastJsonRequest toFastJsonRequest() {
            return (FastJsonRequest)new FastJsonRequest(DEFAULT_URL)
                    .add("size", size)
                    .add("startDate", start.toString())
                    .add("endDate", end.toString())
                    .add("words", words)
                    .add("categories", category);
        }

    }

    public static void requestNews(SearchParams params, Callback callback) {
        new Thread(() -> {
            FastJsonRequest request = params.toFastJsonRequest();
            Response<JSONObject> response = NoHttp.startRequestSync(request);
            JSONObject json = response.get();
            JSONArray newsJsonArray = json.getJSONArray("data");
            for (Object newsObject: newsJsonArray) {

            }
            List<NewsBean> newsBeanList = new ArrayList<>();
            callback.onNewsReceived(newsBeanList);
        }).start();
    }
}
