package com.ucas.iplay.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ucas.iplay.R;
import com.ucas.iplay.core.model.AgendaModel;
import com.ucas.iplay.core.model.EventModel;
import com.ucas.iplay.ui.DetailsActivity;
import com.ucas.iplay.util.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by wanggang on 2015/6/21.
 */
public class AgendaAdapter extends BaseAdapter implements View.OnClickListener {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<AgendaModel> mAgendaList;

    public AgendaAdapter(Context context, ArrayList<AgendaModel> agendaList) {
        mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mAgendaList = agendaList;
    }

    @Override
    public int getCount() {
        return mAgendaList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAgendaList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.agenda_lv_item, null);

        AgendaModel agenda = mAgendaList.get(position);
        ArrayList<EventModel> events = agenda.events;
        String date = agenda.startAt;

        TextView day = (TextView) convertView.findViewById(R.id.tv_day);
        day.setText(StringUtil.parseLongTimeToString(date).substring(8));
        TextView month = (TextView) convertView.findViewById(R.id.tv_month);
        month.setText(getMonthStr(Integer.parseInt(StringUtil.parseLongTimeToString(date).substring(5, 7))));



        LinearLayout contentLayout = (LinearLayout) convertView.findViewById(R.id.layout_content);
        for (int i = 0; i < events.size(); i++) {
            TextView content = new TextView(convertView.getContext());
            content.setText(events.get(i).title);
            content.setHint(events.get(i).eventId + "");
            content.setOnClickListener(this);

        /*  textview控件格式设定*/
            content.setTextSize(18);
            content.setTextColor(Color.rgb(255,255,255));
            content.setBackgroundResource(R.drawable.tv_agendacontent_bg);
            Log.i("getview", events.get(i).title);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            // 控件之间边缘距离
            params.setMargins(10,10,10,10);
            contentLayout.addView(content, params);
        }

        return convertView;
    }



    @Override
    public void onClick(View v) {
        TextView content = (TextView) v;
        Intent intent = new Intent(mContext, DetailsActivity.class);
        intent.putExtra("eventid", Integer.parseInt(content.getHint()+""));
        mContext.startActivity(intent);
    }

    private static String getMonthStr(int month) {
        switch (month-1) {
            case Calendar.JANUARY:
                return "一月";
            case Calendar.FEBRUARY:
                return "二月";
            case Calendar.MARCH:
                return "三月";
            case Calendar.APRIL:
                return "四月";
            case Calendar.MAY:
                return "五月";
            case Calendar.JUNE:
                return "六月";
            case Calendar.JULY:
                return "七月";
            case Calendar.AUGUST:
                return "八月";
            case Calendar.SEPTEMBER:
                return "九月";
            case Calendar.OCTOBER:
                return "十月";
            case Calendar.NOVEMBER:
                return "十一月";
            case Calendar.DECEMBER:
                return "十二月";
            default:
                break;
        }
        return "";
    }
}