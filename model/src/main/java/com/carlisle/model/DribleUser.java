package com.carlisle.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.carlisle.model.e.Link;
import com.google.gson.Gson;

import java.util.Date;

/**
 * Created by chengxin on 16/1/7.
 */
public class DribleUser implements Parcelable {
    private static final String TAG = DribleUser.class.getSimpleName();

    public static final String USER_ID = "id";
    public static final String USER_NAME = "name";
    public static final String USER_USERNAME = "username";
    public static final String USER_HTML_URL = "html_url";
    public static final String USER_AVATAR_URL = "avatar_url";

    public static final String USER_BIO = "bio";
    public static final String USER_LOCATION = "location";
    public static final String USER_LINKS = "links";
    public static final String USER_BUCKETS_COUNT = "buckets_count";
    public static final String USER_COMMENTS_RECEIVED_COUNT = "comments_received_count";

    public static final String USER_FOLLOWERS_COUNT = "followers_count";
    public static final String USER_FOLLOWINGS_COUNT = "followings_count";
    public static final String USER_LIKES_COUNT = "likes_count";
    public static final String USER_LIKES_RECEIVED_COUNT = "likes_received_count";
    public static final String USER_PROJECTS_COUNT = "projects_count";

    public static final String USER_REBOUNDS_RECEIVED_COUNT = "rebounds_received_count";
    public static final String USER_SHOTS_COUNT = "shots_count";
    public static final String USER_TEAMS_COUNT = "teams_count";
    public static final String USER_CAN_UPLOAD_SHOT = "can_upload_shot";
    public static final String USER_TYPE = "type";

    public static final String USER_PRO = "pro";
    public static final String USER_BUCKETS_URL = "buckets_url";
    public static final String USER_FOLLOWERS_URL = "followers_url";
    public static final String USER_FOLLOWINGS_URL = "following_url";
    public static final String USER_LIKES_URL = "likes_url";

    public static final String USER_PROJECTS_URL = "projects_url";
    public static final String USER_SHOTS_URL = "shots_url";
    public static final String USER_TEAMS_URL = "teams_url";
    public static final String USER_CREATED_AT = "created_at";
    public static final String USER_UPDATED_AT = "updated_at";

    // for mem, totally 30 fields
    public int id;
    public String name;
    public String username;
    public String html_url;
    public String avatar_url;
    public String bio;
    public String location;
    public Link links;
    public int buckets_count;
    public int comments_received_count;
    public int followers_count;
    public int followings_count;
    public int likes_count;
    public int likes_received_count;
    public int projects_count;
    public int rebounds_received_count;
    public int shots_count;
    public int teams_count;
    public boolean can_upload_shot;
    public String type;
    public boolean pro;
    public String buckets_url;
    public String followers_url;
    public String followings_url;
    public String likes_url;
    public String projects_url;
    public String shots_url;
    public String teams_url;
    public Date created_at;
    public Date updated_at;


    public DribleUser() {
    }

    public DribleUser(Parcel in) {
        Bundle bundle = in.readBundle();
        this.comments_received_count = bundle.getInt(USER_COMMENTS_RECEIVED_COUNT);
        this.id = bundle.getInt(USER_ID);
        this.name = bundle.getString(USER_NAME);
        this.username = bundle.getString(USER_USERNAME);
        this.html_url = bundle.getString(USER_HTML_URL);
        this.avatar_url = bundle.getString(USER_AVATAR_URL);
        this.bio = bundle.getString(USER_BIO);
        this.location = bundle.getString(USER_LOCATION);
        this.links = new Gson().fromJson(bundle.getString(USER_LINKS), Link.class);
        this.buckets_count = bundle.getInt(USER_BUCKETS_COUNT);
        this.followers_count = bundle.getInt(USER_FOLLOWERS_COUNT);
        this.followings_count = bundle.getInt(USER_FOLLOWINGS_COUNT);
        this.likes_count = bundle.getInt(USER_LIKES_COUNT);
        this.likes_received_count = bundle.getInt(USER_LIKES_RECEIVED_COUNT);
        this.projects_count = bundle.getInt(USER_PROJECTS_COUNT);
        this.rebounds_received_count = bundle.getInt(USER_REBOUNDS_RECEIVED_COUNT);
        this.shots_count = bundle.getInt(USER_SHOTS_COUNT);
        this.teams_count = bundle.getInt(USER_TEAMS_COUNT);
        this.can_upload_shot = bundle.getBoolean(USER_CAN_UPLOAD_SHOT);
        this.type = bundle.getString(USER_TYPE);
        this.pro = bundle.getBoolean(USER_PRO);
        this.buckets_url = bundle.getString(USER_BUCKETS_URL);
        this.followers_url = bundle.getString(USER_FOLLOWERS_URL);
        this.followings_url = bundle.getString(USER_FOLLOWINGS_URL);
        this.likes_url = bundle.getString(USER_LIKES_URL);
        this.projects_url = bundle.getString(USER_PROJECTS_URL);
        this.shots_url = bundle.getString(USER_SHOTS_URL);
        this.teams_url = bundle.getString(USER_TEAMS_URL);
        this.created_at = (Date) bundle.get(USER_CREATED_AT);
        this.updated_at = (Date) bundle.get(USER_UPDATED_AT);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putInt(USER_COMMENTS_RECEIVED_COUNT, comments_received_count);
        bundle.putInt(USER_ID, id);
        this.id = bundle.getInt(USER_ID);
        bundle.putString(USER_NAME, name);
        bundle.putString(USER_USERNAME, username);
        bundle.putString(USER_HTML_URL, html_url);
        bundle.putString(USER_AVATAR_URL, avatar_url);
        bundle.putString(USER_BIO, bio);
        bundle.putString(USER_LOCATION, location);
        bundle.putString(USER_LINKS, new Gson().toJson(links));
        bundle.putInt(USER_BUCKETS_COUNT, buckets_count);
        bundle.putInt(USER_FOLLOWERS_COUNT, followers_count);
        bundle.putInt(USER_FOLLOWINGS_COUNT, followings_count);
        bundle.putInt(USER_LIKES_COUNT, likes_count);
        bundle.putInt(USER_LIKES_RECEIVED_COUNT, likes_received_count);
        bundle.putInt(USER_PROJECTS_COUNT, projects_count);
        bundle.putInt(USER_REBOUNDS_RECEIVED_COUNT, rebounds_received_count);
        bundle.putInt(USER_SHOTS_COUNT, shots_count);
        bundle.putInt(USER_TEAMS_COUNT, teams_count);
        bundle.putBoolean(USER_CAN_UPLOAD_SHOT, can_upload_shot);
        bundle.putString(USER_TYPE, type);
        bundle.putBoolean(USER_PRO, pro);
        bundle.putString(USER_BUCKETS_URL, buckets_url);
        bundle.putString(USER_FOLLOWERS_URL, followers_url);
        bundle.putString(USER_FOLLOWINGS_URL, followings_url);
        bundle.putString(USER_LIKES_URL, likes_url);
        bundle.putString(USER_PROJECTS_URL, projects_url);
        bundle.putString(USER_SHOTS_URL, shots_url);
        bundle.putString(USER_TEAMS_URL, teams_url);
        this.teams_url = bundle.getString(USER_TEAMS_URL);
        bundle.putSerializable(USER_CREATED_AT, created_at);
        bundle.putSerializable(USER_UPDATED_AT, updated_at);
        dest.writeBundle(bundle);
    }

    public static final Creator CREATOR = new Creator<DribleUser>() {
        @Override
        public DribleUser createFromParcel(Parcel source) {
            return new DribleUser(source);
        }

        @Override
        public DribleUser[] newArray(int size) {
            return new DribleUser[size];
        }
    };

}
