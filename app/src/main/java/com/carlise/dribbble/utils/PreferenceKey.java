package com.carlise.dribbble.utils;

/**
 * Created by chengxin on 1/6/16.
 */
public class PreferenceKey {
    public static final String DRIBLE_USER_INFO = "drible_user_info";

    public static final String DRIBLE_TOKEN_FIELD = "com.tuesda.watch.dirbbble.token";

    public static final String IMAGE_LEVEL = "image_level";

    /**
     * Following is about shots set
     */
    public static final String REQUEST_SHOTS_FIELD_LIST = "list";
    public static final String REQUEST_LIST_ANIMATED = "animated";
    public static final String REQEUST_LIST_ATTACHMENTS = "attachments";
    public static final String REQUEST_LIST_DEBUTS = "debuts";
    public static final String REQUEST_LIST_PLAYOFFS = "playoffs";
    public static final String REQUEST_LIST_REBOUNDS = "rebounds";
    public static final String REQUEST_LIST_TEAMS = "teams";
    public static final String REQUEST_SHOTS_FIELD_TIMEFRAME = "timeframe";
    public static final String REQUEST_TIMEFRAME_WEEK = "week";
    public static final String REQUEST_TIMEFRAME_MONTH = "month";
    public static final String REQUEST_TIMEFRAME_YEAR = "year";
    public static final String REQUEST_TIMEFRAME_EVER = "ever";

    // Limit the timeframe to a specific date, week, month, or year. Must be in the format of YYYY-MM-DD.
    public static final String REQUEST_SHOTS_FIELD_DATE = "date";

    public static final String REQUEST_SHOTS_FIELD_SORT = "sort",
            REQUEST_SORT_COMMENTS = "comments",
            REQUEST_SORT_RECENT = "recent",
            REQUEST_SORT_VIEWS = "views";
}
