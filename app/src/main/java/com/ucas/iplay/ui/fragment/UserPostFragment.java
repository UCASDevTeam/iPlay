package com.ucas.iplay.ui.fragment;

import android.os.Bundle;

import com.ucas.iplay.ui.base.BaseFragment;

/**
 * Created by ivanchou on 4/11/15.
 */
public class UserPostFragment extends BaseFragment {

    public static UserPostFragment newInstance(int page, String title) {
        UserPostFragment userPostFragment = new UserPostFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("page", page);
        bundle.putString("title", title);
        userPostFragment.setArguments(bundle);
        return userPostFragment;
    }
}
