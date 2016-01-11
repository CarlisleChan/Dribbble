package com.carlisle.model;

import com.carlisle.tools.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Calendar;

/**
 * Created by chengxin on 16/1/7.
 */
public class DribleBucket {

    public static final String BUCKET_ID = "id";
    public static final String BUCKET_NAME = "name";
    public static final String BUCKET_DESCRIPTION = "description";
    public static final String BUCKET_SHOTS_COUNT = "shots_count";
    public static final String BUCKET_CREATED_AT = "created_at";
    public static final String BUCKET_UPDATED_AT = "updated_at";

    public long id;
    public String name;
    public String description;
    public int shots_count;
    public Calendar created_at;
    public Calendar updated_at;

    public DribleBucket() {
    }

    public DribleBucket(JSONObject data) {
        try {
            if (data.has(BUCKET_ID)) {
                id = data.getInt(BUCKET_ID);
            }
            if (data.has(BUCKET_NAME)) {
                name = data.getString(BUCKET_NAME);
            }
            if (data.has(BUCKET_DESCRIPTION)) {
                description = data.getString(BUCKET_DESCRIPTION);
            }
            if (data.has(BUCKET_SHOTS_COUNT)) {
                shots_count = data.getInt(BUCKET_SHOTS_COUNT);
            }
            if (data.has(BUCKET_CREATED_AT)) {
                String createStr = data.getString(BUCKET_CREATED_AT);
                created_at = Calendar.getInstance();
                created_at.setTime(DateUtils.SIMPLE_DATE_FORMAT.parse(createStr));
            }
            if (data.has(BUCKET_UPDATED_AT)) {
                String updateStr = data.getString(BUCKET_UPDATED_AT);
                updated_at = Calendar.getInstance();
                updated_at.setTime(DateUtils.SIMPLE_DATE_FORMAT.parse(updateStr));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
