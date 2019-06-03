package com.roobo.baselibiray.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by HP on 2019/3/14.
 */

public class TimeUtils {

    public static final String TAG = TimeUtils.class.getSimpleName();

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    public static boolean isBetween(int startTime, int endTime) {
        try {
            int currentTime = (int) (System.currentTimeMillis() / 1000);
            if (currentTime >= startTime && currentTime <= endTime) {
                return true;
            }
        } catch (Exception e) {
            Log.d(TAG, "[isBetween] Exception:" + e.getLocalizedMessage());
            return false;
        }
        return false;
    }

    public static String parseTime(long time) {
        SIMPLE_DATE_FORMAT.setTimeZone(TimeZone.getDefault());
        return SIMPLE_DATE_FORMAT.format(new Date(time));
    }

    public static int getTime(String time) {
        try {
            SIMPLE_DATE_FORMAT.setTimeZone(TimeZone.getDefault());
            return (int) (SIMPLE_DATE_FORMAT.parse(time).getTime() / 1000);
        } catch (Exception e) {
            Log.d(TAG, "[getTime] Exception:" + e.getLocalizedMessage());
            return -1;
        }
    }
}
