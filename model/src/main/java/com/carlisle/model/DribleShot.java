package com.carlisle.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.carlisle.model.e.ImageCollection;
import com.carlisle.model.e.Team;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by chengxin on 16/1/7.
 */
public class DribleShot implements Parcelable {

    public static final String SHOT_ID = "id";
    public static final String SHOT_TITLE = "title";
    public static final String SHOT_DESCRIPTION = "description";
    public static final String SHOT_WIDTH = "width";
    public static final String SHOT_HEIGHT = "height";
    public static final String SHOT_IMAGES = "images";
    public static final String SHOT_VIEWS_COUNT = "views_count";
    public static final String SHOT_LIKES_COUNT = "likes_count";
    public static final String SHOT_COMMENTS_COUNT = "comments_count";
    public static final String SHOT_ATTACHMENTS_COUNT = "attachments_count";
    public static final String SHOT_REBOUNDS_COUNT = "rebounds_count";
    public static final String SHOT_BUCKETS_COUNT = "buckets_count";
    public static final String SHOT_CREATED_AT = "created_at";
    public static final String SHOT_UPDATED_AT = "updated_at";
    public static final String SHOT_HTML_URL = "html_url";
    public static final String SHOT_ATTACHMENTS_URL = "attachments_url";
    public static final String SHOT_BUCKETS_URL = "buckets_url";
    public static final String SHOT_COMMENTS_URL = "comments_url";
    public static final String SHOT_LIKES_URL = "likes_url";
    public static final String SHOT_PROJECTS_URL = "projects_url";
    public static final String SHOT_REBOUNDS_URL = "rebounds_url";
    public static final String SHOT_TAGS = "tags";
    public static final String SHOT_USER = "user";
    public static final String SHOT_TEAM = "team";

    public int id;
    public String title;
    public String description;
    public int width;
    public int height;
    /**
     * There are three images:
     * 1. hidpi aka large
     * 2. normal aka middle
     * 3. teaser aka small
     */
    public ImageCollection images;
    public int views_count;
    public int likes_count;
    public int comments_count;
    public int attachments_count;
    public int rebounds_count;
    public int buckets_count;
    public Date created_at;
    public Date updated_at;
    public String html_url;
    public String attachments_url;
    public String buckets_url;
    public String comments_url;
    public String likes_url;
    public String projects_url;
    public String rebounds_url;
    public ArrayList<String> tags;
    public DribleUser user;
    public Team team;

    public DribleShot() {
    }

    public DribleShot(Parcel in) {
        Bundle bundle = in.readBundle();
        id = bundle.getInt(SHOT_ID);
        title = bundle.getString(SHOT_TITLE);
        description = bundle.getString(SHOT_DESCRIPTION);
        width = bundle.getInt(SHOT_WIDTH);
        height = bundle.getInt(SHOT_HEIGHT);
        images = new Gson().fromJson(bundle.getString(SHOT_IMAGES), ImageCollection.class);
        views_count = bundle.getInt(SHOT_VIEWS_COUNT);
        likes_count = bundle.getInt(SHOT_LIKES_COUNT);
        comments_count = bundle.getInt(SHOT_COMMENTS_COUNT);
        attachments_count = bundle.getInt(SHOT_ATTACHMENTS_COUNT);
        rebounds_count = bundle.getInt(SHOT_ATTACHMENTS_COUNT);
        buckets_count = bundle.getInt(SHOT_BUCKETS_COUNT);
        created_at = (Date) bundle.getSerializable(SHOT_CREATED_AT);
        updated_at = (Date) bundle.getSerializable(SHOT_UPDATED_AT);
        html_url = bundle.getString(SHOT_HTML_URL);
        attachments_url = bundle.getString(SHOT_ATTACHMENTS_URL);
        buckets_url = bundle.getString(SHOT_BUCKETS_URL);
        comments_url = bundle.getString(SHOT_COMMENTS_URL);
        likes_url = bundle.getString(SHOT_LIKES_URL);
        projects_url = bundle.getString(SHOT_PROJECTS_URL);
        rebounds_url = bundle.getString(SHOT_REBOUNDS_URL);
        tags = bundle.getStringArrayList(SHOT_TAGS);
        int userId = bundle.getInt(SHOT_USER);
        user = new DribleUser();
        user.id = userId;

        team = new Gson().fromJson(bundle.getString(SHOT_TEAM), Team.class);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putInt(SHOT_ID, id);
        bundle.putString(SHOT_TITLE, title);
        bundle.putString(SHOT_DESCRIPTION, description);
        bundle.putInt(SHOT_WIDTH, width);
        bundle.putInt(SHOT_HEIGHT, height);
        bundle.putString(SHOT_IMAGES, new Gson().toJson(images));
        bundle.putInt(SHOT_VIEWS_COUNT, views_count);
        bundle.putInt(SHOT_LIKES_COUNT, likes_count);
        bundle.putInt(SHOT_COMMENTS_COUNT, comments_count);
        bundle.putInt(SHOT_ATTACHMENTS_COUNT, attachments_count);
        bundle.putInt(SHOT_REBOUNDS_COUNT, rebounds_count);
        bundle.putInt(SHOT_BUCKETS_COUNT, buckets_count);
        bundle.putSerializable(SHOT_CREATED_AT, created_at);
        bundle.putSerializable(SHOT_UPDATED_AT, updated_at);
        bundle.putString(SHOT_HTML_URL, html_url);
        bundle.putString(SHOT_ATTACHMENTS_URL, attachments_url);
        bundle.putString(SHOT_BUCKETS_URL, buckets_url);
        bundle.putString(SHOT_COMMENTS_URL, comments_url);
        bundle.putString(SHOT_LIKES_URL, likes_url);
        bundle.putString(SHOT_PROJECTS_URL, projects_url);
        bundle.putString(SHOT_REBOUNDS_URL, rebounds_url);
        bundle.putStringArrayList(SHOT_TAGS, tags);
        bundle.putInt(SHOT_USER, user.id);

        bundle.putString(SHOT_TEAM, new Gson().toJson(team));
        dest.writeBundle(bundle);

    }

    public static final Creator<DribleShot> CREATOR = new Creator<DribleShot>() {
        @Override
        public DribleShot createFromParcel(Parcel source) {
            return new DribleShot(source);
        }

        @Override
        public DribleShot[] newArray(int size) {
            return new DribleShot[size];
        }
    };
}
