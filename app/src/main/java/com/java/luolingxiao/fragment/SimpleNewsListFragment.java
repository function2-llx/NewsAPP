package com.java.luolingxiao.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.java.luolingxiao.DefaultActivity;
import com.java.luolingxiao.NewsActivity;
import com.java.luolingxiao.R;
import com.java.luolingxiao.adapter.BaseRecyclerAdapter;
import com.java.luolingxiao.adapter.SmartViewHolder;
import com.java.luolingxiao.bean.NewsBean;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public abstract class SimpleNewsListFragment extends DefaultFragment implements OnRefreshLoadMoreListener {
    ArrayList<NewsBean> data = new ArrayList<>();

    protected static final int chunkSize = 5;

    boolean noMore = false;

    // 标志位，标志已经初始化完成。
    RefreshLayout refreshLayout;

    public SimpleNewsListFragment() {}

    class Model {
        int imageId;
        int avatarId;
        int position;
        String name;
        String nickname;
        String imageUrl;
    }

    BaseRecyclerAdapter<Model> mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);
        refreshLayout = view.findViewById(R.id.refreshLayout);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new BaseRecyclerAdapter<Model>(R.layout.item_practice_repast) {
            @Override
            protected void onBindViewHolder(SmartViewHolder holder, Model model, int position) {
                NewsBean newsBean = data.get(position);
                holder.myText(R.id.name, model.name, false);
                if (holder.getItemViewType() < 2)
                    holder.myText(R.id.nickname, model.nickname, false);

                List<String> imageUrls = data.get(model.position).getImageUrls();

                if (newsBean.getImageUrls().size() >= 3) {
                    holder.image(R.id.image1, imageUrls.get(0));
                    holder.image(R.id.image2, imageUrls.get(1));
                    holder.image(R.id.image3, imageUrls.get(2));
                } else if (newsBean.getImageUrls().size() > 0) {
                    holder.image(R.id.image, model.imageUrl);
                }
                holder.text(R.id.time, data.get(model.position).getPublisher() + " " + data.get(model.position).getPublishTime().toString());
            }


            @Override
            public int getItemViewType(int position) {
                NewsBean newsBean = data.get(position);
                if (newsBean.getImageUrls().size() == 0) {
                    return 0;
                } else if (newsBean.getImageUrls().size() < 3) {
                    return 1;
                } else {
                    return 2;
                }
            }

            @Override
            @NonNull
            public SmartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                if (viewType == 0) {
                    return new SmartViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_no_picture, parent, false), mListener);
                } else if (viewType == 1) {
                    return new SmartViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_practice_repast, parent, false), mListener);
                } else {
                    return new SmartViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_3_picture, parent, false), mListener);
                }
            }
        };
        mAdapter.setOnItemClickListener((parent, view1, position, id) -> {
            NewsBean newsBean = data.get(position);
            NewsActivity.startAction(getContext(), newsBean);
            TextView subView = view1.findViewById(R.id.name);
            subView.setTextColor(DefaultActivity.getAnyActivity().getColor(R.color.text_read));
            subView = view1.findViewById(R.id.nickname);
            getDataRepository().setRead(newsBean, true);
            if (subView != null) {
                subView.setTextColor(DefaultActivity.getAnyActivity().getColor(R.color.text_read));
            }
        });
        recyclerView.setAdapter(mAdapter);
        refreshLayout.setOnRefreshLoadMoreListener(this);

        return view;
    }

    // append?
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

}
