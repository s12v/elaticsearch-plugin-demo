package me.snov.elasticsearch.demo.plugin;

import me.snov.elasticsearch.demo.script.EventInProgressScript;
import org.elasticsearch.plugins.AbstractPlugin;
import org.elasticsearch.script.ScriptModule;

public class ExamplePlugin extends AbstractPlugin {

    @Override
    public String name() {
        return "plugin-example";
    }

    @Override
    public String description() {
        return "Plugin example";
    }

    public void onModule(ScriptModule module) {
        module.registerScript(EventInProgressScript.SCRIPT_NAME, EventInProgressScript.Factory.class);
    }
}
