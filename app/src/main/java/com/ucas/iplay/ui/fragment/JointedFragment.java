package com.ucas.iplay.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ucas.iplay.R;
import com.ucas.iplay.core.model.AgendaModel;
import com.ucas.iplay.core.model.EventModel;
import com.ucas.iplay.ui.adapter.AgendaAdapter;
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
 * Created by ivanchou on 1/19/2015.
 */
public class JointedFragment extends BaseFragment {
    private ListView mListView;
    private List<EventModel> mEventModelList;
    private ArrayList<AgendaModel> mAgendaModelList;
    private AgendaAdapter mAgendaAdapter;
    private SPUtil mSpUtil;


    public static JointedFragment newInstance() {
        JointedFragment fragment = new JointedFragment();
        Bundle bundle = new Bundle();

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mEventModelList = new ArrayList<EventModel>();
        mAgendaModelList = new ArrayList<AgendaModel>();
        mAgendaAdapter = new AgendaAdapter(getActivity(), mAgendaModelList);

        View view = inflater.inflate(R.layout.fragment_alltag, container, false);
        mListView = (ListView) view.findViewById(R.id.lv_alltag);
        mListView.setAdapter(mAgendaAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSpUtil = SPUtil.getSPUtil(getActivity());
        getUserJoinData();
    }

    private void getUserJoinData() {
        Log.i("tag", "getuserjoindata is running");
        if (PhoneUtil.isConnect(getActivity())) { // 网络连接正常时，联网获取数据
            RequestParams requestParams = new RequestParams();
            HttpUtil.getJointedEventOfUser(getActivity(), requestParams, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        getEventModels(response.getJSONArray("joinhistories"));
                        getAgendaModels(mEventModelList);
                        mSpUtil.put("joinhistories", response.getJSONArray("joinhistories").toString());
                        mAgendaAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    super.onSuccess(statusCode, headers, response);
                    Log.i("tag", "super onSuccess");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                }
            });


        } else { // 无网络，获取sp里的数据
            Log.i("tag", "not connect");
            try {
                getEventModels(new JSONArray(mSpUtil.get("joinhistories")));
                getAgendaModels(mEventModelList);
                mAgendaAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void getEventModels(JSONArray response) throws JSONException {
        Log.i("tag", "geteventmodels is running");
        SPUtil sp = SPUtil.getSPUtil(getActivity());
        Log.i("tag", sp.get("sessionid"));
        for (int i = 0; i < response.length(); i++) {
            EventModel model = new EventModel();
            model.parse(response.getJSONObject(i));
            mEventModelList.add(model);
        }
    }

    private void getAgendaModels(List<EventModel> mEventModelList) {
        int j = 0;
        Log.i("tag", "getagendamodels is running");
        AgendaModel agendaModel = new AgendaModel();
        for (int i = 0; i< mEventModelList.size();i++) {
            if (i == 0) {
                agendaModel = new AgendaModel(mEventModelList.get(i));
            } else if (agendaModel.startAt.equals(mEventModelList.get(i).startAt)) {
                agendaModel.addEvent(mEventModelList.get(i));
                j = 0;
            }else {
                mAgendaModelList.add(agendaModel);
                agendaModel = new AgendaModel(mEventModelList.get(i));
                j = 1;
            }
        }
        if (j == 1) {
            mAgendaModelList.add(agendaModel);
        }
    }
}
