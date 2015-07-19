package com.ucas.iplay.ui;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.view.LayoutInflater;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ucas.iplay.R;
import com.ucas.iplay.core.db.EventsDataHelper;
import com.ucas.iplay.core.model.EventModel;
import com.ucas.iplay.core.model.UserModel;
import com.ucas.iplay.ui.base.BaseActivity;
import com.ucas.iplay.ui.fragment.DetailsFragment;
import com.ucas.iplay.ui.fragment.PosterAlbumFragment;
import com.ucas.iplay.util.HttpUtil;
import com.ucas.iplay.util.StringUtil;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Origa on 2015/4/24.
 */
public class DetailsActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
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
    private ImageView[] mImageViews;
    private LinearLayout mListView;

    RelativeLayout mPlaceLayout;
    RelativeLayout mDateLayout;
    RelativeLayout mCostLayout;
    RelativeLayout mPhoneLayout;
    RelativeLayout mEmailLayout;
    LinearLayout.LayoutParams mListParam;

    private int displayWidth;
    private int displayHeight;

    ActionBar mActionBar;
    Button mBackButton;
    FrameLayout mContent;
    private Toolbar mToolBar;
    int eventID = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setTheme(R.style.AppTheme_Details);

        setContentView(R.layout.activity_details);

        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(config);

        mEventID = getIntent().getIntExtra(DetailsFragment.EVENT_ID,0);

        mToolBar = (Toolbar)findViewById(R.id.toolbar);

        setSupportActionBar(mToolBar);

        mEventsDataHelper = new EventsDataHelper(this);

        initial();
    }

    private void initial() {
        Point size = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(size);
        displayWidth = size.x;
        displayHeight = size.y;

        LayoutInflater inflater = this.getLayoutInflater();
        mListParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        mPlaceLayout = (RelativeLayout)inflater.inflate(R.layout.fragment_details_items,null,false);
        mDateLayout = (RelativeLayout)inflater.inflate(R.layout.fragment_details_items,null,false);
        mCostLayout = (RelativeLayout)inflater.inflate(R.layout.fragment_details_items,null,false);
        mPhoneLayout = (RelativeLayout)inflater.inflate(R.layout.fragment_details_items,null,false);
        mEmailLayout = (RelativeLayout)inflater.inflate(R.layout.fragment_details_items,null,false);

        /**  获取界面组件  */
        mDetailsView = findViewById(R.id.fl_details);

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


    private void initData() {
        mEvent = mEventsDataHelper.queryById(mEventID);
        if (mEvent == null){
            createTestEvent();
        }
        getDataFromHttp();
    }

        /*  从服务器获取数据    */

    private void getDataFromHttp(){
        HttpUtil.getEventByEventId(this, mEventID, new JsonHttpResponseHandler() {
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
    private void getAuthorDataFromHttp(){
        HttpUtil.getUserInfoByUserId(this,mEvent.author.userId,new JsonHttpResponseHandler(){
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
    /*  获取数据后刷新界面   */
    public void getData(){
        drawView();

        mAuthorNickView.invalidate();
        mListView.invalidate();
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

    private void createTestEvent() {
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
    //    reSolvePosterImage();
    }
    /*  解析海报图像  */
    private void reSolvePosterImage(){
        int [] mImages = new int[] {
        };

        mImageViews = new ImageView[mImages.length];
        for (int i=0;i<mImages.length;i++){
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(mImages[i]);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setAdjustViewBounds(true);
            mImageViews[i] = imageView;
        }
    }


    private void initialListener(){
/*        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });*/
    }

    private void startDetailsFragment(){
        /*FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        DetailsFragment detailsFragment = new DetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(DetailsFragment.EVENT_ID, eventID);
        detailsFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.content_details, detailsFragment);
        fragmentTransaction.commit();*/
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details,menu);
        return true;
    }

    @Override
    public void onRefresh() {
        getDataFromHttp();
        getData();
    }

}
