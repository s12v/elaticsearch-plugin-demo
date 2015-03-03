package me.snov.elasticsearch.demo.script;

import com.carrotsearch.randomizedtesting.annotations.Name;
import com.carrotsearch.randomizedtesting.annotations.ParametersFactory;

import java.util.Arrays;
import java.util.HashMap;

public class EventInProgressScriptTests extends AbstractScriptTest {

    protected String time;
    protected boolean useDoc;
    protected Integer expected;

    public EventInProgressScriptTests(
        @Name("time") String time,
        @Name("useDoc") boolean useDoc,
        @Name("expected") Integer expected
    ) {
        super(EventInProgressScript.SCRIPT_NAME);
        this.time = time;
        this.useDoc = useDoc;
        this.expected = expected;
    }

    @ParametersFactory
    public static Iterable<Object[]> parameters() {
        return Arrays.asList(
            $$(
                $("00:00:00", true, 0),
                $("09:00:00", true, 1),
                $("12:34:56", true, 1),
                $("18:30:01", true, 0),
                $("23:00:00", true, 0)
            )
        );
    }

    private HashMap<String, Object> getScriptParameters() {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(EventInProgressScript.PARAM_TIME, time);
        parameters.put(EventInProgressScript.PARAM_USE_DOC, useDoc);

        return parameters;
    }

    public void testIsEventInProgress() throws Exception {
        String documentId = "1";
        mockDocumentFromResource(documentId, "data.json", "mapping.json");
        assertEquals(
            String.format("%s at %s", scriptName, time),
            expected,
            getObjectFieldFromScript(documentId, getScriptParameters())
        );
    }
}
