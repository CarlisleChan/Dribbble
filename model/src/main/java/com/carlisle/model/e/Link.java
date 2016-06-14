package com.carlisle.model.e;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chengxin on 1/7/16.
 */
public class Link implements Parcelable {
    public String web;
    public String twitter;

    protected Link(Parcel in) {
        web = in.readString();
        twitter = in.readString();
    }

    public static final Creator<Link> CREATOR = new Creator<Link>() {
        @Override
        public Link createFromParcel(Parcel in) {
            return new Link(in);
        }

        @Override
        public Link[] newArray(int size) {
            return new Link[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(web);
        dest.writeString(twitter);
    }
}
