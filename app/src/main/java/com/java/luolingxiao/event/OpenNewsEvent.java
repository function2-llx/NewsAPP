package com.java.luolingxiao.event;

import com.java.luolingxiao.bean.NewsBean;

public class OpenNewsEvent extends EventBase {
    private NewsBean newsBean;

    public OpenNewsEvent(NewsBean newsBean) { this.newsBean = newsBean; }
    public NewsBean getNewsBean() { return newsBean; }
}
