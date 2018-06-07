package org.gcube.common.authorization.library;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.gcube.common.authorization.library.utils.MapAdapter;



@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ExternalServiceList {

	/**
	 * a map with qualifier as key and token as value 
	 */
	@XmlJavaTypeAdapter(MapAdapter.class)
	Map<String, String> externalServiceMap= new HashMap<String, String>();

	
	@SuppressWarnings("unused")
	private ExternalServiceList(){}
	
	public ExternalServiceList(Map<String, String> externalServiceMap) {
		this.externalServiceMap = externalServiceMap;
	}

	public Map<String, String> getExternalServiceMap() {
		return externalServiceMap;
	}

	@Override
	public String toString() {
		return "ExternalServiceList [externalServiceMap=" + externalServiceMap
				+ "]";
	}
	
}