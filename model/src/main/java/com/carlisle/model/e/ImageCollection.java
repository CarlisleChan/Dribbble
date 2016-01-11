package com.carlisle.model.e;

/**
 * Created by chengxin on 1/7/16.
 */
public class ImageCollection {

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
}
