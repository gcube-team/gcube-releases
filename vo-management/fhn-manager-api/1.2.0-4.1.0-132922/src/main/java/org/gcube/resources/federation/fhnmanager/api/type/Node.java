package org.gcube.resources.federation.fhnmanager.api.type;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Node extends FHNResource {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	private String status;
	
	private String hostname;

	private NodeWorkload workload;

	private ResourceReference<VMProvider> vmProvider;

	private ResourceReference<ServiceProfile> serviceProfile;
	
	public ResourceReference<ServiceProfile> getServiceProfile() {
		return serviceProfile;
	}

	public void setServiceProfile(ResourceReference<ServiceProfile> serviceProfile) {
		this.serviceProfile = serviceProfile;
	}

	private ResourceReference<NodeTemplate> nodeTemplate;

	private ResourceReference<ResourceTemplate> resourceTemplate;

	public Node() {

	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public NodeWorkload getWorkload() {
		return workload;
	}

	public void setWorkload(NodeWorkload workload) {
		this.workload = workload;
	}

	public ResourceReference<VMProvider> getVmProvider() {
		return vmProvider;
	}

	public void setVmProvider(ResourceReference<VMProvider> vmProvider) {
		this.vmProvider = vmProvider;
	}

	public ResourceReference<NodeTemplate> getNodeTemplate() {
		return nodeTemplate;
	}

	public void setNodeTemplate(ResourceReference<NodeTemplate> nodeTemplate) {
		this.nodeTemplate = nodeTemplate;
	}

	public ResourceReference<ResourceTemplate> getResourceTemplate() {
		return resourceTemplate;
	}

	public void setResourceTemplate(ResourceReference<ResourceTemplate> resourceTemplate) {
		this.resourceTemplate = resourceTemplate;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	
	
	
	
	
}
