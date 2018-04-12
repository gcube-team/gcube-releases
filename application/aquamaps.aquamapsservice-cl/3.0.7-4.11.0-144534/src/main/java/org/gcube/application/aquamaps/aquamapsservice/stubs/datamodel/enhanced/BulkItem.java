package org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced;

import java.util.HashMap;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("BulkItem")
public class BulkItem {

	@XStreamAsAttribute
	private String speciesId;
	private HashMap<Integer,ItemResources> resources=new HashMap<Integer, ItemResources>();
	
	public BulkItem() {
		// TODO Auto-generated constructor stub
	}

	public BulkItem(String speciesId,
			HashMap<Integer, ItemResources> resources) {
		super();
		this.speciesId = speciesId;
		this.resources = resources;
	}

	public String getSpeciesId() {
		return speciesId;
	}

	public void setSpeciesId(String speciesId) {
		this.speciesId = speciesId;
	}

	public HashMap<Integer, ItemResources> getResources() {
		return resources;
	}

	public void setResources(HashMap<Integer, ItemResources> resources) {
		this.resources = resources;
	}
	
	public void addItemResource(Integer hspecId,ItemResources toAdd){
		if(resources==null) resources=new HashMap<Integer, ItemResources>();
		resources.put(hspecId, toAdd);
	}

	@Override
	public String toString() {
		return "BulkItem [speciesId=" + speciesId + ", imgUrisByHSPEC="
				+ resources + "]";
	}
	
	

	public boolean hasResources(){
		if(resources==null||resources.isEmpty()) return false;
		for(ItemResources res:resources.values()) if(res.hasResources()) return true;
		return false;
	}
	
	
}
