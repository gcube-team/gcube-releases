package org.gcube.dataanalysis.ecoengine.connectors.livemonitor;

import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.graphtools.utils.HttpRequest;

public class Resources {
	
	public List<SingleResource> list;
	
	public Resources(){
		list = new ArrayList<SingleResource>();
	}
	
	public void addResource(String resID, double value){
		
		list.add(new SingleResource(resID, value));
		
	}
	
	
	public static String buildLocalResourcesLog(int nres){
		
		Resources res = new Resources();
		try {
			for (int i = 0; i < nres; i++) {
				try {
					double value = 100.00;
					res.addResource("Thread_" + (i + 1), value);
				} catch (Exception e1) {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if ((res != null) && (res.list != null))
			return HttpRequest.toJSon(res.list).replace("resId", "resID");
		else
			return "";
	}
}
