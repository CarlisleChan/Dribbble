package com.carlisle.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.carlisle.tools.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by zhanglei on 15/7/26.
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
    public static final String USER_LINKS_KEYS = "links.keys";
    public static final String USER_LINKS_VALUES = "links.values";
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
    public Map<String, String> links;
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

    public DribleUser(JSONObject data) {
        try {
            if (data.has(USER_ID)) id = data.getInt(USER_ID);
            if (data.has(USER_NAME)) name = data.getString(USER_NAME);
            if (data.has(USER_USERNAME)) username = data.getString(USER_USERNAME);
            if (data.has(USER_HTML_URL)) html_url = data.getString(USER_HTML_URL);
            if (data.has(USER_AVATAR_URL)) avatar_url = data.getString(USER_AVATAR_URL);
            if (data.has(USER_BIO)) bio = data.getString(USER_BIO);
            if (data.has(USER_LOCATION)) location = data.getString(USER_LOCATION);
            if (data.has(USER_LINKS)) {
                JSONObject linkJson = (JSONObject) data.get(USER_LINKS);

                HashMap<String, String> linksFromD = new HashMap<>();
                Iterator iter = linkJson.keys();
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    String value = (String) linkJson.get(key);
                    linksFromD.put(key, value);
                }
                links = linksFromD;
            }

            if (data.has(USER_BUCKETS_COUNT)) buckets_count = data.getInt(USER_BUCKETS_COUNT);
            if (data.has(USER_COMMENTS_RECEIVED_COUNT))
                comments_received_count = data.getInt(USER_COMMENTS_RECEIVED_COUNT);
            if (data.has(USER_FOLLOWERS_COUNT)) followers_count = data.getInt(USER_FOLLOWERS_COUNT);
            if (data.has(USER_FOLLOWINGS_COUNT))
                followings_count = data.getInt(USER_FOLLOWINGS_COUNT);
            if (data.has(USER_LIKES_COUNT)) likes_count = data.getInt(USER_LIKES_COUNT);
            if (data.has(USER_LIKES_RECEIVED_COUNT))
                likes_received_count = data.getInt(USER_LIKES_RECEIVED_COUNT);
            if (data.has(USER_PROJECTS_COUNT)) projects_count = data.getInt(USER_PROJECTS_COUNT);
            if (data.has(USER_REBOUNDS_RECEIVED_COUNT))
                rebounds_received_count = data.getInt(USER_REBOUNDS_RECEIVED_COUNT);
            if (data.has(USER_SHOTS_COUNT)) shots_count = data.getInt(USER_SHOTS_COUNT);
            if (data.has(USER_TEAMS_COUNT)) teams_count = data.getInt(USER_TEAMS_COUNT);
            if (data.has(USER_CAN_UPLOAD_SHOT))
                can_upload_shot = data.getBoolean(USER_CAN_UPLOAD_SHOT);
            if (data.has(USER_TYPE)) type = data.getString(USER_TYPE);
            if (data.has(USER_PRO)) pro = data.getBoolean(USER_PRO);
            if (data.has(USER_BUCKETS_URL)) buckets_url = data.getString(USER_BUCKETS_URL);
            if (data.has(USER_FOLLOWERS_URL)) followers_url = data.getString(USER_FOLLOWERS_URL);
            if (data.has(USER_FOLLOWINGS_URL)) followings_url = data.getString(USER_FOLLOWINGS_URL);
            if (data.has(USER_LIKES_URL)) likes_url = data.getString(USER_LIKES_URL);
            if (data.has(USER_PROJECTS_URL)) projects_url = data.getString(USER_PROJECTS_URL);
            if (data.has(USER_SHOTS_URL)) shots_url = data.getString(USER_SHOTS_URL);
            if (data.has(USER_TEAMS_URL)) teams_url = data.getString(USER_TEAMS_URL);

            if (data.has(USER_CREATED_AT)) {
                created_at = DateUtils.SIMPLE_DATE_FORMAT.parse(data.getString(USER_CREATED_AT));
            }
            if (data.has(USER_UPDATED_AT)) {
                updated_at = DateUtils.SIMPLE_DATE_FORMAT.parse(data.getString(USER_UPDATED_AT));
            }


        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "json parse error userId:" + id);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public DribleUser(int comments_received_count, int id, String name, String username, String html_url,
                      String avatar_url, String bio, String location, Map<String, String> links, int buckets_count,
                      int followers_count, int followings_count, int likes_count, int likes_received_count,
                      int projects_count, int rebounds_received_count, int shots_count, int teams_count,
                      boolean can_upload_shot, String type, boolean pro, String buckets_url, String followers_url,
                      String followings_url, String likes_url, String projects_url, String shots_url, String teams_url,
                      Date created_at, Date updated_at) {
        this.comments_received_count = comments_received_count;
        this.id = id;
        this.name = name;
        this.username = username;
        this.html_url = html_url;
        this.avatar_url = avatar_url;
        this.bio = bio;
        this.location = location;
        this.links = links;
        this.buckets_count = buckets_count;
        this.followers_count = followers_count;
        this.followings_count = followings_count;
        this.likes_count = likes_count;
        this.likes_received_count = likes_received_count;
        this.projects_count = projects_count;
        this.rebounds_received_count = rebounds_received_count;
        this.shots_count = shots_count;
        this.teams_count = teams_count;
        this.can_upload_shot = can_upload_shot;
        this.type = type;
        this.pro = pro;
        this.buckets_url = buckets_url;
        this.followers_url = followers_url;
        this.followings_url = followings_url;
        this.likes_url = likes_url;
        this.projects_url = projects_url;
        this.shots_url = shots_url;
        this.teams_url = teams_url;
        this.created_at = created_at;
        this.updated_at = updated_at;
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
        ArrayList<String> links_keys = bundle.getStringArrayList(USER_LINKS_KEYS);
        ArrayList<String> links_values = bundle.getStringArrayList(USER_LINKS_VALUES);
        HashMap<String, String> linksFromP = new HashMap<>();
        for (int i = 0; i < links_keys.size(); i++) {
            linksFromP.put(links_keys.get(i), links_values.get(i));
        }

        this.links = linksFromP;
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

        bundle.putStringArrayList(USER_LINKS_KEYS, new ArrayList<>(links.keySet()));
        bundle.putStringArrayList(USER_LINKS_VALUES, new ArrayList<>(links.values()));


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


    @Override
    public String toString() {
        return "DribleUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", html_url='" + html_url + '\'' +
                ", avatar_url='" + avatar_url + '\'' +
                ", bio='" + bio + '\'' +
                ", location='" + location + '\'' +
                ", links=" + links +
                ", buckets_count=" + buckets_count +
                ", comments_received_count=" + comments_received_count +
                ", followers_count=" + followers_count +
                ", followings_count=" + followings_count +
                ", likes_count=" + likes_count +
                ", likes_received_count=" + likes_received_count +
                ", projects_count=" + projects_count +
                ", rebounds_received_count=" + rebounds_received_count +
                ", shots_count=" + shots_count +
                ", teams_count=" + teams_count +
                ", can_upload_shot=" + can_upload_shot +
                ", type='" + type + '\'' +
                ", pro=" + pro +
                ", buckets_url='" + buckets_url + '\'' +
                ", followers_url='" + followers_url + '\'' +
                ", followings_url='" + followings_url + '\'' +
                ", likes_url='" + likes_url + '\'' +
                ", projects_url='" + projects_url + '\'' +
                ", shots_url='" + shots_url + '\'' +
                ", teams_url='" + teams_url + '\'' +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                '}';
    }


}
