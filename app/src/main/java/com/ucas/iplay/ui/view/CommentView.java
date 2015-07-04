package com.ucas.iplay.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.ucas.iplay.R;

/**
 * Created by ivanchou on 7/4/15.
 */
public class CommentView extends RelativeLayout implements View.OnClickListener {
    private Context mContext;

    public CommentView(Context context) {
        this(context, null);
    }

    public CommentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }

    private EditText mCommentsEt;
    private ImageButton mEmojiIb;
    private Button mSendBtn;

    private void init() {
        inflate(mContext, R.layout.comment_view, this);
        mCommentsEt = (EditText) findViewById(R.id.et_comment);
        mEmojiIb = (ImageButton) findViewById(R.id.ib_emoji);
        mSendBtn = (Button) findViewById(R.id.btn_send);
        mEmojiIb.setOnClickListener(this);
        mSendBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_emoji:
                // emoji
                break;
            case R.id.btn_send:
                mCallBacks.onSendClick(mCommentsEt.getText().toString());
                break;
            default:
                break;
        }
    }

    private OnSendClickListener mCallBacks;

    public void setOnSendClickListener(OnSendClickListener onSendClickListener) {
        this.mCallBacks = onSendClickListener;
    }

    public interface OnSendClickListener {
        /**
         * 用户按下发送评论
         */
        void onSendClick(String text);
    }
}
