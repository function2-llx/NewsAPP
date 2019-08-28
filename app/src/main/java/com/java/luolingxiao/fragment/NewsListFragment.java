package com.java.luolingxiao.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.java.luolingxiao.DataRepository;
import com.java.luolingxiao.DeFaultActivity;
import com.java.luolingxiao.NewsActivity;
import com.java.luolingxiao.R;
import com.java.luolingxiao.adapters.BaseRecyclerAdapter;
import com.java.luolingxiao.adapters.SmartViewHolder;
import com.java.luolingxiao.api.NetworkChecker;
import com.java.luolingxiao.api.NewsApi;
import com.java.luolingxiao.bean.NewsBean;
import com.java.luolingxiao.bean.NewsDateTime;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;



public class NewsListFragment extends Fragment {
    private ArrayList<NewsBean> data = new ArrayList<>();

    private String mNewsId;
    private String mNewsType;
    private int mStartPage=0;
    private boolean noMore = false;
//    Context a;

    // 标志位，标志已经初始化完成。
    RefreshLayout refreshLayout;
    private boolean isPrepared;
    private boolean isVisible;
    private NewsDateTime lastDate;
    private class Model {
        int imageId;
        int avatarId;
        int position;
        String name;
        String nickname;
        String imageUrl;
    }
    private BaseRecyclerAdapter<Model> mAdapter;

    private DataRepository getDataRepository() {
        return ((DeFaultActivity)getActivity()).getDataRepository();
    }

    public NewsListFragment() {}

    public static NewsListFragment newInstance(String channel, String words) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("channel", channel);
        bundle.putString("words", words);
        fragment.setArguments(bundle);
        return fragment;
    }

    private String getChannel() {
        assert this.getArguments() != null;
        return this.getArguments().getString("channel");
    }

    private String getWords() {
        assert this.getArguments() != null;
        return getArguments().getString("words");
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);
        refreshLayout = view.findViewById(R.id.refreshLayout);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        lastDate = new NewsDateTime();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setBackgroundColor(getResources().getColor(R.color.gray, null));
        recyclerView.setAdapter(mAdapter = new BaseRecyclerAdapter<Model>(R.layout.item_practice_repast) {
            @Override
            protected void onBindViewHolder(SmartViewHolder holder, Model model, int position) {
                NewsBean newsBean = data.get(position);
                holder.myText(R.id.name, model.name);
                holder.myText(R.id.nickname, model.nickname);
                if (newsBean.getImageUrls().size() > 0) {
//                    holder.image_init(R.id.image, R.drawable.);
                    holder.image(R.id.image, model.imageUrl);
                }
//                holder.setPosition(model.position);
//                holder.image(R.id.image, model.imageId);
            }


            @Override
            public int getItemViewType(int position) {
                NewsBean newsBean = data.get(position);
                if (newsBean.getImageUrls().size() == 0) {
                    return 0;
//                    return R.layout.item_news_no_picture;
                } else {
                    return 1;
//                    return R.layout.item_practice_repast;
                }
            }

            @Override
            public SmartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                if (viewType == 0) {
                    return new SmartViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_no_picture, parent, false),mListener);
                } else {
                    return new SmartViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_practice_repast, parent, false),mListener);
                }
            }

            //            @Override
//            public int getViewTypeCount() {
//                return super.getViewTypeCount();
//            }
        });

        mAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                LinearLayout subView = view.findViewById(R.id.linearLayout);
//                subView.setBackgroundColor(Color.parseColor("#FFF0F0F0"));
                NewsBean newsBean = data.get(position);
                newsBean.setRead(true);
                getDataRepository().insertNews(getChannel(), newsBean);
                NewsActivity.startAction(getContext(), newsBean);
//                NewsActivity.startAction(getContext(), data.get(position));
                TextView subView = view.findViewById(R.id.name);
                subView.setTextColor(getResources().getColor(R.color.toast_stroke_gray));
                subView = view.findViewById(R.id.nickname);
                subView.setTextColor(getResources().getColor(R.color.toast_stroke_gray));
            }
        });

        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                refreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        recyclerView.set
                        getNewsListDataRequest("", 6, lastDate, true, false);
//                        refreshLayout.finishRefresh();
                        refreshLayout.resetNoMoreData();//setNoMoreData(false);//恢复上拉状态
                    }
                }, 100);
            }
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                refreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        if (mAdapter.getCount() > 100)
                        if (noMore) {
                            Toast.makeText(getContext(), "数据全部加载完毕", Toast.LENGTH_SHORT).show();
                            refreshLayout.finishLoadMoreWithNoMoreData();//设置之后，将不会再触发加载事件
                        } else {
                            getNewsListDataRequest("", 6, lastDate, false, true);
//                            mAdapter.loadMore(loadModels());
                        }
                    }
                }, 100);
            }
        });

//        refreshLayout.getLayout().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                refreshLayout.setHeaderInsetStart(SmartUtil.px2dp(toolbar.getHeight()));
//            }
//        }, 500);

        if (NetworkChecker.isNetworkConnected(getContext())) {
            getNewsListDataRequest("", 1, lastDate, false, false);
        } else {
            Toast.makeText(getContext(), "离线模式", Toast.LENGTH_SHORT).show();
            getDataRepository().getSavedNews(getChannel(), 6, 0, new DataRepository.OnReceiveSavedNewsCallback() {
                @Override
                public void onReceive(List<NewsBean> savedNews) {
                    setNewsList(savedNews, false, false);
                }
            });
//            setNewsList(getDataRepository().getSavedNews(getChannel(), new DataRepository.OnReceiveSavedNewsCallback() {
//                @Override
//                public void onReceive(List<NewsBean> savedNews) {
//
//                }
//            }), false, false);
//            Toast.makeText(getContext(), getChannel() + ":没网", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    public void getNewsListDataRequest(String type, int size, NewsDateTime endDate, boolean isOnRefresh, boolean isOnLoadMore) {
        if (endDate != null) {
            endDate = endDate.minusSeconds(1);
        }
        NewsApi.requestNews(new NewsApi.SearchParams()
                        .setSize(size)
                        .setWords(getWords())
                        .setCategory(getChannel().equals("首页")? "" : getChannel())
//                        .setStartDate()
                        .setEndDate(endDate),
                new NewsApi.NewsCallback() {
                    @Override
                    public void onReceived(List<NewsBean> newsBeanList) {
                        if (newsBeanList.size() > 0) {
                            lastDate = newsBeanList.get(newsBeanList.size() - 1).getPublishTime();
                        } else {
                            noMore = true;
                        }
                        getDataRepository().insertNews(getChannel(), newsBeanList);
                        setNewsList(newsBeanList, isOnRefresh, isOnLoadMore);

                        refreshLayout.finishLoadMore();
                        refreshLayout.finishRefresh();
//                        returnNewsListData(newsBeanList);
//                        if (newsBeanList.isEmpty()) {
//                            Toast.makeText(MainActivity.this, "莫得新闻了，等哈再来哈", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(MainActivity.this, newsBeanList.get(0).getTitle(), Toast.LENGTH_SHORT).show();
//                        }
                    }

                    @Override
                    public void onException(Exception e) {
                        e.printStackTrace();
//                        if (e instanceof NetworkError) {
//                            Toast.makeText(MainActivity.this, "莫得网络啦，等下再来吧", Toast.LENGTH_SHORT).show();
//                        }
                    }
                }
        );
    }

    public void setNewsList(List<NewsBean> newsBeanList, boolean isOnRefresh, boolean isOnLoadMore) {
        ArrayList<Model> newsList = new ArrayList<>();
        if (isOnRefresh) {
            data.clear();
        }
        for (int i = 0; i < newsBeanList.size(); ++i) {
            int finalI = i;
            List<String> images = newsBeanList.get(finalI).getImageUrls();
            data.add(newsBeanList.get(finalI));
            newsList.add(new Model() {{
                this.name = newsBeanList.get(finalI).getTitle();
                this.nickname = newsBeanList.get(finalI).getContent();
                if (this.nickname.length() > 20) {
                    this.nickname = this.nickname.substring(0, 20) + "...";
                }
                if (images.size() > 0) this.imageUrl = images.get(0);
                this.position = data.size() - 1;
            }});
        }
        if (isOnRefresh) {
            mAdapter.refresh(newsList);
        } else mAdapter.loadMore(newsList);
    }

    private Collection<Model> loadModels() {
        return Arrays.asList(
                new Model() {{
                    this.name = "但家香酥鸭";
                    this.nickname = "爱过那张脸";
//                    this.imageId = R.mipmap.image_practice_repast_1;
                }}, new Model() {{
                    this.name = "香菇蒸鸟蛋";
                    this.nickname = "淑女算个鸟";
//                    this.imageId = R.mipmap.image_practice_repast_2;
                }}, new Model() {{
                    this.name = "花溪牛肉粉";
                    this.nickname = "性感妩媚";
//                    this.imageId = R.mipmap.image_practice_repast_3;
                }}, new Model() {{
                    this.name = "破酥包";
                    this.nickname = "一丝丝纯真";
//                    this.imageId = R.mipmap.image_practice_repast_4;
                }}, new Model() {{
                    this.name = "盐菜饭";
                    this.nickname = "等着你回来";
//                    this.imageId = R.mipmap.image_practice_repast_5;
                }}, new Model() {{
                    this.name = "米豆腐";
                    this.nickname = "宝宝树人";
//                    this.imageId = R.mipmap.image_practice_repast_6;
                }});
    }
//    public void scrolltoTop() {
//        irc.smoothScrollToPosition(0);
//    }
}
