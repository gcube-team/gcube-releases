package org.gcube.data.publishing.gCatFeeder.collectors.dm.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.data.publishing.gCatFeeder.collectors.dm.model.ckan.GCatModel;
import org.gcube.data.publishing.gCatfeeder.collectors.model.CustomData;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InternalAlgorithmDescriptor implements CustomData {

	private String className;
	
	//operator info
	private String name;
	private String description;
	private String briefDescription;
	private String id;

	private UserIdentity author;
	private UserIdentity maintainer;
	
	//category info
	
	private String categoryBriefDescription;
	private String categoryDescription;
	private String categoryID;
	private String categoryName;
	
	
	private Set<Parameter> inputParameters=new HashSet<>();
	private Set<Parameter> outputParameters=new HashSet<>();
	
	private String guiLink;
	private String wpsLink;
	
	private Boolean privateFlag;
	
	
	private List<String> tags=new ArrayList<>();
	
	public GCatModel asCKANModel() {
		return new GCatModel(this);
	}
	
	
	
}
