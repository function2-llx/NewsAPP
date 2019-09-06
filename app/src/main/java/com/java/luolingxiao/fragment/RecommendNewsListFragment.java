package com.java.luolingxiao.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.java.luolingxiao.api.NewsApi;
import com.java.luolingxiao.bean.NewsBean;
import com.java.luolingxiao.bean.NewsDateTime;
import com.yanzhenjie.nohttp.error.NetworkError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static java.lang.Math.min;

public class RecommendNewsListFragment extends NormalNewsListFragment {
    private List<NewsBean> dataCache = new ArrayList<>();

    public static RecommendNewsListFragment newInstance() {
        RecommendNewsListFragment fragment = new RecommendNewsListFragment();
        return fragment;
    }

    @Override
    public void getNewsListDataRequest(String type, int size, NewsDateTime endDate, boolean isOnRefresh, boolean isOnLoadMore) {
        size = Math.min(size, dataCache.size());
        List<NewsBean> newsBeanList = dataCache.subList(0, size);
        dataCache = dataCache.subList(size, dataCache.size());

        setNewsList(newsBeanList, isOnRefresh, isOnLoadMore);
        refreshLayout.finishLoadMore();
        refreshLayout.finishRefresh();
    }

    public List<NewsBean> getNewsListBestMatch(List<NewsBean> newsBeanListRead, List<NewsBean> newsBeanListNotRead, int size) {
        HashMap<String, Double> word2score = new HashMap<>();
        for (NewsBean newsBean :
                newsBeanListRead) {
            List<NewsBean.Keyword> keywords = newsBean.getKeywords();
            for (NewsBean.Keyword keyword :
                    keywords) {
                Double t = word2score.get(keyword.word);
                if (t == null) {
                    t = new Double(0);
                }
                word2score.put(keyword.word, t + keyword.score);

            }
        }
        List<NewsBean.Keyword> keywordsRead = new ArrayList<>();
        Double maxScore = new Double(0);
        for (String key :
                word2score.keySet()) {
            Double value = word2score.get(key);
            maxScore = Math.max(value, maxScore);
            keywordsRead.add(new NewsBean.Keyword(key, value));
        }
        for (int i = 0; i < keywordsRead.size(); ++i) {
            keywordsRead.get(i).score /= maxScore;
        }

        for (NewsBean newsBean :
                newsBeanListNotRead) {
            List<NewsBean.Keyword> keywords = new ArrayList<>();
            for (NewsBean.Keyword keyword :
                    newsBean.getKeywords()) {
                keywords.add(new NewsBean.Keyword(keyword.word, keyword.score));
            }

            double scoreSum = 0, scoreAccuracy = 0, scoreTotal = 0;
            for (NewsBean.Keyword keyword :
                    keywords) {
                int fg = 0;
                scoreTotal += keyword.score;
                for (NewsBean.Keyword keywordRead :
                        keywordsRead) {
                    if (keyword.word.equals(keywordRead.word)) {
                        keyword.score = min(keywordRead.score, keyword.score);
                        fg = 1;
                        break;
                    }
                }
                if (fg == 0) {
                    keyword.score = 0;
                }
                scoreSum += keyword.score;
            }


            scoreAccuracy = scoreTotal < 1e-6 ? 0 : scoreSum / scoreTotal;
            newsBean.score = scoreSum + scoreAccuracy < 1e-6 ? 0 : 2 * scoreSum * scoreAccuracy / (scoreSum + scoreAccuracy);
        }

        Collections.sort(newsBeanListNotRead, (newsBean, t1) -> {
//                if (Math.abs(newsBean.score - t1.score) < 1e-10) return 0;
            if (newsBean.score == t1.score) return 0;
            return newsBean.score > t1.score ? -1 : 1;
        });
        return newsBeanListNotRead.subList(0, min(newsBeanListNotRead.size(), size));
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        NewsApi.requestNews(new NewsApi.SearchParams()
                        .setSize(1000)
//                        .setStartDate()
                        .setEndDate(new NewsDateTime()),
                new NewsApi.NewsCallback() {
                    @Override
                    public void onReceived(List<NewsBean> newsBeanList) {
                        // 可能已经莫得了
                        if (getActivity() == null) return;

                        dataCache = getNewsListBestMatch(getDataRepository().getReadsSync(), newsBeanList, 20);
                        if (newsBeanList.size() > 0) {
                            lastDate = newsBeanList.get(newsBeanList.size() - 1).getPublishTime();
                        } else {
                            noMore = true;
                        }
                        getDataRepository().insertNews(newsBeanList);
                        getNewsListDataRequest("", chunkSize, lastDate, false, false);

                    }

                    @Override
                    public void onException(Exception e) {
                        e.printStackTrace();
                        if (e instanceof NetworkError) {
                            Toast.makeText(getContext(), "莫得网络啦，等下再来吧", Toast.LENGTH_SHORT).show();
                        }
                        refreshLayout.finishLoadMore();
                        refreshLayout.finishRefresh();
                    }
                }
        );

        return view;
    }
}
