package com.ucas.iplay;

import android.content.Intent;
import android.test.InstrumentationTestCase;

import com.ucas.iplay.ui.CommentsActivity;

/**
 * Created by ivanchou on 7/4/15.
 */
public class CommentActivityTest extends InstrumentationTestCase {

    private CommentsActivity commentsActivity;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent();
        intent.setClassName("com.ucas.iplay.CommentsActivity", CommentsActivity.class.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        commentsActivity = (CommentsActivity) getInstrumentation().startActivitySync(intent);

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        commentsActivity.finish();
    }
}
