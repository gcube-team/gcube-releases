package org.gcube.resourcemanagement.whnmanager.api.types;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AddContextInputParams{

//	@XmlElement
	public String context;
//	@XmlElement
	public String map;
	
	protected AddContextInputParams(){}
	
	public AddContextInputParams(String scope, String map){
		super();
		this.context=scope;
		this.map=map;
	}
	
	public String getContext() {
		return context;
	}

	public String getMap() {
		return map;
	}

}
