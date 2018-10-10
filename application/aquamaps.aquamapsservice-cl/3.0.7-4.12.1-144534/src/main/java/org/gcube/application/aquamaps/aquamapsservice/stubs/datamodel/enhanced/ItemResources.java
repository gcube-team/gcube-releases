package org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced;

import java.util.ArrayList;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ItemResources")
public class ItemResources{
	
	private String standardImgUri=null;
	private ArrayList<String> customImgUris=new ArrayList<String>();
	private String standardLayerId=null;
	private ArrayList<String> customLayerIds=new ArrayList<String>();
	public ItemResources() {
		// TODO Auto-generated constructor stub
	}
	
	public ItemResources(String standardImgUri,
			ArrayList<String> customImgUris, String standardLayerId,
			ArrayList<String> customLayerIds) {
		super();
		this.standardImgUri = standardImgUri;
		this.customImgUris = customImgUris;
		this.standardLayerId = standardLayerId;
		this.customLayerIds = customLayerIds;
	}

	public String getStandardImgUri() {
		return standardImgUri;
	}
	public void setStandardImgUri(String standardImgUri) {
		this.standardImgUri = standardImgUri;
	}
	public ArrayList<String> getCustomImgUris() {
		return customImgUris;
	}
	public void setCustomImgUris(ArrayList<String> customImgUris) {
		this.customImgUris = customImgUris;
	}
	public String getStandardLayerId() {
		return standardLayerId;
	}
	public void setStandardLayerId(String standardLayerId) {
		this.standardLayerId = standardLayerId;
	}
	public ArrayList<String> getCustomLayerIds() {
		return customLayerIds;
	}
	public void setCustomLayerIds(ArrayList<String> customLayerIds) {
		this.customLayerIds = customLayerIds;
	}

	@Override
	public String toString() {
		return "ItemResources [standardImgUri=" + standardImgUri
				+ ", customImgUris=" + customImgUris + ", standardLayerId="
				+ standardLayerId + ", customLayerIds=" + customLayerIds
				+ "]";
	}
	
	public void addResource(boolean isGIS,boolean isCustom,String resource){
		if(isCustom)
			if(isGIS) { // Custom Layer
				if(customLayerIds==null) customLayerIds=new ArrayList<String>();
				customLayerIds.add(resource);
			}else{		// Custom Image
				if(customImgUris==null) customImgUris=new ArrayList<String>();
				customImgUris.add(resource);					
			}
		else 
			if (isGIS) standardLayerId=resource;
			else standardImgUri=resource; 
	}
	
	public boolean hasResources(){
		return ((standardImgUri!=null)||
				(standardLayerId!=null)||
				(customLayerIds!=null&&!customLayerIds.isEmpty())||
				(customImgUris!=null&&!customImgUris.isEmpty()));
	}
}