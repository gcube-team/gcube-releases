package org.gcube.data.spd.utils;

import java.util.HashMap;

public class DynamicMap {

	private HashMap<String, DynamicList> map;
	
	private static DynamicMap singleton= new DynamicMap();
			
	public static DynamicList get(String jobId){
		return singleton.map.get(jobId);
	}
	
	public static DynamicList put(String jobId){
		DynamicList dynamicList = new DynamicList();
		singleton.map.put(jobId, dynamicList);
		return dynamicList;
	}
	
	public static void remove(String jobId){
		DynamicList dynamicList = singleton.map.get(jobId);
		if (dynamicList!= null){
			dynamicList.close();
			singleton.map.remove(jobId);
		}
		
	}
	
	
	private  DynamicMap() {
		map = new HashMap<String, DynamicList>();
	}
	
	
	
}
