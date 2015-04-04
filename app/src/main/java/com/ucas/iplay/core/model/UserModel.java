package com.ucas.iplay.core.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ivanchou on 1/27/2015.
 */
public class UserModel {

    public int userId;// 用户id

    public String name;// 用户名

    public String avatar;// 用户头像

    public int schoolId;

    public long birthday;

    public int sex;

    public String phone;

    public String nickname;

    public long registerTime;

    public int academyId;

    public String sign;// 个人介绍

    public int interestedTags;

    public int majorId;

    public long lastLoginTime;

    public String thumbnailPhoto;

    public String realname;

    public int qq;

    public void parse(JSONObject jsonObject) throws JSONException {
        userId = jsonObject.getInt("userid");
        name = jsonObject.getString("username");
        avatar = jsonObject.getString("avatar");
    }
}
