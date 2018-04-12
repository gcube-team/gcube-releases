package org.gcube.common.scope.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.gcube.common.scope.api.ServiceMap;

/**
 * A {@link ServiceMap} with a standard XML binding.
 * 
 * @author Fabio Simeoni
 *
 */
@XmlRootElement(name="service-map")
public class DefaultServiceMap implements ServiceMap {

	@XmlAttribute
	private String scope;
	
	@XmlAttribute
	private String version;
	
	@XmlJavaTypeAdapter(ServiceMapAdapter.class)
	Map<String,String> services = new LinkedHashMap<String,String>();
	
	@Override
	public String scope() {
		return scope;	
	}
	
	@Override
	public String version() {
		return version;	
	}
	
	@Override
	public String endpoint(String service) {
		
		String endpoint = services.get(service);
		
		if (endpoint==null)
			throw new IllegalArgumentException("unknown service "+service);
		
		return endpoint;
	}

	
}
