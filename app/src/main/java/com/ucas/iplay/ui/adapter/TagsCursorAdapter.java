package com.ucas.iplay.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.ucas.iplay.core.db.TagsDataHelper;
import com.ucas.iplay.core.model.TagModel;
import com.ucas.iplay.ui.view.TagView;
import com.ucas.iplay.ui.view.TagView.OnInterestedClickListener;
import com.ucas.iplay.util.HttpUtil;

/**
 * Created by ivanchou on 4/11/15.
 */
public class TagsCursorAdapter extends CursorAdapter implements OnInterestedClickListener{

    private Context mContext;
    private TagsDataHelper mTagsDataHelper;

    public TagsCursorAdapter(Context context) {
        super(context, null, false);
        mContext = context;
        mTagsDataHelper = new TagsDataHelper(context);
    }

    @Override
    public Object getItem(int position) {
        getCursor().moveToPosition(position);
        return TagModel.fromCursor(mContext, getCursor());
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return new TagView(context);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TagView tagView = (TagView) view;
        TagModel tagModel = TagModel.fromCursor(context, cursor);
        tagView.parse(tagModel);
        tagView.setOnInterestedClickListener(this);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    /**
     * 订阅 Tag 的回调事件
     * @param tagId
     * @param interested
     */
    @Override
    public void onClick(int tagId, int interested) {
        System.out.println("tagid: " + tagId + ", interested: " + interested);
        mTagsDataHelper.updateInterested(tagId, interested);
//        HttpUtil.changeInterestedTags(mContext, );
    }
}
