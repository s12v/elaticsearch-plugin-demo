package me.snov.elasticsearch.demo.service;

import me.snov.elasticsearch.demo.model.Event;
import org.elasticsearch.common.joda.time.LocalTime;
import org.elasticsearch.index.fielddata.ScriptDocValues;
import org.elasticsearch.search.lookup.DocLookup;
import org.elasticsearch.search.lookup.SourceLookup;

public class EventParser {

    public Event getEvent(SourceLookup lookup) {
        return new Event(
            new LocalTime(lookup.get(Event.START)),
            new LocalTime(lookup.get(Event.STOP))
        );
    }

    public Event getEvent(DocLookup lookup) {
        return new Event(
            new LocalTime(((ScriptDocValues.Strings) lookup.get(Event.START)).getValue()),
            new LocalTime(((ScriptDocValues.Strings) lookup.get(Event.STOP)).getValue())
        );
    }
}
