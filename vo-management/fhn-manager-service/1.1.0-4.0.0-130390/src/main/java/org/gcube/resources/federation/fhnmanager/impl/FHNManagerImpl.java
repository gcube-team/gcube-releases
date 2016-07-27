package org.gcube.resources.federation.fhnmanager.impl;


import java.net.URI;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.gcube.resources.federation.fhnmanager.api.FHNManager;
import org.gcube.resources.federation.fhnmanager.api.exception.ConnectorException;
import org.gcube.resources.federation.fhnmanager.api.exception.FHNManagerException;
import org.gcube.resources.federation.fhnmanager.api.type.Node;
import org.gcube.resources.federation.fhnmanager.api.type.NodeTemplate;
import org.gcube.resources.federation.fhnmanager.api.type.ResourceReference;
import org.gcube.resources.federation.fhnmanager.api.type.ResourceTemplate;
import org.gcube.resources.federation.fhnmanager.api.type.ServiceProfile;
import org.gcube.resources.federation.fhnmanager.api.type.VMProvider;
import org.gcube.resources.federation.fhnmanager.is.ISProxyImpl;
import org.gcube.resources.federation.fhnmanager.is.ISProxyInterface;
import org.gcube.resources.federation.fhnmanager.is.ISProxyLocalYaml;
import org.gcube.resources.federation.fhnmanager.utils.NodeHelper;
import org.gcube.vomanagement.occi.FHNConnector;
import org.gcube.vomanagement.occi.datamodel.cloud.OSTemplate;
import org.gcube.vomanagement.occi.datamodel.cloud.VM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cesnet.cloud.occi.api.exception.CommunicationException;

public class FHNManagerImpl implements FHNManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(FHNManagerImpl.class);

	private ConnectorFactory connectorFactory;

	private ISProxyInterface isProxy;
	
	public FHNManagerImpl() {
		this.connectorFactory = new ConnectorFactory();
		// this.isProxy = new ISProxyLocalYaml(); //to change with isproxyimpl
		this.isProxy = new ISProxyImpl();

	}	
	
	public Node createNode(String vmProviderId, String serviceProfileId, String resourceTemplateId) {
		VMProvider vmp = this.isProxy.getVMProviderById(vmProviderId);
		FHNConnector connector = this.connectorFactory.getConnector(vmp);

		try {

			connector.connect();

			// 1. collect parameters needed to create the VM
			NodeTemplate nt = this.isProxy.getNodeTemplate(serviceProfileId, vmProviderId);
			LOGGER.debug("NodeTemplate found: " + nt);
			String osTemplateId = nt.getOsTemplateId();
			LOGGER.debug("OSTemplateId found: " + osTemplateId);
			ResourceTemplate resourceTemplate = connector.getResourceTemplate(new URI(resourceTemplateId));
			LOGGER.debug("ResourceTemplate found: " + nt);
			OSTemplate osTemplate = connector.getOSTemplate(new URI(osTemplateId));
			LOGGER.debug("OSTemplate found: " + nt);
			ServiceProfile sp = this.isProxy.getServiceProfileById(serviceProfileId);

			// 2. crete the VM
			URI vmId = connector.createVM("test", osTemplate, resourceTemplate, nt.getScript());
			VM vm = connector.getVM(vmId);

			// 3. update the IS
			Node node = NodeHelper.createNode(vm, vmp, sp, nt, resourceTemplate);
			this.isProxy.addNode(node);
			//this.isProxy.updateIs();
			return node;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Set<Node> findNodes(String serviceProfileId, String vmProviderId) throws UnknownHostException {
		return this.isProxy.findNodes(serviceProfileId, vmProviderId);
	}

	@Override
	public Set<VMProvider> findVMProviders(String serviceProfileId) throws FHNManagerException {
		return this.isProxy.findVMProvidersbyServiceProfile(serviceProfileId);
	}

	public VMProvider getVMProviderbyId(String vmProviderid) {
		return this.isProxy.findVMProviderbyId(vmProviderid);
	}

	@Override
	public Set<ServiceProfile> allServiceProfiles() throws FHNManagerException {
		return this.isProxy.getAllServiceProfiles();

	}

	public Collection<ResourceTemplate> findResourceTemplate(String vmProviderid) {
		if (vmProviderid == null) {
			Collection<ResourceTemplate> listvmp = new HashSet<ResourceTemplate>();
			for (VMProvider vmp2 : findVMProviders(null)) {
				listvmp.addAll(findResourceTemplate(vmp2.getId()));
			}
			return listvmp;
		}

		VMProvider vmp = this.isProxy.getVMProviderById(vmProviderid);
		FHNConnector connector = this.connectorFactory.getConnector(vmp);
		try {
			connector.connect();
			Collection<ResourceTemplate> list = new HashSet<ResourceTemplate>();
			list = connector.listResourceTemplates();
			ResourceReference<VMProvider> rr = new ResourceReference<VMProvider>(vmProviderid);
			for (ResourceTemplate a : list) {
				a.setVmProvider(rr);
				a.setCores(new ISProxyLocalYaml().getCore(a.getId()));
				a.setMemory(new ISProxyLocalYaml().getMemory(a.getId()));
			}
			return list;

		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Node getNodeById(String nodeId) {
		return this.isProxy.getNodeById(nodeId);
	}

	// public void startNode(String NodeId) throws FHNManagerException {
	// String vmProviderId = NodeHelper.getVMProviderId(NodeId);
	// String vmid = NodeHelper.getVMId(NodeId);
	// VMProvider vmp = this.isProxy.getVMProviderById(vmProviderId);
	// FHNConnector connector = this.connectorFactory.getConnector(vmp);
	// try {
	// //connector.connect();
	// connector.startVM(URI.create(vmid));
	// } catch (CommunicationException e) {
	// throw new ConnectorException("Exception received from the connector: " +
	// e.getMessage());
	// }
	// }

	public void startNode(String NodeId) throws FHNManagerException {
		VMProvider vmp = this.isProxy.getVMProviderById(this.getNodeById(NodeId).getVmProvider().getRefId());
		FHNConnector connector = this.connectorFactory.getConnector(vmp);
		try {
			connector.startVM(URI.create(this.getNodeById(NodeId).getId()));
			this.isProxy.updateIs();
		} catch (CommunicationException e) {
			throw new ConnectorException("Exception received from the connector: " + e.getMessage());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stopNode(String NodeId) throws FHNManagerException {
		VMProvider vmp = this.isProxy.getVMProviderById(this.getNodeById(NodeId).getVmProvider().getRefId());
		FHNConnector connector = this.connectorFactory.getConnector(vmp);
		try {
			connector.stopVM(URI.create(this.getNodeById(NodeId).getId()));
			this.isProxy.updateIs();

		} catch (CommunicationException e) {
			throw new ConnectorException("Exception received from the connector: " + e.getMessage());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// id is the uri
	public void deleteNode(String NodeId) throws FHNManagerException {
		Node n = this.isProxy.getNodeById(NodeId);
		VMProvider vmp = this.isProxy.getVMProviderById(this.getNodeById(NodeId).getVmProvider().getRefId());
		FHNConnector connector = this.connectorFactory.getConnector(vmp);
		try {
			connector.destroyVM(URI.create(this.getNodeById(NodeId).getId()));
			this.isProxy.deleteNode(n);
			this.isProxy.deleteHostingNode(n.getHostname());
		} catch (CommunicationException e) {
			throw new ConnectorException("Exception received from the connector: " + e.getMessage());
		}
	}

	// public void deleteNode(String NodeId) throws FHNManagerException {
	// String vmProviderId = NodeHelper.getVMProviderId(NodeId);
	// String vmid = NodeHelper.getVMId(NodeId);
	// Node n = this.isProxy.getNodeById(NodeId);
	// VMProvider vmp = this.isProxy.getVMProviderById(vmProviderId);
	// FHNConnector connector = this.connectorFactory.getConnector(vmp);
	// try {
	// //connector.connect();
	// connector.destroyVM(URI.create(vmid));
	// this.isProxy.deleteNode(n);
	// } catch (CommunicationException e) {
	// throw new ConnectorException("Exception received from the connector: " +
	// e.getMessage());
	// }
	// }

//	public static void main(String[] args) {
//		FHNManagerImpl a = new FHNManagerImpl();
//		ScopeProvider.instance.set("/gcube");
//		// a.startNode("https://carach5.ics.muni.cz:11443/compute/73117");
//		// try {
//		// a.findNodes(null, null);
//		// } catch (UnknownHostException e) {
//		// // TODO Auto-generated catch block
//		// e.printStackTrace();
//		// }
//		//a.getNodeById("https://carach5.ics.muni.cz:11443/compute/73237");
//		//a.deleteNode("https://carach5.ics.muni.cz:11443/compute/73305");
	
	//}

}
