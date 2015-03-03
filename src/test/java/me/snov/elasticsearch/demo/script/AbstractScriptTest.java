package me.snov.elasticsearch.demo.script;

import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.plugins.PluginsService;
import org.elasticsearch.script.ScriptException;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.test.ElasticsearchIntegrationTest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.cluster.metadata.IndexMetaData.SETTING_NUMBER_OF_REPLICAS;
import static org.elasticsearch.cluster.metadata.IndexMetaData.SETTING_NUMBER_OF_SHARDS;
import static org.elasticsearch.test.hamcrest.ElasticsearchAssertions.assertAcked;
import static org.elasticsearch.test.hamcrest.ElasticsearchAssertions.assertHitCount;
import static org.elasticsearch.test.hamcrest.ElasticsearchAssertions.assertNoFailures;

@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE, numDataNodes = 1)
abstract public class AbstractScriptTest extends ElasticsearchIntegrationTest {

    public static final String TEST_INDEX = "test";
    public static final String TEST_TYPE = "event";

    protected final String scriptName;

    AbstractScriptTest(String scriptName) {
        this.scriptName = scriptName;
    }

    @Override
    public Settings indexSettings() {
        ImmutableSettings.Builder builder = ImmutableSettings.builder();
        builder.put(SETTING_NUMBER_OF_SHARDS, 1);
        builder.put(SETTING_NUMBER_OF_REPLICAS, 0);
        return builder.build();
    }

    @Override
    protected Settings nodeSettings(int nodeOrdinal) {
        return ImmutableSettings.settingsBuilder()
            .put("gateway.type", "none")
            .put("plugins." + PluginsService.LOAD_PLUGIN_FROM_CLASSPATH, true)
            .put(super.nodeSettings(nodeOrdinal))
            .build();
    }

    protected void mockDocumentFromResource(String id, String dataFile, String mappingFile)
        throws IOException, ExecutionException, InterruptedException {
        String data = convertStreamToString(getClass().getResourceAsStream("/" + dataFile));
        String mapping = convertStreamToString(getClass().getResourceAsStream("/" + mappingFile));

        assertAcked(prepareCreate(TEST_INDEX).addMapping(TEST_TYPE, mapping));

        List<IndexRequestBuilder> indexBuilders = new ArrayList<>();
        indexBuilders.add(
            client().prepareIndex(TEST_INDEX, TEST_TYPE, id).setSource(data)
        );

        indexRandom(true, indexBuilders);
    }

    private String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    protected Object getObjectFieldFromScript(String documentId, HashMap<String, Object> scriptParams) {
        SearchResponse searchResponse = client()
            .prepareSearch(TEST_INDEX).setTypes(TEST_TYPE)
            .addScriptField(scriptName, "native", scriptName, scriptParams)
            .execute()
            .actionGet();

        assertNoFailures(searchResponse);
        assertHitCount(searchResponse, 1);

        for (SearchHit searchHit : searchResponse.getHits()) {
            if (documentId.equals(searchHit.getId())) {
                return searchHit.fields().get(scriptName).getValue();
            }
        }

        throw new ScriptException("No document with the given id available");
    }
}
