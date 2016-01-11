package com.carlisle.model;

import java.util.Date;

/**
 * Created by chengxin on 16/1/7.
 */
public class DribleComment {
    public int id;
    public String body;
    public int likes_count;
    public String likes_url;
    public Date created_at;
    public Date updated_at;
    public DribleUser user;
}
