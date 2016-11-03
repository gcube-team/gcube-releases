package org.gcube.dataanalysis.ecoengine.utils;

import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.graphtools.utils.HttpRequest;
import org.gcube.dataanalysis.ecoengine.connectors.livemonitor.ResourceLoad;
import org.gcube.dataanalysis.ecoengine.connectors.livemonitor.Resources;
import org.gcube.dataanalysis.ecoengine.connectors.livemonitor.SingleResource;

import com.google.gson.Gson;

public class ResourceFactory {

	public static String getResources(float... values) {
		Resources res = new Resources();
		for (int i = 0; i < values.length; i++)
			res.addResource("Thread_" + (i + 1), values[i]);

		return HttpRequest.toJSon(res.list).replace("resId", "resID");
	}

	public static String getLoad(int processedRecords){
		long tk = System.currentTimeMillis();
		double activity = processedRecords;
		ResourceLoad rs = new ResourceLoad(tk, activity);
		return rs.toString();
	}
	
	long lastTime;
	int lastProcessedRecordsNumber;
	public ResourceFactory(){
		lastTime = System.currentTimeMillis();
		lastProcessedRecordsNumber = 0;
	}
	
	
	public String getResourceLoad(int processedRecords){ 
		long tk = System.currentTimeMillis();
		long timediff = tk - lastTime; 
		if ( timediff == 0)
			timediff = 1;
		
		double activity = Double.valueOf(processedRecords - lastProcessedRecordsNumber) * 1000.00 / Double.valueOf(timediff);
		lastTime = tk;
		lastProcessedRecordsNumber = processedRecords;
		ResourceLoad rs = new ResourceLoad(tk, activity);
		return rs.toString();
	}
	
	public static String getOverallResourceLoad(List<String> resourceLoadJson){
		long time = -1;
		double val = 0d;
		for (String json:resourceLoadJson){
			String[] arrays = json.replace("[", "").replace("]", "").split(",");
			Long timestamp = Long.parseLong(arrays[0]);
			Double value = Double.parseDouble(arrays[1]) ;
			if (time<0) time = timestamp;
			val += value;
		} 
		
		ResourceLoad rs = new ResourceLoad(time, val);
		return rs.toString();
	}
	
	public static String getOverallResources(List<String> resourcesJson){
		List<SingleResource> generalList = new ArrayList<SingleResource>();
		int size = resourcesJson.size();
		if (size==0) return "[]";
		if (size==1) return resourcesJson.get(0);
		
		int i=1;
		for (String json:resourcesJson){
			Gson gson = new Gson();
			Resources rr = gson.fromJson("{\"list\":"+json.replace("resID", "resId")+"}", Resources.class);
			List<SingleResource> l = rr.list;
			for (SingleResource sr: l){
				if (sr.value>0){
					sr.resId=sr.resId+"."+i;
					generalList.add(sr);
				}
			}
			i++;
		} 
		
		return HttpRequest.toJSon(generalList).replace("resId", "resID");
	}
	
	public static void main(String[] args){
		ArrayList<String> arrayL = new ArrayList<String>();
		arrayL.add("[1339150993573, 1203.0]");
		arrayL.add("[1339150993573, 2503.0]");
		arrayL.add("[1339150993573, 503.0]");
		String s =  getOverallResourceLoad(arrayL);
		System.out.println("S1:"+s);
		
		ArrayList<String> arrayR = new ArrayList<String>();
		arrayR.add("[" +
				"{\"resID\":\"Thread_1\",\"value\":100.0}," +
				"{\"resID\":\"Thread_2\",\"value\":100.0}," +
				"{\"resID\":\"Thread_3\",\"value\":100.0}," +
				"{\"resID\":\"Thread_4\",\"value\":100.0}" +
				"]");

		arrayR.add("[" +
				"{\"resID\":\"Thread_1\",\"value\":100.0}," +
				"{\"resID\":\"Thread_2\",\"value\":100.0}," +
				"{\"resID\":\"Thread_3\",\"value\":100.0}," +
				"{\"resID\":\"Thread_4\",\"value\":100.0}" +
				"]");

		
		String s2 =  getOverallResources(arrayR);
		System.out.println("S2:"+s2);
		
	}
	
	
}
