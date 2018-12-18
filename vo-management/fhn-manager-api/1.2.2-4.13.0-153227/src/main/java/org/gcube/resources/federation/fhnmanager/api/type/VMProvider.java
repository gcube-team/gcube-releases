package org.gcube.resources.federation.fhnmanager.api.type;

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class VMProvider extends FHNResource {

	private String name;

	private String endpoint;

	private VMProviderCredentials credentials;

	private Set<ResourceReference<ResourceTemplate>> resourceTemplates;

	private Set<ResourceReference<NodeTemplate>> nodeTemplates;

	public VMProvider() {

	}

	@Override
	public String toString() {
		return this.name + "(" + this.getId() + ")";
	}

	/*
	 * public VMProviderCredentials getCredentials() { return credentials; }
	 * 
	 * public void setCredentials(VMProviderCredentials credentials) {
	 * this.credentials = credentials; }
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	@XmlElement(name = "resourceTemplates")
	@XmlElementWrapper(name = "resourceTemplates")
	public Set<ResourceReference<ResourceTemplate>> getResourceTemplates() {
		return resourceTemplates;
	}

	public void setResourceTemplates(Set<ResourceReference<ResourceTemplate>> resourceTemplates) {
		this.resourceTemplates = resourceTemplates;
	}

	@XmlElement(name = "nodeTemplate")
	@XmlElementWrapper(name = "nodeTemplates")
	public Set<ResourceReference<NodeTemplate>> getNodeTemplates() {
		return nodeTemplates;
	}

	public void setNodeTemplates(
			Set<ResourceReference<NodeTemplate>> nodeTemplates) {
		this.nodeTemplates = nodeTemplates;
	}

	public VMProviderCredentials getCredentials() {
		return credentials;
	}

	public void setCredentials(VMProviderCredentials credentials) {
		this.credentials = credentials;
	}

}
