package org.gcube.resourcemanagement.whnmanager.api.types;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

@SuppressWarnings("restriction")
@XmlRootElement
public class AddScopeInputParams{

	@XmlElement
	public String scope;
	@XmlElement
	public String map;
	
	protected AddScopeInputParams(){}
	
	public AddScopeInputParams(String scope, String map){
		super();
		this.scope=scope;
		this.map=map;
	}
	
	public String getScope() {
		return scope;
	}

	public String getMap() {
		return map;
	}

}
