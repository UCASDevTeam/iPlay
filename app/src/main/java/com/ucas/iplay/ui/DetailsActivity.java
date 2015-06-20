package com.ucas.iplay.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ucas.iplay.R;
import com.ucas.iplay.ui.base.BaseActivity;
import com.ucas.iplay.ui.fragment.DetailsFragment;

/**
 * Created by Origa on 2015/4/24.
 */
public class DetailsActivity extends BaseActivity {
    ActionBar mActionBar;
    Button mBackButton;
    FrameLayout mContent;
    int eventID = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setTheme(R.style.AppTheme_Details);

        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(config);

        eventID = getIntent().getIntExtra(DetailsFragment.EVENT_ID,0);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        mActionBar = getSupportActionBar();
        mActionBar.setTitle(R.string.details_tag_activity);
//        mActionBar.setDisplayHomeAsUpEnabled(true);
//        mActionBar.setHomeAsUpIndicator(R.drawable.ic_back);

        View homeView = findViewById(android.R.id.home);
        homeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setContentView(R.layout.activity_details);
        mBackButton = (Button)findViewById(R.id.bt_details_return);
        mContent = (FrameLayout)findViewById(R.id.content_details);

        initialListener();

        startDetailsFragment();
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
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        DetailsFragment detailsFragment = new DetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(DetailsFragment.EVENT_ID, eventID);
        detailsFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.content_details, detailsFragment);
        fragmentTransaction.commit();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details,menu);
        return true;
    }

}
