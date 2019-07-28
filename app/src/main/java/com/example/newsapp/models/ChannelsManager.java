package com.example.newsapp.models;

import java.util.ArrayList;
import java.util.List;

public class ChannelsManager {
    private static ChannelsManager manager = new ChannelsManager();
    private List<String> channels;

    private ChannelsManager() {
        channels = new ArrayList<>();
        channels.add("关注");
        channels.add("推荐");
        for (int i = 0; i < 10; i++) {
            channels.add("频道" + i);
        }
    }

    public String getChannel(int pos) {
        return channels.get(pos);
    }

    public int getCount() { return channels.size(); }

    void updateChannels(List<String> channels) {
        this.channels = channels;
    }

    public static ChannelsManager getInstance() {
        return manager;
    }
}
