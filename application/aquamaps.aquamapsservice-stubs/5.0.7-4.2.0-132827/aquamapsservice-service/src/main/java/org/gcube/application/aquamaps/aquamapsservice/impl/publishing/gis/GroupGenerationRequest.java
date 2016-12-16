package org.gcube.application.aquamaps.aquamapsservice.impl.publishing.gis;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupGenerationRequest implements GISRequest{

	private HashMap<String,String> geoLayersAndStyles=new HashMap<String, String>();
	private String toGenerateGroupName;	
	private ArrayList<String> publishedLayersId=new ArrayList<String>();
	public HashMap<String, String> getGeoLayersAndStyles() {
		return geoLayersAndStyles;
	}
	public String getToGenerateGroupName() {
		return toGenerateGroupName;
	}
	public ArrayList<String> getPublishedLayersId() {
		return publishedLayersId;
	}
	public GroupGenerationRequest(HashMap<String, String> geoLayersAndStyles,
			String toGenerateGroupName, ArrayList<String> publishedLayersId) {
		super();
		this.geoLayersAndStyles = geoLayersAndStyles;
		this.toGenerateGroupName = toGenerateGroupName;
		this.publishedLayersId = publishedLayersId;
	}
	
	
	
}
