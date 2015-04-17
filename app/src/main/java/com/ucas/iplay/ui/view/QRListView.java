package com.ucas.iplay.ui.view;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.ucas.iplay.R;
import com.ucas.iplay.util.QuickReturnUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivanchou on 4/17/15.
 */
public class QRListView extends ListView implements AbsListView.OnScrollListener{
    private Context mContext;
    private LayoutInflater mInflater;
    private View mFooterLoadingView;
    private FooterTagsView mFooterTagsView;
    private boolean isBottom = false;// 是否到达底部
    private DataChangeListener mCallbacks;

    private List<OnScrollListener> mExtraOnScrollListenerList = new ArrayList<OnScrollListener>();
    private int mMinFooterTranslation;
    private int mFooterDiffTotal = 0;
    private int mPrevScrollY = 0;
    private boolean mCanSlideInIdleScrollState = true;


    public QRListView(Context context) {
        this(context, null);
    }

    public QRListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QRListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        initViews();
    }

    private void initViews() {
        mInflater = LayoutInflater.from(mContext);
        mFooterLoadingView = mInflater.inflate(R.layout.listview_footer_layout, null);
        addFooterView(mFooterLoadingView);
        dismissFooterLoadingView();
        setOnScrollListener(this);
    }

    public void setTagsView(FooterTagsView tagsView) {
        if (mFooterTagsView == null) {
            mFooterTagsView = tagsView;
        }
        getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mMinFooterTranslation = mFooterTagsView.getHeight();
                    }
                });
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // 滚动到底部且 listview 的状态是空闲
        if (isBottom && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
            // 加载新的数据
            showFooterLoadingView();
            mCallbacks.onLoadMore();
        }

        for (AbsListView.OnScrollListener listener : mExtraOnScrollListenerList) {
            listener.onScrollStateChanged(view, scrollState);
        }

        if(scrollState == SCROLL_STATE_IDLE && mCanSlideInIdleScrollState) {

            int midFooter = mMinFooterTranslation / 2;
            if (-mFooterDiffTotal > 0 && -mFooterDiffTotal < midFooter) { // slide up
                ObjectAnimator anim = ObjectAnimator.ofFloat(mFooterTagsView, "translationY", mFooterTagsView.getTranslationY(), 0);
                anim.setDuration(100);
                anim.start();
                mFooterDiffTotal = 0;
            } else if (-mFooterDiffTotal < mMinFooterTranslation && -mFooterDiffTotal >= midFooter) { // slide down
                ObjectAnimator anim = ObjectAnimator.ofFloat(mFooterTagsView, "translationY", mFooterTagsView.getTranslationY(), mMinFooterTranslation);
                anim.setDuration(100);
                anim.start();
                mFooterDiffTotal = -mMinFooterTranslation;
            }
        }
    }

    @Override
    public void onScroll(AbsListView listview, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // 判断是否到底
        if (firstVisibleItem + visibleItemCount == totalItemCount) {
            isBottom = true;
        } else {
            isBottom = false;
        }

        for (AbsListView.OnScrollListener listener : mExtraOnScrollListenerList) {
            listener.onScroll(listview, firstVisibleItem, visibleItemCount, totalItemCount);
        }
        int scrollY = QuickReturnUtils.getScrollY(listview);
        int diff = mPrevScrollY - scrollY;
        if (diff != 0) {
            if (diff < 0) { // scrolling down
                mFooterDiffTotal = Math.max(mFooterDiffTotal + diff, -mMinFooterTranslation);
            } else { // scrolling up
                mFooterDiffTotal = Math.min(Math.max(mFooterDiffTotal + diff, -mMinFooterTranslation), 0);
            }

            mFooterTagsView.setTranslationY(-mFooterDiffTotal);
        }
        mPrevScrollY = scrollY;
    }

    public void setDataChangeListener(DataChangeListener listener) {
        this.mCallbacks = listener;
    }


    public interface DataChangeListener {
        /**
         * 从网络加载更多的数据
         */
        public void onLoadMore();
    }

    private void showFooterLoadingView() {
        View view = mFooterLoadingView.findViewById(R.id.pb_loding);
        view.setVisibility(View.VISIBLE);
        view.setScaleX(1.0f);
        view.setScaleY(1.0f);
        view.setAlpha(1.0f);
        mFooterLoadingView.findViewById(R.id.tv_load_failed).setVisibility(View.GONE);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void dismissFooterLoadingView() {
        final View progressbar = mFooterLoadingView.findViewById(R.id.pb_loding);
        progressbar.animate().scaleX(0).scaleY(0).alpha(0.5f).setDuration(300)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        progressbar.setVisibility(View.GONE);
                    }
                });
        mFooterLoadingView.findViewById(R.id.tv_load_failed).setVisibility(View.GONE);
    }

    private void showErrorFooterLoadingView() {
        View view = mFooterLoadingView.findViewById(R.id.pb_loding);
        view.setVisibility(View.GONE);
        TextView tv = ((TextView) mFooterLoadingView.findViewById(R.id.tv_load_failed));
        tv.setVisibility(View.VISIBLE);
    }
}
