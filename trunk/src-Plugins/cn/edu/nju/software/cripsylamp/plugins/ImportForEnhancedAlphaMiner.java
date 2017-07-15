package cn.edu.nju.software.cripsylamp.plugins;

import cn.edu.nju.software.cripsylamp.beans.Trace;
import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Plugin(
        name = "Import TraceFile for Enhanced Alpha Miner Plugin",
        parameterLabels = {"Filename"},
        returnLabels = {"Trace"},
        returnTypes = {Trace.class})
@UIImportPlugin(
        description = "GenerateTrace",
        extensions = "tra")

public class ImportForEnhancedAlphaMiner extends AbstractImportPlugin {
    @Override
    protected Trace importFromStream(PluginContext context, java.io.InputStream input, String filename, long fileSizeInBytes) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        Map<String, String> result = new HashMap<>();
        String line, caseID, trace;
        try {
            context.getFutureResult(0).setLabel("Traces imported from: " + filename);
            while ((line = reader.readLine()) != null) {
                String[] splitStr = line.split("\t");
                if (splitStr.length == 3) {
                    caseID = splitStr[1];
                    trace = splitStr[2];
                    result.put(caseID, trace.replace(" ", ""));
                }
            }
            System.out.println(result.keySet());

            return new Trace(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}