package org.gcube.search.sru.search.adapter.commons.resources;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.rest.commons.resourceawareservice.resources.StatefulResource;
import org.gcube.rest.commons.resourceawareservice.resources.exceptions.StatefulResourceException;


@XmlRootElement
public class SruSearchAdapterResource extends StatefulResource {

	private static final long serialVersionUID = 1L;
	
	private String searchSystemEndpoint;
	
	private String hostname;
	
	private Integer port;
	
	private String scope; // same as searchSystemScope

	
	
	@XmlElement
	public String getSearchSystemEndpoint() {
		return searchSystemEndpoint;
	}

	public void setSearchSystemEndpoint(String searchSystemEndpoint) {
		this.searchSystemEndpoint = searchSystemEndpoint;
	}

	@XmlElement
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	@Override
	public void onLoad() throws StatefulResourceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClose() throws StatefulResourceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDestroy() throws StatefulResourceException {
		// TODO Auto-generated method stub
		
	}

	@XmlElement
	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	@XmlElement
	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	
	
}
