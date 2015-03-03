package me.snov.elasticsearch.demo.model;

import org.elasticsearch.common.joda.time.LocalTime;

public class Event {

    public static final String START = "start";
    public static final String STOP = "stop";

    private final LocalTime start;
    private final LocalTime stop;

    public Event(LocalTime start, LocalTime stop) {
        this.start = start;
        this.stop = stop;
    }

    public boolean isInProgress(LocalTime time) {
        return (time.isEqual(start) || time.isAfter(start))
            && (time.isBefore(stop) || time.isEqual(stop));
    }
}
