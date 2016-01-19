package com.carlisle.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.carlisle.model.e.ImageCollection;
import com.carlisle.model.e.Team;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by chengxin on 16/1/7.
 */
public class DribleShot implements Parcelable {
    public int id;
    public String title;
    public String description;
    public int width;
    public int height;
    public ImageCollection images;
    @SerializedName("views_count")
    public int viewsCount;
    @SerializedName("likes_count")
    public int likesCount;
    @SerializedName("comments_count")
    public int commentsCount;
    @SerializedName("attachments_count")
    public int attachmentsCount;
    @SerializedName("rebounds_count")
    public int reboundsCount;
    @SerializedName("buckets_count")
    public int bucketsCount;
    @SerializedName("created_at")
    public Date createdAt;
    @SerializedName("updated_at")
    public Date updatedAt;
    @SerializedName("html_url")
    public String htmlUrl;
    @SerializedName("attachments_url")
    public String attachmentsUrl;
    @SerializedName("buckets_url")
    public String bucketsUrl;
    @SerializedName("comments_url")
    public String commentsUrl;
    @SerializedName("likes_url")
    public String likesUrl;
    @SerializedName("projects_url")
    public String projectsUrl;
    @SerializedName("rebounds_url")
    public String reboundsUrl;
    public ArrayList<String> tags;
    public DribleUser user;
    public Team team;

    protected DribleShot(Parcel in) {
        id = in.readInt();
        title = in.readString();
        description = in.readString();
        width = in.readInt();
        height = in.readInt();
        images = in.readParcelable(ImageCollection.class.getClassLoader());
        viewsCount = in.readInt();
        likesCount = in.readInt();
        commentsCount = in.readInt();
        attachmentsCount = in.readInt();
        reboundsCount = in.readInt();
        bucketsCount = in.readInt();
        createdAt = (Date) in.readSerializable();
        updatedAt = (Date) in.readSerializable();
        htmlUrl = in.readString();
        attachmentsUrl = in.readString();
        bucketsUrl = in.readString();
        commentsUrl = in.readString();
        likesUrl = in.readString();
        projectsUrl = in.readString();
        reboundsUrl = in.readString();
        tags = in.createStringArrayList();
        user = in.readParcelable(DribleUser.class.getClassLoader());
        team = in.readParcelable(Team.class.getClassLoader());
    }

    public static final Creator<DribleShot> CREATOR = new Creator<DribleShot>() {
        @Override
        public DribleShot createFromParcel(Parcel in) {
            return new DribleShot(in);
        }

        @Override
        public DribleShot[] newArray(int size) {
            return new DribleShot[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeParcelable(images, flags);
        dest.writeInt(viewsCount);
        dest.writeInt(likesCount);
        dest.writeInt(commentsCount);
        dest.writeInt(attachmentsCount);
        dest.writeInt(reboundsCount);
        dest.writeInt(bucketsCount);
        dest.writeSerializable(createdAt);
        dest.writeSerializable(updatedAt);
        dest.writeString(htmlUrl);
        dest.writeString(attachmentsUrl);
        dest.writeString(bucketsUrl);
        dest.writeString(commentsUrl);
        dest.writeString(likesUrl);
        dest.writeString(projectsUrl);
        dest.writeString(reboundsUrl);
        dest.writeStringList(tags);
        dest.writeParcelable(user, flags);
        dest.writeParcelable(team, flags);
    }
}
