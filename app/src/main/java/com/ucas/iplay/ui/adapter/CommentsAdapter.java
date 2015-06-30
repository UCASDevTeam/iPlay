package com.ucas.iplay.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ucas.iplay.R;
import com.ucas.iplay.core.model.CommentModel;
import com.ucas.iplay.util.StringUtil;

import java.util.List;

/**
 * Created by ivanchou on 7/1/15.
 */
public class CommentsAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<CommentModel> mCommentsList;

    public CommentsAdapter(Context context, List<CommentModel> commentModels) {
        mContext = context;
        this.mCommentsList = commentModels;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mCommentsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCommentsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.comments_lv_item, null);
            viewHolder = new ViewHolder();
            viewHolder.avatarIv = (ImageView) convertView.findViewById(R.id.iv_avatar);
            viewHolder.nickTv = (TextView) convertView.findViewById(R.id.tv_nickname);
            viewHolder.commentTv = (TextView) convertView.findViewById(R.id.tv_comment);
            viewHolder.timeTv = (TextView) convertView.findViewById(R.id.tv_comment_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (mCommentsList.size() > 0) {
            CommentModel comment = mCommentsList.get(position);
//            viewHolder.avatarIv.setImageBitmap();
            viewHolder.nickTv.setText(comment.user.name);
            viewHolder.commentTv.setText(comment.content);
            viewHolder.timeTv.setText(StringUtil.parseLongTimeToString(comment.createAt));
        }

        return convertView;
    }

    class ViewHolder {
        public ImageView avatarIv;
        public TextView nickTv;
        public TextView commentTv;
        public TextView timeTv;
    }
}
