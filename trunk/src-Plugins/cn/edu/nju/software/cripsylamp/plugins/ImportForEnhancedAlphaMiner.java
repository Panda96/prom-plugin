package cn.edu.nju.software.cripsylamp.plugins;

import org.deckfour.xes.model.XLog;
import org.omg.CORBA.portable.InputStream;
import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Plugin(
        name = "Import Txt for Enhanced Alpha Miner Plugin",
        parameterLabels = {"Map File"},
        returnLabels = {"MapFile"},
        returnTypes = { Map.class })
@UIImportPlugin(description = "Generate map",extensions = "map")
public class ImportForEnhancedAlphaMiner extends AbstractImportPlugin{
    @Override
    protected Object importFromStream(PluginContext context, java.io.InputStream input, String filename, long fileSizeInBytes) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        Map<String,String> result = new HashMap<>();
        String line = "",caseID = "",trace = "";
        try {
            while((line = reader.readLine())!=null) {
                String[] splitStr = line.split("\t");
                if(splitStr.length==3) {
                    caseID = splitStr[1];
                    trace = splitStr[2];
                    result.put(caseID,trace);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println(result.keySet());
        return result;
    }
}