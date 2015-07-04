package com.ucas.iplay.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ucas.iplay.R;
import com.ucas.iplay.core.model.CommentModel;
import com.ucas.iplay.ui.adapter.CommentsAdapter;
import com.ucas.iplay.ui.base.BaseActivity;
import com.ucas.iplay.ui.view.CommentView;
import com.ucas.iplay.util.HttpUtil;
import com.ucas.iplay.util.SPUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ivanchou on 1/21/2015.
 * @author ivanchou
 */
public class CommentsActivity extends BaseActivity implements CommentView.OnSendClickListener{

    private ListView mCommentsLv;
    private CommentView mCommentCv;

    private int eventID;
    private int pageSize = 20;
    private int mPage = 1;
    private List<CommentModel> comments;
    private CommentsAdapter mCommentsAdapter;
    private SPUtil sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        sp = SPUtil.getSPUtil(this);
        eventID = getIntent().getIntExtra("eventid", 0);
        comments = new ArrayList<CommentModel>();
        mCommentsAdapter = new CommentsAdapter(this, comments);

        initViews();
        getCommentsData();

    }

    public void initViews() {
        mCommentsLv = (ListView) findViewById(R.id.lv_comments);
        mCommentCv = (CommentView) findViewById(R.id.cv_comment);
        mCommentsLv.setAdapter(mCommentsAdapter);
    }

    public void getCommentsData() {
        RequestParams params = new RequestParams();
        params.put("sessionid", sp.get("sessionid"));
        params.put("activityid", eventID);
        params.put("pagesize", pageSize);
        params.put("pagenumber", mPage);

        HttpUtil.getConmentsOfEvent(this, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e(TAG, response.toString());
                try {
                    getModels(response.getJSONArray("commentslist"));
                    mCommentsAdapter.notifyDataSetChanged();
                    mPage++;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // 停止刷新动画
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(TAG, responseString);
            }
        });
    }

    private void getModels(JSONArray response) throws JSONException {
        for(int i = 0; i < response.length(); i++){
            CommentModel model = new CommentModel();
            model.parse(response.getJSONObject(i));
            comments.add(model);
        }
    }

    @Override
    public void onSendClick(String text) {
        RequestParams params = new RequestParams();
        params.put("sessionid", sp.get("sessionid"));
        params.put("activityid", eventID);
        params.put("content", text);

        HttpUtil.commentEvent(this, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e(TAG, response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(TAG, responseString);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comments, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
