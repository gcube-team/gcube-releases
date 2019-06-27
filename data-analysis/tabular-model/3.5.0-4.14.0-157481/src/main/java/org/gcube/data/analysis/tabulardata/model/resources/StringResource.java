package org.gcube.data.analysis.tabulardata.model.resources;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class StringResource extends Resource{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1805503093349235998L;
	
	private String value;

	public StringResource(){}
	
	public StringResource(String value){
		this.value = value;
	}
		

	@Override
	public String getStringValue() {
		return value;
	}

	@Override
	public Class<? extends Resource> getResourceType() {
		return this.getClass();
	}

	@Override
	public String toString() {
		return "StringResource [value=" + value + "]";
	}
		
}
