package com.ucas.iplay.core.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by ivanchou on 7/1/15.
 */
public class CommentModel implements Serializable {

    public UserModel user;

    public int commentId;

    public String createAt;

    public String content;

    public void parse(JSONObject jsonObject) throws JSONException{
        user = new UserModel();
        user.userId = jsonObject.getInt("userid");
        user.name = jsonObject.getString("nickname");

        commentId = jsonObject.getInt("commentid");
        createAt = jsonObject.getString("createat");
        content = jsonObject.getString("content");
    }
}
