package com.ucas.iplay.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.format.DateFormat;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by ivanchou on 3/15/15.
 */
public class StringUtil {
    public static String parseStringToMD5(String str) {
        StringBuffer sb = new StringBuffer();
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(str.getBytes());
            for (int i = 0; i < bytes.length; ++i) {
                sb.append(Integer.toHexString((bytes[i] & 0xFF) | 0x100).substring(1, 3));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 根据 Uri 获得文件的实际路径
     * 限定于照片媒体
     * @param context
     * @param contentUri
     * @return
     */
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static String parseTimeToString(int hourOfDay, int minute) {
        String str = hourOfDay + ":" + minute;
        return str;
    }

    public static String parseDateToString(int year, int monthOfYear, int dayOfMonth) {
        String str = "";
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        str = str + getDayOfWeekStr(calendar.get(Calendar.DAY_OF_WEEK)) + ", ";
        str += year + ", " + (monthOfYear + 1) + ", " + dayOfMonth;
        return str;
    }

    public static String parseLongTimeToString(String s) {
        long time = Long.parseLong(s);
        Date date = new Date(time);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

    /**
     * 从长整数中解析出全套时间
     * @param s
     * @return
     */
    public static String parseLongTimeToWholeString(String s) {
        long time = Long.parseLong(s);
        Date date = new Date(time);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd ");
        SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");
        String sdate = df.format(date);
        String sdate1 = df1.format(date);
        Calendar mCalender = Calendar.getInstance();
        mCalender.setTimeInMillis(time);
        int hour = mCalender.get(Calendar.HOUR_OF_DAY);
        if (hour<4)
            sdate += "凌晨";
        else if (hour < 8)
            sdate += "早晨";
        else if (hour < 11)
            sdate += "上午";
        else if (hour < 13)
            sdate += "中午";
        else if (hour < 16)
            sdate += "下午";
        else if (hour < 20)
            sdate += "傍晚";
        else
            sdate += "夜晚";
        sdate += sdate1;
        return sdate;
    }

    /**
     * 生成随机字符串
     * @return
     */
    public static String randomString() {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < 64; i++) {
            int num = random.nextInt(62);
            buf.append(str.charAt(num));
        }
        return buf.toString();
    }

    private static String getDayOfWeekStr(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                return "周日";
            case Calendar.MONDAY:
                return "周一";
            case Calendar.TUESDAY:
                return "周二";
            case Calendar.WEDNESDAY:
                return "周三";
            case Calendar.THURSDAY:
                return "周四";
            case Calendar.FRIDAY:
                return "周五";
            case Calendar.SATURDAY:
                return "周六";
            default:
                break;
        }
        return "";
    }
}
