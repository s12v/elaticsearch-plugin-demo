package me.snov.elasticsearch.demo.script;

import me.snov.elasticsearch.demo.model.Event;
import me.snov.elasticsearch.demo.service.EventParser;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.joda.time.LocalTime;
import org.elasticsearch.script.AbstractSearchScript;
import org.elasticsearch.script.ExecutableScript;
import org.elasticsearch.script.NativeScriptFactory;
import org.elasticsearch.script.ScriptException;

import java.util.Map;

public class EventInProgressScript extends AbstractSearchScript {

    public final static String SCRIPT_NAME = "in_progress";
    public final static String PARAM_TIME = "time";
    public final static String PARAM_USE_DOC = "use_doc";

    private final LocalTime time;
    private final EventParser parser;
    private final boolean useDoc;

    public static class Factory implements NativeScriptFactory {
        @Override
        public ExecutableScript newScript(@Nullable Map<String, Object> params) {
            LocalTime time = params.containsKey(PARAM_TIME)
                ? new LocalTime(params.get(PARAM_TIME))
                : null;
            Boolean useDoc = params.containsKey(PARAM_USE_DOC)
                ? (Boolean) params.get(PARAM_USE_DOC)
                : null;

            if (time == null || useDoc == null) {
                throw new ScriptException("Parameters \"time\" and \"use_doc\" are required");
            }

            return new EventInProgressScript(time, useDoc);
        }
    }

    private EventInProgressScript(LocalTime time, Boolean useDoc) {
        this.time = time;
        this.parser = new EventParser();
        this.useDoc = useDoc;
    }

    @Override
    public Integer run() {
        Event event = useDoc
            ? parser.getEvent(doc())
            : parser.getEvent(source());


        return event.isInProgress(time)
            ? 1
            : 0;
    }
}
