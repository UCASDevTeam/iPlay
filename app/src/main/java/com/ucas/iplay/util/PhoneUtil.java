package com.ucas.iplay.util;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by ivanchou on 4/18/15.
 */
public class PhoneUtil {
    //判断是否连接网络
    public static boolean isConnect(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }
}
