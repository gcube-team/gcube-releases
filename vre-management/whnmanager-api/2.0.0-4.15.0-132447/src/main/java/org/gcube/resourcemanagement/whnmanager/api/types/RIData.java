package org.gcube.resourcemanagement.whnmanager.api.types;

import javax.xml.bind.annotation.XmlRootElement;

//@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class RIData{
//	@XmlElement
	public String name;
	
//	@XmlElement
	public String clazz;

	protected RIData(){}
	
	public RIData(String name, String clazz){
		super();
		this.name=name;
		this.clazz=clazz;
	}
	
	
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

