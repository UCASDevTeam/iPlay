package com.ucas.iplay.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.doomonafireball.betterpickers.radialtimepicker.RadialTimePickerDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ucas.iplay.R;
import com.ucas.iplay.core.model.EventModel;
import com.ucas.iplay.core.service.PostEventService;
import com.ucas.iplay.ui.base.BaseFragment;
import com.ucas.iplay.util.HttpUtil;
import com.ucas.iplay.util.SPUtil;
import com.ucas.iplay.util.StringUtil;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;

/**
 * Created by ivanchou on 1/19/2015.
 */
public class PostNewFragment extends BaseFragment implements View.OnClickListener, CalendarDatePickerDialog.OnDateSetListener, RadialTimePickerDialog.OnTimeSetListener {
    private static final String START_DATE_PICKER = "start_date_picker";
    private static final String START_TIME_PICKER = "start_time_picker";
    private static final String END_DATE_PICKER = "end_date_picker";
    private static final String END_TIME_PICKER = "end_time_picker";

    private final int SELECT_PHOTO = 1;

    private TextView mStartDateTv;
    private TextView mStartTimeTv;
    private TextView mEndDateTv;
    private TextView mEndTimeTv;

    private EditText mTitleEt;
    private EditText mPlaceEt;
    private EditText mContentEt;

    private Switch mStatusSw;
    private Button mSelectorBtn;
    private ImageView mPictureIv;

    private String mImagePath;
    private boolean mStatus = false;

    private long startDate;
    private long startTime;
    private long endDate;
    private long endTime;
    private long startAt;
    private long endAt;

    private SPUtil sp;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SELECT_PHOTO:
                    mImagePath = (String) msg.obj;
                    Log.e(TAG, mImagePath);
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mImagePath = "";
        sp = SPUtil.getSPUtil(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_postnew, container, false);

        mStartDateTv = (TextView) view.findViewById(R.id.tv_start_date);
        mStartTimeTv = (TextView) view.findViewById(R.id.tv_start_time);
        mEndDateTv = (TextView) view.findViewById(R.id.tv_end_date);
        mEndTimeTv = (TextView) view.findViewById(R.id.tv_end_time);

        mTitleEt = (EditText) view.findViewById(R.id.et_title);
        mPlaceEt = (EditText) view.findViewById(R.id.et_place);
        mContentEt = (EditText) view.findViewById(R.id.et_content);

        mStatusSw = (Switch) view.findViewById(R.id.sw_status);
        mSelectorBtn = (Button) view.findViewById(R.id.btn_picture_selector);
        mPictureIv = (ImageView) view.findViewById(R.id.iv_event_picture);

        mStartDateTv.setOnClickListener(this);
        mStartTimeTv.setOnClickListener(this);
        mEndDateTv.setOnClickListener(this);
        mEndTimeTv.setOnClickListener(this);
        mSelectorBtn.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        FragmentManager fm;
        Calendar calendar;
        CalendarDatePickerDialog calendarDatePickerDialog;
        RadialTimePickerDialog timePickerDialog;

        switch (v.getId()) {
            case R.id.tv_start_date:
                fm = getFragmentManager();
                calendar = Calendar.getInstance();
                calendarDatePickerDialog = CalendarDatePickerDialog
                        .newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                calendarDatePickerDialog.show(fm, START_DATE_PICKER);
                break;
            case R.id.tv_start_time:
                fm = getFragmentManager();
                calendar = Calendar.getInstance();
                timePickerDialog = RadialTimePickerDialog
                        .newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                                DateFormat.is24HourFormat(context));
                timePickerDialog.show(fm, START_TIME_PICKER);
                break;
            case R.id.tv_end_date:
                fm = getFragmentManager();
                calendar = Calendar.getInstance();
                calendarDatePickerDialog = CalendarDatePickerDialog
                        .newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                calendarDatePickerDialog.show(fm, END_DATE_PICKER);
                break;
            case R.id.tv_end_time:
                fm = getFragmentManager();
                calendar = Calendar.getInstance();
                timePickerDialog = RadialTimePickerDialog
                        .newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                                DateFormat.is24HourFormat(context));
                timePickerDialog.show(fm, END_TIME_PICKER);
                break;

            case R.id.sw_status:
                if (mStatusSw.isChecked()) {
                    mStatus = false;
                } else {
                    mStatus = true;
                }
                break;
            case R.id.btn_picture_selector:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDateSet(CalendarDatePickerDialog calendarDatePickerDialog, int year, int monthOfYear, int dayOfMonth) {
        String tag = calendarDatePickerDialog.getTag();
        Calendar calendar = Calendar.getInstance();
        if (tag.equals(START_DATE_PICKER)) {
            calendar.set(year, monthOfYear, dayOfMonth);
            startDate = calendar.getTimeInMillis();
            mStartDateTv.setText(StringUtil.parseDateToString(year, monthOfYear, dayOfMonth));
            return;
        }
        if (tag.equals(END_DATE_PICKER)) {
            calendar.set(year, monthOfYear, dayOfMonth);
            endDate = calendar.getTimeInMillis();
            mEndDateTv.setText(StringUtil.parseDateToString(year, monthOfYear, dayOfMonth));
            return;
        }

    }


    @Override
    public void onTimeSet(RadialTimePickerDialog radialTimePickerDialog, int hourOfDay, int minute) {
        String tag = radialTimePickerDialog.getTag();
        if (tag.equals(START_TIME_PICKER)) {
            startTime = (hourOfDay * 60 + minute) * 6000;
            mStartTimeTv.setText(StringUtil.parseTimeToString(hourOfDay, minute));
            return;
        }
        if (tag.equals(END_TIME_PICKER)) {
            endTime = (hourOfDay * 60 + minute) * 6000;
            mEndTimeTv.setText(StringUtil.parseTimeToString(hourOfDay, minute));
            return;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // 返回图片路径
            case SELECT_PHOTO:
                if (resultCode == getActivity().RESULT_OK) {
                    Uri imageUri = data.getData();
                    String picturePath = StringUtil.getRealPathFromURI(context, imageUri);

                    Message msg = new Message();
                    msg.what = SELECT_PHOTO;
                    msg.obj = picturePath;
                    mHandler.sendMessage(msg);
                }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_event_post:
                postNewEvent();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_post, menu);
    }


    private void postNewEvent() {
        String uid = StringUtil.randomString();
        startAt = startDate + startTime;
        endAt = endDate + endTime;
        Intent intent = new Intent(context, PostEventService.class);
        EventModel event = new EventModel();
        event.startAt = String.valueOf(startAt);
        event.endAt = String.valueOf(endAt);
        event.endrollBefore = "100000000";
        event.placeAt = "国科大";
        event.title = String.valueOf(mTitleEt.getText());
        event.content = String.valueOf(mContentEt.getText());
        event.tags = 1;
        event.maxPeople = 0;
        event.restriction = mStatus ? 1 : 0;
        event.originalPic = mImagePath;

        intent.putExtra("event", event);
        intent.putExtra("myactivityid", uid);
        intent.putExtra("sessionid", sp.get("sessionid"));

        context.startService(intent);
    }
}
