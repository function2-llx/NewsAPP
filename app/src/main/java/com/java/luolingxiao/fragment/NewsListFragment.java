package com.java.luolingxiao.fragment;


import android.graphics.Color;
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
import com.java.luolingxiao.adapter.BaseRecyclerAdapter;
import com.java.luolingxiao.adapter.SmartViewHolder;
import com.java.luolingxiao.api.NetworkChecker;
import com.java.luolingxiao.api.NewsApi;
import com.java.luolingxiao.bean.NewsBean;
import com.java.luolingxiao.bean.NewsDateTime;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.yanzhenjie.nohttp.error.NetworkError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class NewsListFragment extends Fragment {
    private ArrayList<NewsBean> data = new ArrayList<>();
    private ArrayList<Boolean> data_read = new ArrayList<>();

    private String mNewsId;
    private String mNewsType;
    private int mStartPage = 0;
    private boolean noMore = false;
    private boolean offline = false;
//    Context a;

    // 标志位，标志已经初始化完成。
    RefreshLayout refreshLayout;
    private boolean isPrepared;
    private boolean isVisible;
    private NewsDateTime lastDate;
    private int offset;

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
        return ((DeFaultActivity) getActivity()).getDataRepository();
    }

    public NewsListFragment() {
    }

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
                holder.myText(R.id.name, model.name, data_read.get(position));
                if (holder.getItemViewType() < 2)
                    holder.myText(R.id.nickname, model.nickname, data_read.get(position));

                List<String> imageUrls = data.get(model.position).getImageUrls();

                if (newsBean.getImageUrls().size() >= 3) {
                    holder.image(R.id.image1, imageUrls.get(0));
                    holder.image(R.id.image2, imageUrls.get(1));
                    holder.image(R.id.image3, imageUrls.get(2));
                } else if (newsBean.getImageUrls().size() > 0) {
//                    holder.image_init(R.id.image, R.drawable.);
                    holder.image(R.id.image, model.imageUrl);
                }

                holder.text(R.id.time, data.get(model.position).getPublisher() + " " + data.get(model.position).getPublishTime().toString());
//                holder.setPosition(model.position);
//                holder.image(R.id.image, model.imageId);
            }


            @Override
            public int getItemViewType(int position) {
                NewsBean newsBean = data.get(position);
                if (newsBean.getImageUrls().size() == 0) {
                    return 0;
//                    return R.layout.item_news_no_picture;
                } else if (newsBean.getImageUrls().size() < 3) {
                    return 1;
//                    return R.layout.item_practice_repast;
                } else {
                    return 2;
                }
            }

            @Override
            public SmartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                if (viewType == 0) {
                    return new SmartViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_no_picture, parent, false), mListener);
                } else if (viewType == 1) {
                    return new SmartViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_practice_repast, parent, false), mListener);
                } else {
                    return new SmartViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_3_picture, parent, false), mListener);

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
                subView.setTextColor(Color.parseColor("#1A000000"));
                subView = view.findViewById(R.id.nickname);
                if (subView != null) {
                    subView.setTextColor(Color.parseColor("#1A000000"));
                }
                data_read.set(position, true);
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

        getNewsListDataRequest("", 1, lastDate, false, false);

        return view;
    }

    public void getNewsListDataRequest(String type, int size, NewsDateTime endDate, boolean isOnRefresh, boolean isOnLoadMore) {
        if (endDate != null) {
            endDate = endDate.minusSeconds(1);
        }

        if (NetworkChecker.isNetworkConnected(getContext())) {
            if (offline) Toast.makeText(getContext(), "进入在线模式", Toast.LENGTH_SHORT).show();
            offline = false;
            NewsApi.requestNews(new NewsApi.SearchParams()
                            .setSize(size)
                            .setWords(getWords())
                            .setCategory(getChannel().equals("首页") ? "" : getChannel())
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
                            if (e instanceof NetworkError) {
                                Toast.makeText(getContext(), "莫得网络啦，等下再来吧", Toast.LENGTH_SHORT).show();
                            }
                            refreshLayout.finishLoadMore();
                            refreshLayout.finishRefresh();
                        }
                    }
            );
        } else {
//            if (")getCategory().equals("首页)
//            if (getUserVisibleHint())
            if (!offline) Toast.makeText(getContext(), "离线模式", Toast.LENGTH_SHORT).show();
            offline = true;
            getDataRepository().getSavedNewsByCategory(getChannel(), size, offset, new DataRepository.OnReceiveSavedNewsCallback() {
                @Override
                public void onReceive(List<NewsBean> savedNews) {
                    offset += savedNews.size();
                    setNewsList(savedNews, isOnRefresh, isOnLoadMore);
                    refreshLayout.finishLoadMore();
                    refreshLayout.finishRefresh();
                }
            });
        }

    }

    public void setNewsList(List<NewsBean> newsBeanList, boolean isOnRefresh, boolean isOnLoadMore) {
        ArrayList<Model> newsList = new ArrayList<>();
        if (isOnRefresh) {
            data.clear();
            data_read.clear();
        }
        for (int i = 0; i < newsBeanList.size(); ++i) {
            int finalI = i;
            List<String> images = newsBeanList.get(finalI).getImageUrls();
            data.add(newsBeanList.get(finalI));
            data_read.add(false);
            newsList.add(new Model() {{

                this.name = newsBeanList.get(finalI).getTitle();
                this.nickname = newsBeanList.get(finalI).getContent();
                for (int i = 0; i < this.nickname.length() - 1; ++i) {
                    if (this.nickname.charAt(i) != '\n' && this.nickname.charAt(i) != '　' && this.nickname.charAt(i) != ' ') {
                        this.nickname = this.nickname.substring(i);
                        newsBeanList.get(finalI).setContent(this.nickname);
                        break;
                    }
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
