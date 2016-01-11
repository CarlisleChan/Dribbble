package com.carlisle.model;

import java.util.Date;

/**
 * Created by chengxin on 1/7/16.
 */
public class FollowResult {
    public long id;
    public Date created_at;
    public DribleUser followee;
    public DribleUser follower;

    public DribleUser getFollow() {
        if (followee != null) {
            return followee;
        }

        if (follower != null) {
            return follower;
        }

        return null;
    }
}
