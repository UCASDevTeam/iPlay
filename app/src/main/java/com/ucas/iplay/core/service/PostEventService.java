package com.ucas.iplay.core.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.ucas.iplay.core.model.EventModel;
import com.ucas.iplay.core.tasker.EventPostTask;
import com.ucas.iplay.util.SPUtil;

/**
 * Created by ivanchou on 4/18/15.
 */
public class PostEventService extends Service {


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        EventModel event = (EventModel) bundle.get("event");


        new EventPostTask(this, event.content, event.originalPic).execute(event);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
