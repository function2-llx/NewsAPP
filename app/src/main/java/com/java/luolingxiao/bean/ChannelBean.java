package com.java.luolingxiao.bean;

import com.trs.channellib.channel.channel.ChannelEntity;

public class ChannelBean implements ChannelEntity.ChannelEntityCreater {
    private boolean fixed;
    private String name;

    public ChannelBean(String name) { this(name, false); }

    public ChannelBean(String name, boolean fixed) {
        this.name = name;
        this.fixed = fixed;
    }

    @Override
    public ChannelEntity createChannelEntity() {
        ChannelEntity entity = new ChannelEntity();
        entity.setFixed(fixed);
        entity.setName(name);
        return entity;
    }

    public String getName() { return name; }
}
