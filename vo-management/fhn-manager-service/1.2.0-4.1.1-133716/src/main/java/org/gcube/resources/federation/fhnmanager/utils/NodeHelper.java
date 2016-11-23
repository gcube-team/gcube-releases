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
	//id = Fedcloud vmid + FedCloud Provider Id
	public static String createNodeId(String vmProviderId, String vmId){
		return escapeToken(vmId) + SEPARATOR + escapeToken(vmProviderId);
	}
	
	public static String getVMProviderId(String nodeId){
		return unEscapeToken(nodeId.substring(nodeId.indexOf(SEPARATOR)+1));
	}
	
	public static String getVMId(String nodeId){
		return unEscapeToken(nodeId.substring(0, nodeId.indexOf(SEPARATOR)));
	}
	
	
	public static String escapeToken(String vmId){
		vmId = vmId.replaceAll("\\/", "\\$");
		return vmId;
	}
	
	public static String unEscapeToken(String nodeIdToken){
		nodeIdToken = nodeIdToken.replaceAll("\\$", "\\/");
		return nodeIdToken;
	}
		
	public static Node createNode(
			VM vm, 
			VMProvider vmp, 
			ServiceProfile sp,
			NodeTemplate nt,
			ResourceTemplate rt){
			Node node = new Node();
		
		
		//works fine with local version
		
		//		node.setId(createNodeId(vmp.getId(), vm.getEndpoint().toString()));
		//		node.setVmProvider(new ResourceReference<VMProvider>(vmp.getId()));
		//		node.setServiceProfile(new ResourceReference<ServiceProfile>(sp.getId()));
		//		node.setNodeTemplate(new ResourceReference<NodeTemplate>(nt.getId()));
		//		node.setResourceTemplate(new ResourceReference<ResourceTemplate>(rt.getId()));
		//		node.setStatus(vm.getStatus());
		//		node.setHostname(vm.getHostname());

		node.setId(createNodeId(vmp.getId(), vm.getEndpoint().toString()));
		node.setVmProvider(new ResourceReference<VMProvider>(vmp.getId()));
		node.setServiceProfile(new ResourceReference<ServiceProfile>(sp.getId()));
		node.setNodeTemplate(new ResourceReference<NodeTemplate>(nt.getId()));
		node.setResourceTemplate(new ResourceReference<ResourceTemplate>(rt.getId()));
		node.setStatus(vm.getStatus());
		node.setHostname(vm.getHostname());
		
		return node;
	}
	
	public static void main(String[] args){
		String vmId = "https:$$carach5.ics.muni.cz:11443$compute$74088";
		String providerId="58d494a2-505d-4550-8d48-83ade4c2b49e";
		
		String nodeId = createNodeId(providerId, vmId);
		System.out.println(nodeId);
		System.out.println(getVMProviderId(nodeId));
		System.out.println(getVMId(nodeId));
		
		
	}
}
