package com.ucas.iplay.ui.fragment;

import android.content.Context;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ucas.iplay.R;
import com.ucas.iplay.core.model.EventModel;
import com.ucas.iplay.core.model.JoinEventModel;
import com.ucas.iplay.ui.adapter.EventListAdapter;
import com.ucas.iplay.ui.adapter.JoinEventListAdapter;
import com.ucas.iplay.ui.base.BaseFragment;
import com.ucas.iplay.util.HttpUtil;
import com.ucas.iplay.util.SPUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanggang on 2015/4/16.
 */
public class UserJoinFragment extends BaseFragment {
    private ListView mListView;
    private List<JoinEventModel> mEventModelList;
    private JoinEventListAdapter mEventListAdapter;
    private SPUtil mSpUtil;

    public static UserJoinFragment newInstance(int page, String title) {
        UserJoinFragment userJoinFragment = new UserJoinFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("page", page);
        bundle.putString("title", title);
        userJoinFragment.setArguments(bundle);
        return userJoinFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*      放到 onCreateView方法里去了
        mEventModelList = new ArrayList<JoinEventModel>();
        mEventListAdapter = new JoinEventListAdapter(getActivity(), mEventModelList);*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mEventModelList = new ArrayList<JoinEventModel>();
        mEventListAdapter = new JoinEventListAdapter(getActivity(), mEventModelList);

        View view = inflater.inflate(R.layout.fragment_alltag, container, false);
        mListView = (ListView) view.findViewById(R.id.lv_alltag);
        mListView.setAdapter(mEventListAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSpUtil = SPUtil.getSPUtil(getActivity());
        getUserJoinData();
    }

    private void getUserJoinData() {
        if (isConnect(getActivity())) { // 网络连接正常时，联网获取数据
            RequestParams requestParams = new RequestParams();
            HttpUtil.getJointedEventOfUser(getActivity(), requestParams, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        getModels(response.getJSONArray("joinhistories"));
                        mSpUtil.put("joinhistories", response.getJSONArray("joinhistories").toString());
                        mEventListAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    super.onSuccess(statusCode, headers, response);
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                }
            });

/*          对应的  HttpUtil  的 getJointedEventOfUser（） 方法
            public static void getJointedEventOfUser(Context context, RequestParams params, final JsonHttpResponseHandler responseHandler) {
                SPUtil sp = SPUtil.getSPUtil(context);
                params.put("sessionid", sp.get("sessionid"));
                new AsyncHttpClient().post(context,USER_JOIN_EVENTS, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        responseHandler.onSuccess(statusCode, headers, response);
                        super.onSuccess(statusCode, headers, response);
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        responseHandler.onFailure(statusCode, headers, responseString, throwable);
                        super.onFailure(statusCode, headers, responseString, throwable);
                    }
                });
            }*/
        } else { // 无网络，获取sp里的数据
            try {
                getModels(new JSONArray(mSpUtil.get("joinhistories")));
                mEventListAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void getModels(JSONArray response) throws JSONException {
        for (int i = 0; i < response.length(); i++) {
            JoinEventModel model = new JoinEventModel();
            model.parse(response.getJSONObject(i));
            mEventModelList.add(model);
        }
    }

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
