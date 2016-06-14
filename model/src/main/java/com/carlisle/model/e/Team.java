package com.carlisle.model.e;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chengxin on 1/7/16.
 */
public class Team implements Parcelable {
    public long id;
    public String name;
    public String username;
    @SerializedName("html_url")
    public String htmlUrl;
    @SerializedName("avatar_url")
    public String avatarUrl;
    public String bio;
    public String location;
    public Link links;

    protected Team(Parcel in) {
        id = in.readLong();
        name = in.readString();
        username = in.readString();
        htmlUrl = in.readString();
        avatarUrl = in.readString();
        bio = in.readString();
        location = in.readString();
        links = in.readParcelable(Link.class.getClassLoader());
    }

    public static final Creator<Team> CREATOR = new Creator<Team>() {
        @Override
        public Team createFromParcel(Parcel in) {
            return new Team(in);
        }

        @Override
        public Team[] newArray(int size) {
            return new Team[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(htmlUrl);
        dest.writeString(avatarUrl);
        dest.writeString(bio);
        dest.writeString(location);
        dest.writeParcelable(links, flags);
    }
}
