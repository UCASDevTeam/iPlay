package com.ucas.iplay.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.ucas.iplay.R;
import com.ucas.iplay.ui.base.BaseActivity;
import com.ucas.iplay.util.HttpUtil;
import com.ucas.iplay.util.SPUtil;
import com.ucas.iplay.util.StringUtil;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by wanggang on 2015/4/4.
 *
 * @author wanggang
 * @version 2.0 by ivanchou
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private Intent mIntent;
    private EditText mAccountEt;
    private EditText mPasswordEt;
    private Button mLoginBtn;
    private SPUtil mSpUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        getSupportActionBar().hide();
        mSpUtil = SPUtil.getSPUtil(this);

        initView();
    }

    private void initView() {
        mAccountEt = (EditText) findViewById(R.id.et_login_account);
        mPasswordEt = (EditText) findViewById(R.id.et_login_password);
        mLoginBtn = (Button) findViewById(R.id.btn_login);
        mLoginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                Log.i("MD5", mPasswordEt.getText().toString());

                Log.i("MD5", StringUtil.parseStringToMD5(mPasswordEt.getText().toString()));
                HttpUtil.logIn(getApplicationContext(), mAccountEt.getText().toString(), mPasswordEt.getText().toString(), new JsonHttpResponseHandler() {

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                try {
                                    String sessionId = (String) response.get(SPUtil.SESSIONID);
                                    String interestedTags = String.valueOf(response.get(SPUtil.INTERESTED_TAGS));
                                    mSpUtil.put(SPUtil.SESSIONID, sessionId);
                                    Calendar calendar = Calendar.getInstance();
                                    mSpUtil.put(SPUtil.LAST_LOGIN_TIME, String.valueOf(calendar.getTimeInMillis()));
                                    mSpUtil.put(SPUtil.INTERESTED_TAGS, interestedTags);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                super.onSuccess(statusCode, headers, response);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                                super.onFailure(statusCode, headers, responseString, throwable);
                            }
                        }
                );
                break;
        }
    }
}
