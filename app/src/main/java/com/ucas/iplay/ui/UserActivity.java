package com.ucas.iplay.ui;

import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ucas.iplay.R;
import com.ucas.iplay.core.model.UserModel;
import com.ucas.iplay.ui.adapter.UserInfoPagerAdapter;
import com.ucas.iplay.ui.base.BaseActivity;

/**
 * @author ivanchou
 * Created by ivanchou on 4/11/15.
 */
public class UserActivity extends BaseActivity {

    private ViewPager mViewPager;
    private UserInfoPagerAdapter mPagerAdapter;
    private UserModel mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mUser = (UserModel) getIntent().getSerializableExtra("user");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mUser.name);

        initView();
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.vp_userinfo);

        mPagerAdapter = new UserInfoPagerAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.action_settings:
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
