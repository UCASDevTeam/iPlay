package com.ucas.iplay.core.model;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.ucas.iplay.core.dbinfo.BaseEventsDBInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by ivanchou on 1/21/2015.
 */
public class EventModel implements Serializable{

    public long eventId;// 事件 ID

    public String createdAt;// 事件创建时间

    public int jointed;// 用户对该事件的操作

    public UserModel author;// 事件的发布者

    public String startAt;// 事件的开始时间

    public String endAt;// 事件的结束时间

    public String endrollBefore;// 报名截至时间

    public String placeAt;// 事件的地点

    public String title;// 事件的主题

    public String content;// 事件的详细内容

    public long tags;// 事件的标签属性

    public long maxPeople;// 活动的最大人数限制

    public int restriction;// 访问权限

    public String cost;//消耗

    public String phone;//电话

    public String email;//电子邮件

    public String thumbnailPic;

    public String originalPic;

    public EventModel() {

    }

    public void parse(JSONObject jsonObject) throws JSONException {
        eventId = jsonObject.getLong("activityid");

        if (jsonObject.has("createdat"))
            createdAt = jsonObject.getString("createdat");
        else
            createdAt = "";
//        jointed = jsonObject.getInt("jointed");

        /** 注释掉的功能上相同 根据返回的 json 格式选择 **/
        author = new UserModel();
        if (jsonObject.has("authorid"))
            author.userId = jsonObject.getInt("authorid");
        else
            author.userId = -1;
        if (jsonObject.has("authornick"))
            author.name = jsonObject.getString("authornick");
        else
            author.name = "";
//        author.avatar = jsonObject.getString("photo");

//        author.parse(jsonObject.getJSONObject("author"));
        if (jsonObject.has("startat"))
            startAt = jsonObject.getString("startat");
        else
            startAt = "";
        if (jsonObject.has("endat"))
            endAt = jsonObject.getString("endat");
        else
            endAt = "";
//        endrollBefore =
        if (jsonObject.has("placeat"))
            placeAt = jsonObject.getString("placeat");
        else
            placeAt = "";
        if (jsonObject.has("title"))
            title = jsonObject.getString("title");
        else
            title = "";
        if (jsonObject.has("text"))
            content = jsonObject.getString("text");
        else
            content = "";
        if (jsonObject.has("tags"))
            tags = jsonObject.getLong("tags");
        else
            tags = -1;
        if (jsonObject.has("maxpeople"))
            maxPeople = jsonObject.getLong("maxpeople");
        else
            maxPeople = -1;
//        restriction = jsonObject.getInt("restriction");
//        thumbnailPic = jsonObject.getString("thumbnailpic");
//        originalPic = jsonObject.getString("originalpic");
    }

    public static EventModel fromCursor(Context context, Cursor cursor) {
        EventModel eventModel = new EventModel();
        eventModel.eventId = cursor.getLong(cursor.getColumnIndex(BaseEventsDBInfo.EVENT_ID));
        eventModel.createdAt = cursor.getString(cursor.getColumnIndex(BaseEventsDBInfo.CREATED_AT));
        eventModel.jointed = cursor.getInt(cursor.getColumnIndex(BaseEventsDBInfo.JOINTED));

        UserModel userModel = new UserModel();
        userModel.userId = cursor.getInt(cursor.getColumnIndex(BaseEventsDBInfo.USER_ID));
        userModel.name = cursor.getString(cursor.getColumnIndex(BaseEventsDBInfo.USER_NAME));
        userModel.avatar = cursor.getString(cursor.getColumnIndex(BaseEventsDBInfo.USER_AVATAR));
        eventModel.author = userModel;

        eventModel.startAt = cursor.getString(cursor.getColumnIndex(BaseEventsDBInfo.START_AT));
        eventModel.endAt = cursor.getString(cursor.getColumnIndex(BaseEventsDBInfo.END_AT));
        eventModel.endrollBefore = cursor.getString(cursor.getColumnIndex(BaseEventsDBInfo.ENDROLL_BEFORE));
        eventModel.placeAt = cursor.getString(cursor.getColumnIndex(BaseEventsDBInfo.PLACE_AT));
        eventModel.title = cursor.getString(cursor.getColumnIndex(BaseEventsDBInfo.TITLE));
        eventModel.content = cursor.getString(cursor.getColumnIndex(BaseEventsDBInfo.CONTENT));
        eventModel.tags = cursor.getLong(cursor.getColumnIndex(BaseEventsDBInfo.TAGS));
        eventModel.maxPeople = cursor.getLong(cursor.getColumnIndex(BaseEventsDBInfo.MAX_PEOPLE));
        eventModel.restriction = cursor.getInt(cursor.getColumnIndex(BaseEventsDBInfo.RESTRICTION));
        eventModel.thumbnailPic = cursor.getString(cursor.getColumnIndex(BaseEventsDBInfo.THUMBNAIL_PIC));
        eventModel.originalPic = cursor.getString(cursor.getColumnIndex(BaseEventsDBInfo.ORIGINAL_PIC));
        return eventModel;
    }
}
