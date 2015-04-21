package com.ucas.iplay.core.tasker;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ucas.iplay.R;
import com.ucas.iplay.core.model.EventModel;
import com.ucas.iplay.util.HttpUtil;
import com.ucas.iplay.util.SPUtil;
import com.ucas.iplay.util.StringUtil;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by ivanchou on 4/18/15.
 */
public class EventPostTask extends AsyncTask<EventModel, Integer, Boolean> {
    private static final String TAG = "EventPostTask";

    private Context mContext;
    private String sessionid;
    private String mContent;
    private String mPicPath;
    private String uuid;
    private EventModel mEventModel;
    private Notification notification;
    private NotificationManager notificationManager;
    private PendingIntent pendingIntent;

    private Handler handler = new Handler();


    public EventPostTask(Context context, String content, String picPath) {
        this.mContext = context;
        this.mContent = content;
        this.mPicPath = picPath;
        sessionid = SPUtil.getSPUtil(context).get(SPUtil.SESSIONID);
        uuid = StringUtil.randomString();
        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Notification.Builder builder = new Notification.Builder(mContext)
                .setTicker(mContext.getString(R.string.task_sending))
                .setContentText(mContext.getString(R.string.task_sending))
                .setContentText(mContent)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher);
        if (!TextUtils.isEmpty(mPicPath)) {
            builder.setProgress(0, 100, false);
        } else {
            builder.setProgress(0, 100, true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // expanding notification
        }

        notification = builder.build();
        notificationManager.notify(1, notification);
    }

    @Override
    protected Boolean doInBackground(EventModel... params) {
        mEventModel = params[0];
        if (!TextUtils.isEmpty(mPicPath)) {
            postPic();
        }
        postEvent();
        return null;
    }

    /**
     * 上传图片
     * @return
     */
    private void postPic() {
        RequestParams params = new RequestParams();
        params.put("myactivityid", uuid);
        params.put("sessionid", sessionid);
        File f = new File(mPicPath);
        if (f.exists()) {
            try {
                params.put("picname", f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            return;
        }

        HttpUtil.uploadPhoto(mContext, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e(TAG, response.toString());
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onProgress(int bytesWritten, int totalSize) {
                publishProgress(bytesWritten, totalSize);
                super.onProgress(bytesWritten, totalSize);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });

    }

    /**
     * 发布新活动
     */
    private void postEvent() {

        RequestParams params = new RequestParams();
        // 打包所有请求参数
        params.put("sessionid", sessionid);
        params.put("startat", mEventModel.startAt);
        params.put("endat", mEventModel.endAt);
        params.put("enrollbefore", mEventModel.endrollBefore);
        params.put("placeat", mEventModel.placeAt);
        params.put("title", mEventModel.title);
        params.put("text", mEventModel.content);
        params.put("tags", mEventModel.tags);
        params.put("maxpeople", mEventModel.maxPeople);
        params.put("status", mEventModel.restriction);
        if (!TextUtils.isEmpty(mPicPath)) {
            params.put("myactivityid", uuid);
        }

        HttpUtil.createNewEvent(mContext, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                publishProgress(-1);
                Log.e(TAG, response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, responseString);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onProgressUpdate(Integer... values) {
        if (values.length > 0 ) {
            int size = values[0];
            if (size != -1) {
                int totalSize = values[1];
                double r = size / (double) totalSize;
                Notification.Builder builder = new Notification.Builder(mContext)
                        .setTicker(mContext.getString(R.string.task_sending_pic))
                        .setContentTitle(mContext.getString(R.string.task_sending))
                        .setNumber((int) (r * 100))
                        .setContentText(mContent)
                        .setProgress(totalSize, size, false)
                        .setOnlyAlertOnce(true)
                        .setOngoing(true)
                        .setSmallIcon(R.drawable.ic_launcher);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    // expanding notification
//                builder.addAction(R.drawable.send_cancel, mContext.getString(R.string.task_send_cancel), pendingIntent);
                }
                notification = builder.build();
            } else {
                Notification.Builder builder = new Notification.Builder(mContext)
                        .setTicker(mContext.getString(R.string.task_sending))
                        .setContentTitle(mContext.getString(R.string.task_waiting))
                        .setContentText(mContent)
                        .setProgress(100, 100, false)
                        .setOnlyAlertOnce(true)
                        .setOngoing(true)
                        .setSmallIcon(R.drawable.ic_launcher);
                notification = builder.build();
            }
            notificationManager.notify(1, notification);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        Notification.Builder builder = new Notification.Builder(mContext)
                .setTicker(mContext.getString(R.string.task_success))
                .setContentTitle(mContext.getString(R.string.task_success))
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.drawable.ic_launcher)
                .setOngoing(false);

        notification = builder.build();
        notificationManager.notify(1, notification);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                notificationManager.cancel(1);
            }
        }, 3000);

        // stop service
    }


}
