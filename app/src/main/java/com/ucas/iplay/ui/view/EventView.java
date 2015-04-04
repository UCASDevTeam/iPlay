package com.ucas.iplay.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ucas.iplay.R;
import com.ucas.iplay.core.model.EventModel;
import com.ucas.iplay.core.model.UserModel;

/**
 * Created by ivanchou on 4/5/15.
 */
public class EventView extends RelativeLayout {
    public EventView(Context context) {
        super(context);
        init();
    }

    public EventView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EventView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private TextView mTitleTv;
    private TextView mStartAtTv;

    private long mEventId;
    private UserModel mUser;

    private void init() {
        inflate(getContext(), R.layout.event_lv_item, this);
        mTitleTv = (TextView) findViewById(R.id.tv_title);
        mStartAtTv = (TextView) findViewById(R.id.tv_start_at);

    }

    public void parse(EventModel event) {
        mEventId = event.eventId;
        mTitleTv.setText(event.title);
        mStartAtTv.setText(event.startAt);

    }

    public long getEventId() {
        return mEventId;
    }
}
