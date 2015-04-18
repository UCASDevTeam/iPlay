package com.ucas.iplay.ui.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.ucas.iplay.R;

/**
 * Created by ivanchou on 4/18/15.
 */
public class SettingFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}
