package com.ucas.iplay.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ucas.iplay.R;
import com.ucas.iplay.core.model.TagModel;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * author  hoolee 2015/04/10
 */
public class TagsSimpleAdapter extends SimpleAdapter {

    private class TagsAdapterHolder {
        ImageView tagImage;
        TextView tagName;
        TextView tagDesc;
        CheckBox tagCheck;
    }

    private List<Map<String, Object>> mAppList;
    private LayoutInflater mInflater;
    private Context mContext;
    private String[] keyString;
    private int[] valueViewID;
    private TagsAdapterHolder holder;
    private boolean checkedStatus[];
    private TagModel tagsModel[];
    public TagsSimpleAdapter(Context c, List<Map<String, Object>> listItems, int resource,
                             String[] from, int[] to, TagModel[] tagsModel){
        super(c, listItems, resource, from, to);
        mAppList = listItems;
        mContext = c;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        keyString = from;
        valueViewID = to;
        this.tagsModel = tagsModel;
        checkedStatus = new boolean[mAppList.size()];
        Arrays.fill(checkedStatus, false);
    }
}
