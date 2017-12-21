package org.gcube.resourcemanagement.whnmanager.api.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class ScopeRIParams{
//	@XmlElement
	public String scope;
	
//	@XmlElement
	public String name;
	
//	@XmlElement
	public String clazz;
	
	protected ScopeRIParams(){}
	
	public ScopeRIParams(String scope, String name, String clazz){
		super();
		this.scope=scope;
		this.name=name;
		this.clazz=clazz;
	}

	public String getScope() {
		return scope;
	}

//	public void setScope(String scope) {
//		this.scope = scope;
//	}

	public String getName() {
		return name;
	}

//	public void setName(String name) {
//		this.name = name;
//	}

	public String getClazz() {
		return clazz;
	}

//	public void setClazz(String clazz) {
//		this.clazz = clazz;
//	}
	
}
