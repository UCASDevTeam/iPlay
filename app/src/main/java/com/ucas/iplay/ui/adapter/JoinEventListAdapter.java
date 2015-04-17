package com.ucas.iplay.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ucas.iplay.R;
import com.ucas.iplay.core.model.JoinEventModel;

import java.util.List;

/**
 * Created by wanggang on 2015/4/17.
 */
public class JoinEventListAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<JoinEventModel> mJoinEventsList;

    public JoinEventListAdapter(Context context, List<JoinEventModel> eventsList) {
        mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mJoinEventsList = eventsList;
    }

    @Override
    public int getCount() {
        return mJoinEventsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mJoinEventsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.joinevent_lv_item, null);
            viewHolder = new ViewHolder();
            viewHolder.startAtTv = (TextView) convertView.findViewById(R.id.tv_time_scope);
            viewHolder.titleTv = (TextView) convertView.findViewById(R.id.tv_title);
//            viewHolder.jointedIv = (ImageView) convertView.findViewById(R.id.iv_jointed);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 填充数据
        if (mJoinEventsList.size() > 0) {
            JoinEventModel event = mJoinEventsList.get(position);
            viewHolder.startAtTv.setText(event.startAt);
            viewHolder.titleTv.setText(event.title);
            // 设置参加的图片标识
//            viewHolder.jointedIv.setImageResource(R.drawable.ic_drawer);
        }

        return convertView;
    }

    class ViewHolder {
        public TextView startAtTv;
        public TextView titleTv;
        public ImageView jointedIv;
    }
}
