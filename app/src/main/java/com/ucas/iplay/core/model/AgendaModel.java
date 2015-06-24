package com.ucas.iplay.core.model;

import java.util.ArrayList;

/**
 * Created by wanggang on 2015/6/21.
 */
public class AgendaModel {
    public String startAt;
    public ArrayList<EventModel> events;

    public AgendaModel() {
        startAt = null;
        events = null;
    }

    public AgendaModel(EventModel event) {
        startAt = event.startAt;
        events = new ArrayList<EventModel>();
        events.add(event);
    }

    public void addEvent(EventModel event) {
        events.add(event);
    }

    @Override
    public String toString() {
        return "AgendaModel: 开始时间：" + startAt + "，内容：" + events.get(0).title + "\n";
    }
}
