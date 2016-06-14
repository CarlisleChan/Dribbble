package com.carlisle.model.e;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chengxin on 1/7/16.
 */
public class ImageCollection implements Parcelable {

    /**
     * There are three images:
     * 1. hidpi aka large
     * 2. normal aka middle
     * 3. teaser aka small
     */
    enum ImageLevel {
        H(0),
        N(1),
        L(2);

        public int level;

        ImageLevel(int level) {
            this.level = level;
        }
    }

    public String hidpi;
    public String normal;
    public String teaser;

    public static ImageLevel imageLevel = ImageLevel.N;

    public String getUrl() {
        switch (imageLevel) {
            case H:
                return hidpi;
            case N:
                return normal;
            case L:
                return teaser;
            default:
                return normal;
        }
    }

    protected ImageCollection(Parcel in) {
        hidpi = in.readString();
        normal = in.readString();
        teaser = in.readString();
    }

    public static final Creator<ImageCollection> CREATOR = new Creator<ImageCollection>() {
        @Override
        public ImageCollection createFromParcel(Parcel in) {
            return new ImageCollection(in);
        }

        @Override
        public ImageCollection[] newArray(int size) {
            return new ImageCollection[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hidpi);
        dest.writeString(normal);
        dest.writeString(teaser);
    }

}
