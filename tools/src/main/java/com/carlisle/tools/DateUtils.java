package com.carlisle.tools;

import java.text.SimpleDateFormat;

/**
 * Created by chengxin on 1/5/16.
 */
public class DateUtils {
    public static final String DRIBLE_DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DRIBLE_DATE_FORMAT_PATTERN);
}
