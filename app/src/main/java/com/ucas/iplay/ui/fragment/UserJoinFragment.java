package com.ucas.iplay.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ucas.iplay.R;
import com.ucas.iplay.core.model.EventModel;
import com.ucas.iplay.ui.adapter.EventListAdapter;
import com.ucas.iplay.ui.base.BaseFragment;
import com.ucas.iplay.util.HttpUtil;
import com.ucas.iplay.util.PhoneUtil;
import com.ucas.iplay.util.SPUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanggang on 2015/4/16.
 * @author wanggang
 * @version 2.0 by ivanchou
 */
public class UserJoinFragment extends BaseFragment {
    private ListView mListView;
    private List<EventModel> mEventModelList;
    private EventListAdapter mEventListAdapter;
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
        // mEventModelList、mEventListAdapter 的初始化已移到onCreateView方法中，不能在onCreate方法

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mEventModelList = new ArrayList<EventModel>();
        mEventListAdapter = new EventListAdapter(getActivity(), mEventModelList);
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
        if (PhoneUtil.isConnect(getActivity())) { // 网络连接正常时，联网获取数据
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
            EventModel model = new EventModel();
            model.parse(response.getJSONObject(i));
            mEventModelList.add(model);
        }
    }

}
