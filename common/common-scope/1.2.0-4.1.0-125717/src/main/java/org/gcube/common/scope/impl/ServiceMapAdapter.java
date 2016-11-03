package org.gcube.common.scope.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * Adapts the JAXB-binding for {@link DefaultServiceMap}.
 * 
 * @author Fabio Simeoni
 *
 */
public class ServiceMapAdapter extends XmlAdapter<ServiceMapAdapter.ValueServiceMap,Map<String,String>> {

	@XmlRootElement(name="services")
	static class ValueServiceMap {
		
		@XmlElement(name="service")
		Set<ServiceEntry> services;
		
	}
	
	static class ServiceEntry {
		
		@XmlAttribute
		private String name;
		
		@XmlAttribute
		private String endpoint;
	}

	@Override
	public Map<String,String> unmarshal(ValueServiceMap valueMap) throws Exception {
		Map<String, String> map = new LinkedHashMap<String,String>();
		for (ServiceEntry service : valueMap.services)
			map.put(service.name,service.endpoint);
		return map;
	}

	@Override
	public ValueServiceMap marshal(Map<String, String> map) throws Exception {
		ValueServiceMap valueMap = new ValueServiceMap();
		for (Map.Entry<String,String> e : map.entrySet()) {
			ServiceEntry entry = new ServiceEntry();
			entry.name=e.getKey();
			entry.endpoint = e.getValue();
		}
		return valueMap;
	}
}
