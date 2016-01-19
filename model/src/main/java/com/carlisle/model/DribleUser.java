package com.carlisle.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.carlisle.model.e.Link;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by chengxin on 16/1/7.
 */
public class DribleUser implements Parcelable {
    private static final String TAG = DribleUser.class.getSimpleName();

    // for mem, totally 30 fields
    public int id;
    public String name;
    public String username;
    @SerializedName("html_url")
    public String htmlUrl;
    @SerializedName("avatar_url")
    public String avatarUrl;
    public String bio;
    public String location;
    public Link links;
    @SerializedName("buckets_count")
    public int bucketsCount;
    @SerializedName("comments_received_count")
    public int commentsReceivedCount;
    @SerializedName("followers_count")
    public int followersCount;
    @SerializedName("followings_count")
    public int followingsCount;
    @SerializedName("likes_count")
    public int likesCount;
    @SerializedName("likes_received_count")
    public int likesReceivedCount;
    @SerializedName("projects_count")
    public int projectsCount;
    @SerializedName("rebounds_received_count")
    public int reboundsReceivedCount;
    @SerializedName("shots_count")
    public int shotsCount;
    @SerializedName("teams_count")
    public int teamsCount;
    @SerializedName("can_upload_shot")
    public boolean canUploadShot;
    public String type;
    public boolean pro;
    @SerializedName("buckets_url")
    public String bucketsUrl;
    @SerializedName("followers_url")
    public String followersUrl;
    @SerializedName("followings_url")
    public String followingsUrl;
    @SerializedName("likes_url")
    public String likesUrl;
    @SerializedName("projects_url")
    public String projectsUrl;
    @SerializedName("shots_url")
    public String shotsUrl;
    @SerializedName("teams_url")
    public String teamsUrl;
    @SerializedName("created_at")
    public Date createdAt;
    @SerializedName("updated_at")
    public Date updatedAt;

    public DribleUser() {

    }

    protected DribleUser(Parcel in) {
        id = in.readInt();
        name = in.readString();
        username = in.readString();
        htmlUrl = in.readString();
        avatarUrl = in.readString();
        bio = in.readString();
        location = in.readString();
        links = in.readParcelable(Link.class.getClassLoader());
        bucketsCount = in.readInt();
        commentsReceivedCount = in.readInt();
        followersCount = in.readInt();
        followingsCount = in.readInt();
        likesCount = in.readInt();
        likesReceivedCount = in.readInt();
        projectsCount = in.readInt();
        reboundsReceivedCount = in.readInt();
        shotsCount = in.readInt();
        teamsCount = in.readInt();
        canUploadShot = in.readByte() != 0;
        type = in.readString();
        pro = in.readByte() != 0;
        bucketsUrl = in.readString();
        followersUrl = in.readString();
        followingsUrl = in.readString();
        likesUrl = in.readString();
        projectsUrl = in.readString();
        shotsUrl = in.readString();
        teamsUrl = in.readString();
        createdAt = (Date) in.readSerializable();
        updatedAt = (Date) in.readSerializable();
    }

    public static final Creator<DribleUser> CREATOR = new Creator<DribleUser>() {
        @Override
        public DribleUser createFromParcel(Parcel in) {
            return new DribleUser(in);
        }

        @Override
        public DribleUser[] newArray(int size) {
            return new DribleUser[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(htmlUrl);
        dest.writeString(avatarUrl);
        dest.writeString(bio);
        dest.writeString(location);
        dest.writeParcelable(links, flags);
        dest.writeInt(bucketsCount);
        dest.writeInt(commentsReceivedCount);
        dest.writeInt(followersCount);
        dest.writeInt(followingsCount);
        dest.writeInt(likesCount);
        dest.writeInt(likesReceivedCount);
        dest.writeInt(projectsCount);
        dest.writeInt(reboundsReceivedCount);
        dest.writeInt(shotsCount);
        dest.writeInt(teamsCount);
        dest.writeByte((byte) (canUploadShot ? 1 : 0));
        dest.writeString(type);
        dest.writeByte((byte) (pro ? 1 : 0));
        dest.writeString(bucketsUrl);
        dest.writeString(followersUrl);
        dest.writeString(followingsUrl);
        dest.writeString(likesUrl);
        dest.writeString(projectsUrl);
        dest.writeString(shotsUrl);
        dest.writeString(teamsUrl);
        dest.writeSerializable(createdAt);
        dest.writeSerializable(updatedAt);
    }
}
