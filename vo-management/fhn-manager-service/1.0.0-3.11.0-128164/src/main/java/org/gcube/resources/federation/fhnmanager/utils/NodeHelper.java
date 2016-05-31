package org.gcube.resources.federation.fhnmanager.utils;

import org.gcube.resources.federation.fhnmanager.api.type.Node;
import org.gcube.resources.federation.fhnmanager.api.type.NodeTemplate;
import org.gcube.resources.federation.fhnmanager.api.type.ResourceReference;
import org.gcube.resources.federation.fhnmanager.api.type.ResourceTemplate;
import org.gcube.resources.federation.fhnmanager.api.type.ServiceProfile;
import org.gcube.resources.federation.fhnmanager.api.type.VMProvider;
import org.gcube.vomanagement.occi.datamodel.cloud.VM;

public class NodeHelper {

	private static final String SEPARATOR = "@";
	
	public static String createNodeId(String vmProviderId, String vmId){
		return vmProviderId + SEPARATOR + vmId;
	}
	
	public static String getVMProviderId(String nodeId){
		return nodeId.substring(0, nodeId.indexOf(SEPARATOR));
	}
	
	public static String getVMId(String nodeId){
		return nodeId.substring(nodeId.indexOf(SEPARATOR)+1);
	}
		
	public static Node createNode(
			VM vm, 
			VMProvider vmp, 
			ServiceProfile sp,
			NodeTemplate nt,
			ResourceTemplate rt){
		Node node = new Node();
		
		node.setId(createNodeId(vmp.getId(), vm.getEndpoint().toString()));
		node.setVmProvider(new ResourceReference<VMProvider>(vmp.getId()));
		node.setServiceProfile(new ResourceReference<ServiceProfile>(sp.getId()));
		node.setNodeTemplate(new ResourceReference<NodeTemplate>(nt.getId()));
		node.setResourceTemplate(new ResourceReference<ResourceTemplate>(rt.getId()));
		node.setStatus(vm.getStatus());
		node.setHostname(vm.getHostname());

		return node;
	}
}
