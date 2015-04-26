package com.ucas.iplay.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

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
        eventID = getIntent().getIntExtra(DetailsFragment.EVENT_ID,0);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setCustomView(R.layout.action_bar_details);
        setContentView(R.layout.activity_details);
        mBackButton = (Button)findViewById(R.id.bt_details_return);
        mContent = (FrameLayout)findViewById(R.id.content_details);

        initialListener();

        startDetailsFragment();
    }

    private void initialListener(){
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
        return super.onCreateOptionsMenu(menu);
    }

}
