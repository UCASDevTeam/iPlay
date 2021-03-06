package com.ucas.iplay.ui.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.widget.ToggleButton;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ucas.iplay.core.db.EventsDataHelper;
import com.ucas.iplay.ui.base.BaseFragment;
import com.ucas.iplay.core.model.EventModel;
import com.ucas.iplay.R;
import com.ucas.iplay.util.HttpUtil;
import com.ucas.iplay.core.model.UserModel;
import com.ucas.iplay.util.StringUtil;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailsFragment extends BaseFragment implements OnRefreshListener{

    private Activity mActivity;
    private View mDetailsView;
    private View mFragmentContainerView;
    private PosterAlbumFragment mPosterAlbumFragment;
    private EventsDataHelper mEventsDataHelper;

    public static final int POSTER_ON_CLICK = 0;
    public static final int MAP_ON_CLICK = 1;
    public static final String EVENT_ID = "eventid";
    /*  事件参数    */
    private EventModel mEvent;
    private int mEventID;
    private int mUserID;
    private String startAt;
    private String endAt;

    private TextView mAuthorNickView;
    private TextView mTitleView;
    private TextView mContentView;
    private TextView mSupportView;
    private Button mMapButton;
    private ImageView mPosterView;
    private SwipeRefreshLayout mSwipRefreshLayout;
    private ToggleButton mLikeToggleButton;
    private Button mBackButton;
    private ActionBar mActionBar;
    private ImageView [] mImageViews;
    private LinearLayout mListView;

    RelativeLayout mPlaceLayout;
    RelativeLayout mDateLayout;
    RelativeLayout mCostLayout;
    RelativeLayout mPhoneLayout;
    RelativeLayout mEmailLayout;
    LinearLayout.LayoutParams mListParam;

    private int displayWidth;
    private int displayHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*  获取时间ID和用户ID */
        mEventID = getArguments().getInt(EVENT_ID,0);
        mEventsDataHelper = new EventsDataHelper(context);

        mActionBar = this.getActivity().getActionBar();
        //mActionBar.hide();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /**  获取发展宽度  */
        Point size = new Point();
        this.getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        displayWidth = size.x;
        displayHeight = size.y;

        mListParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        mPlaceLayout = (RelativeLayout)inflater.inflate(R.layout.fragment_details_items,container,false);
        mDateLayout = (RelativeLayout)inflater.inflate(R.layout.fragment_details_items,container,false);
        mCostLayout = (RelativeLayout)inflater.inflate(R.layout.fragment_details_items,container,false);
        mPhoneLayout = (RelativeLayout)inflater.inflate(R.layout.fragment_details_items,container,false);
        mEmailLayout = (RelativeLayout)inflater.inflate(R.layout.fragment_details_items,container,false);

        /**  获取界面组件  */
        View view = inflater.inflate(R.layout.fragment_details,container,false);
        mDetailsView = view;

        mListView = (LinearLayout) mDetailsView.findViewById(R.id.rl_details_list);

        mAuthorNickView = (TextView) mDetailsView.findViewById(R.id.tv_details_author_nick);
        mTitleView = (TextView) mDetailsView.findViewById(R.id.tv_details_title);
        mContentView = (TextView) mDetailsView.findViewById(R.id.tv_details_content);

        mSwipRefreshLayout = (SwipeRefreshLayout) mDetailsView.findViewById(R.id.srl_details_refresh);
        //mBackButton = (Button)mDetailsView.findViewById(R.id.bt_details_return);
        //mSupportView = (TextView) mDetailsView.findViewById(R.id.tv_details_supporter);


        /**  初始化界面组件 */
        initData();
        drawView();
        /*  设置监听器   */
        setListener();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        /*try {
            mCallback = (DetailsCallback)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("The Activity must implement DetailsCallback!");4
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        releaseFragmentStack();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseFragmentStack();
    }

    @Override
    public void onRefresh() {
        getDataFromHttp();
        getData();
    }

    /*  绘制界面    */
    private void drawView(){
        mListView.removeAllViews();
        mAuthorNickView.setText(mEvent.author.name);
        mTitleView.setText(mEvent.title);
        mContentView.setText(mEvent.content);

        /**   从Event中解析出活动开始时间  */
        startAt = StringUtil.parseLongTimeToWholeString(mEvent.startAt);

        TextView dt, lt;
        ImageView iv;
        /**   地点描述：具体地点 城市/国家
        *        空格为分隔符             */
        if (mEvent.placeAt!=null && mEvent.placeAt.length()>0) {
            mListView.addView(mPlaceLayout, mListParam);
            iv = (ImageView)mPlaceLayout.findViewById(R.id.iv_details_item_icon);
            iv.setImageResource(R.drawable.ic_location);
            dt = (TextView)mPlaceLayout.findViewById(R.id.tv_details_item_dt);
            lt = (TextView)mPlaceLayout.findViewById(R.id.tv_details_item_lt);
            int blank = mEvent.placeAt.indexOf(" ");
            if (blank > 0) {
                dt.setText(mEvent.placeAt.substring(0, blank));
                lt.setText(mEvent.placeAt.substring(blank,mEvent.placeAt.length()));
            }
            else {
                dt.setText(mEvent.placeAt);
                lt.setText("中国");
            }
        }
        /**   时间描述：hh/mm year-mm-day
        *        空格为分隔符             */
        if (startAt!=null && startAt.length()>0) {
            mListView.addView(mDateLayout, mListParam);
            iv = (ImageView)mDateLayout.findViewById(R.id.iv_details_item_icon);
            iv.setImageResource(R.drawable.ic_time);
            dt = (TextView)mDateLayout.findViewById(R.id.tv_details_item_dt);
            lt = (TextView)mDateLayout.findViewById(R.id.tv_details_item_lt);
            int blank = startAt.indexOf(" ");
            if (blank > 0) {
                lt.setText(startAt.substring(0, blank));
                dt.setText(startAt.substring(blank,startAt.length()));
            }
            else {
                dt.setText(startAt);
                lt.setText("2015-05-20");
            }
        }
        if (mEvent.cost != null && mEvent.cost.length()>0) {
            mListView.addView(mCostLayout, mListParam);
            iv = (ImageView)mCostLayout.findViewById(R.id.iv_details_item_icon);
            iv.setImageResource(R.drawable.ic_cost);
            dt = (TextView)mCostLayout.findViewById(R.id.tv_details_item_dt);
            lt = (TextView)mCostLayout.findViewById(R.id.tv_details_item_lt);
            int blank = mEvent.cost.indexOf(" ");
            if (blank > 0) {
                lt.setText(mEvent.cost.substring(0, blank));
                dt.setText(mEvent.cost.substring(blank,mEvent.cost.length()));
            }
            else {
                dt.setText(mEvent.cost);
                lt.setText("2015-05-20");
            }
        }
        if (mEvent.author!=null && mEvent.author.phone != null && mEvent.author.phone.length()>0) {
            mListView.addView(mPhoneLayout, mListParam);
            iv = (ImageView)mPhoneLayout.findViewById(R.id.iv_details_item_icon);
            iv.setImageResource(R.drawable.ic_phone);
            dt = (TextView)mPhoneLayout.findViewById(R.id.tv_details_item_dt);
            lt = (TextView)mPhoneLayout.findViewById(R.id.tv_details_item_lt);
            int blank = mEvent.author.phone.indexOf(" ");
            if (blank > 0) {
                lt.setText(mEvent.author.phone.substring(0, blank));
                dt.setText(mEvent.author.phone.substring(blank,mEvent.author.phone.length()));
            }
            else {
                dt.setText(mEvent.author.phone);
                lt.setText("手机");
            }
        }
        if (mEvent.email!=null && mEvent.email.length()>0) {
            mListView.addView(mEmailLayout, mListParam);
            iv = (ImageView)mEmailLayout.findViewById(R.id.iv_details_item_icon);
            iv.setImageResource(R.drawable.ic_email);
            dt = (TextView)mEmailLayout.findViewById(R.id.tv_details_item_dt);
            lt = (TextView)mEmailLayout.findViewById(R.id.tv_details_item_lt);
            int blank = mEvent.email.indexOf(" ");
            if (blank > 0) {
                lt.setText(mEvent.email.substring(0, blank));
                dt.setText(mEvent.email.substring(blank,mEvent.email.length()));
            }
            else {
                dt.setText(mEvent.email);
                lt.setText("2015-05-20");
            }
        }
        //mSupportView.setText(mEvent.supporter);
        //displayPictures();
    }

    /*  获取数据后刷新界面   */
    public void getData(){
        drawView();

        mAuthorNickView.invalidate();
        mListView.invalidate();
    }

    /*  设置事件    */
    public void setEvent(EventModel event){
        mEvent = event;
        getData();
        return;
    }

    /*  设置监听器   */
    private void setListener()
    {

        /*mMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
            }
        });*/

        /*mPosterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDetailsFragmentClick(POSTER_ON_CLICK);
            }
        });*/

        mSwipRefreshLayout.setOnRefreshListener(this);
        mSwipRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        /*mLikeToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Log.v("Like", "selected ");
                    likeSelected();
                }else{
                    unLikeSelected();
                }
            }
        });*/

        /*mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActionBar.show();
                releaseFragmentStack();
            }
        });*/
    }

    /*  喜欢  */
    private void likeSelected(){
        HttpUtil.setInterestAtEvent(context,mEventID,new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                mLikeToggleButton.setChecked(true);
                mEventsDataHelper.updateJointed(mEventID,1);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                mLikeToggleButton.setChecked(false);
            }
        });
    }

    /*  不喜欢 */
    private void unLikeSelected(){}

    /*  释放fragment队列*/
    private void releaseFragmentStack(){
        for (int i =0;i<getFragmentManager().getBackStackEntryCount();i++){
            getFragmentManager().popBackStack();
        }
    }
    /*   启动线程，获取活动  */
    private void initData(){
        mEvent = mEventsDataHelper.queryById(mEventID);
        if (mEvent == null){
            createTestEvent();
        }
        getDataFromHttp();
    }

    /*  从服务器获取数据    */

    private void getDataFromHttp(){
        HttpUtil.getEventByEventId(getActivity(),mEventID,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                getModelFromJason(response);
                getAuthorDataFromHttp();
                List<EventModel> modles = new ArrayList<EventModel>();
                modles.add(mEvent);
                mEventsDataHelper.bulkInsert(modles);
                mSwipRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }
    private void getAuthorDataFromHttp(){
        HttpUtil.getUserInfoByUserId(getActivity(),mEvent.author.userId,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    mEvent.author.parse(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getData();
                super.onSuccess(statusCode,headers,response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }
    /*  解析海报图像  */
    private void reSolvePosterImage(){
        int [] mImages = new int[] {
        };

        mImageViews = new ImageView[mImages.length];
        for (int i=0;i<mImages.length;i++){
            ImageView imageView = new ImageView(this.getActivity());
            imageView.setImageResource(mImages[i]);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setAdjustViewBounds(true);
            mImageViews[i] = imageView;
        }
    }

    private void displayPictures(){
        String imageUrl = "https://lh6.googleusercontent.com/-55osAWw3x0Q/URquUtcFr5I/AAAAAAAAAbs/rWlj1RUKrYI/s1024/A%252520Photographer.jpg";

        //显示图片的配置
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.details_post_loading)
                .showImageOnFail(R.drawable.details_post_loading)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        ImageLoader.getInstance().displayImage(imageUrl, mPosterView, options);
    }
    /*  初始化事件   */
    private void createTestEvent(){
        if(mEvent == null) {
            mEvent = new EventModel();
        }
        mEvent.author = new UserModel();
        mEvent.author.name = "";
        mEvent.author.userId = 0;
        mEvent.startAt = "";
        mEvent.endAt = "";
        mEvent.placeAt = "";
        mEvent.title = "";
        mEvent.content = "";
        reSolvePosterImage();

    }

    /*  从JSON包中解析事件到mEvent中 */
    private void getModelFromJason(JSONObject jsonObject){
        if(mEvent==null){
            mEvent = new EventModel();
        }
        try {
            mEvent.parse(jsonObject);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /*  详情界面借口函数
    *   onDetailsFragmentClick
    *       收集来自详情界面的点击事件
    *   getEvent
    *       详情界面读取事件*/
    public void onDetailsFragmentClick(int viewID) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        switch (viewID){
            case DetailsFragment.POSTER_ON_CLICK:
                /*if (mPosterAlbumFragment == null){
                    mPosterAlbumFragment = new PosterAlbumFragment();
                }
                mPosterAlbumFragment.setImageViews(mImageViews);
                fragmentTransaction.add(R.id.content_frame,mPosterAlbumFragment).addToBackStack("DetailsFragment");*/
                break;
            case DetailsFragment.MAP_ON_CLICK:
                break;
            default:
                break;
        }
        fragmentTransaction.commit();
    }

    public EventModel getEvent() {
        return null;
    }
/*
    *//*End Test*//*
    public interface DetailsCallback{

    }*/

}
