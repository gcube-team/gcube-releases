package org.gcube.resources.federation.fhnmanager.api.type;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class NodeDefinition extends FHNResource {
	
	
	
	
	
	
	
	
	//private String node_def;
	
	private String vmproviderId;
	
	private String nodetemplateId;
	
	private String resourceTemplateId;
	
	OccopusScalingParams scaling;

	public NodeDefinition() {

	}






	public String getVmproviderId() {
		return vmproviderId;
	}



	public void setVmproviderId(String vmproviderId) {
		this.vmproviderId = vmproviderId;
	}



	public String getNodetemplateId() {
		return nodetemplateId;
	}



	public void setNodetemplateId(String nodetemplateId) {
		this.nodetemplateId = nodetemplateId;
	}



	public String getResourceTemplateId() {
		return resourceTemplateId;
	}



	public void setResourceTemplateId(String resourceTemplateId) {
		this.resourceTemplateId = resourceTemplateId;
	}



	public OccopusScalingParams getScaling() {
		return scaling;
	}



	public void setScaling(OccopusScalingParams scaling) {
		this.scaling = scaling;
	}
	
}
