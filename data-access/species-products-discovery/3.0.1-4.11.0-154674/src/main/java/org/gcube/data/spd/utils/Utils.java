package org.gcube.data.spd.utils;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.Coordinate;
import org.gcube.data.spd.model.PluginDescription;
import org.gcube.data.spd.model.PropertySupport;
import org.gcube.data.spd.model.util.Capabilities;
import org.gcube.data.spd.plugin.fwk.AbstractPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Utils {

	private static final Logger logger = LoggerFactory.getLogger(Utils.class);
	
	public static PluginDescription getPluginDescription(AbstractPlugin plugin){
		PluginDescription description = new PluginDescription(plugin.getRepositoryName(), plugin.getDescription(), plugin.getRepositoryInfo());
		description.setRemote(plugin.isRemote());
		
		Map<Capabilities, List<Conditions>> capabilityMap = new HashMap<Capabilities, List<Conditions>>();
		
		
		for (Capabilities capability : plugin.getSupportedCapabilities()){
			if (capability.isPropertySupport())
				try{
					Set<Conditions> props = ((PropertySupport) plugin.getClass().getDeclaredMethod(capability.getMethod()).invoke(plugin)).getSupportedProperties();		
					capabilityMap.put(capability, new ArrayList<Conditions>(props));
				}catch (Exception e) {
					logger.warn("cannot retreive properties for capability "+capability,e);
				}
			else{
				List<Conditions> emptyConditions = Collections.emptyList();
				capabilityMap.put(capability, emptyConditions);
			}
		}
		description.setSupportedCapabilities(capabilityMap);
		return description;
	}
	
	
	public static String getPropsAsString(Condition[] conditions){
		StringBuilder props =new StringBuilder(); 
		Arrays.sort(conditions);
		for (Condition cond: conditions){
			switch (cond.getType()) {
			case COORDINATE:
					Coordinate coord = (Coordinate)cond.getValue();
					props.append("lat="+coord.getLatitude());
					props.append("long="+coord.getLongitude());
					props.append("op="+cond.getOp().name());
				break;
			case DATE:
				Calendar cal = (Calendar)cond.getValue();
				props.append("date="+cal.getTimeInMillis());
				props.append("op="+cond.getOp().name());
				break;	
			default:
				break;
			}
		}
		return props.toString();
	}
	
	public static File createErrorFile(Iterator<String> errors) throws Exception{
		int entries =0;
		File file = File.createTempFile("errors", "txt");
		FileWriter writer= new FileWriter(file);
		while(errors.hasNext()){
			writer.write(errors.next()+"\n");
			entries++;
		}
		writer.close();
		if (entries==0){
			file.delete();
			return null;
		}else return file;
	}
}
