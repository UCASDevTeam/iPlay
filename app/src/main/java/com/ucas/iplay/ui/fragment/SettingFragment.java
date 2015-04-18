package com.ucas.iplay.ui.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.support.v4.app.FragmentTransaction;

import com.ucas.iplay.R;
import com.ucas.iplay.support.v4.preference.PreferenceFragment;

/**
 * Created by ivanchou on 4/18/15.
 */
public class SettingFragment extends PreferenceFragment implements OnPreferenceClickListener{

    private static final String PREF_LOGIN = "pref_login";
    private static final String PREF_INTEREST = "pref_interest";
    private static final String PREF_ABOUT = "pref_about";

    private TagsFragment mTagsFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        findPreference(PREF_LOGIN).setOnPreferenceClickListener(this);
        findPreference(PREF_INTEREST).setOnPreferenceClickListener(this);
        findPreference(PREF_ABOUT).setOnPreferenceClickListener(this);

    }

    @Override
    public boolean onPreferenceClick(final Preference preference) {
        if (preference.getKey().equals(PREF_LOGIN)) {

        } else if (preference.getKey().equals(PREF_INTEREST)) {
            if (mTagsFragment == null) {
                mTagsFragment = new TagsFragment();
            }
            FragmentTransaction fm = getFragmentManager().beginTransaction();
            fm.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fm.replace(R.id.content_frame, mTagsFragment);
            fm.addToBackStack(null);
            getActivity().setTitle(getString(R.string.drawer_item_changetags));
            fm.commit();
        } else if (preference.getKey().equals(PREF_ABOUT)) {

        }
        return false;
    }
}
