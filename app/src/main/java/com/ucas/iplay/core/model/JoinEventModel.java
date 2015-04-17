package com.ucas.iplay.core.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wanggang on 2015/4/17.
 */
public class JoinEventModel {
    public long eventId;// 事件 ID
    public String title;// 事件的主题
    public String startAt;// 事件的开始时间
    public String endAt;// 事件的结束时间
    public long orderId;//活动记录在返回结果中的顺序

    public void parse(JSONObject jsonObject) throws JSONException {
        eventId = jsonObject.getLong("activityid");
        title = jsonObject.getString("title");
        startAt = jsonObject.getString("startat");
        endAt = jsonObject.getString("endat");
//        orderId = jsonObject.getLong("orderid");
    }
}
