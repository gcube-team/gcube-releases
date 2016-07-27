package org.gcube.resources.federation.fhnmanager.is;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashSet;

import java.util.Set;

import org.gcube.resources.federation.fhnmanager.api.type.Node;
import org.gcube.resources.federation.fhnmanager.api.type.NodeTemplate;
import org.gcube.resources.federation.fhnmanager.api.type.ResourceReference;
import org.gcube.resources.federation.fhnmanager.api.type.ResourceTemplate;
import org.gcube.resources.federation.fhnmanager.api.type.ServiceProfile;
import org.gcube.resources.federation.fhnmanager.api.type.VMProvider;
import org.gcube.resources.federation.fhnmanager.impl.ConnectorFactory;
import org.gcube.resources.federation.fhnmanager.utils.NodeHelper;
import org.gcube.resources.federation.fhnmanager.utils.Props;
import org.gcube.vomanagement.occi.FHNConnector;
import org.gcube.vomanagement.occi.datamodel.cloud.VM;
import org.gcube.vomanagement.occi.datamodel.cloud.VMNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import cz.cesnet.cloud.occi.api.exception.CommunicationException;

public class ISProxyLocalYaml implements ISProxyInterface {

	private static final Logger LOGGER = LoggerFactory.getLogger(ISProxyLocalYaml.class);
	static Props a = new Props();
	private static final String NODES_STORAGE_FILE = a.getPath() + File.separator + "nodes.yml";

	private ConnectorFactory connectorFactory;

	private Set<ServiceProfile> allServiceProfiles;
	private Set<VMProvider> allVMProviders;
	private Set<NodeTemplate> allNodeTemplates;
	private Set<Node> allNodes;
	private Set<ResourceTemplate> allResourceTemplate;

	private void loadNodes() {
		Yaml yamlp = new Yaml(new Constructor(Node.class));
		InputStream input = null;
		try {
			input = new FileInputStream(new File(NODES_STORAGE_FILE));
			allNodes = new HashSet<>();
			for (Object data : yamlp.loadAll(input)) {
				allNodes.add((Node) data);
			}

			LOGGER.info("Loaded " + this.allNodes.size() + " Nodes: " + this.allNodes);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadVMProviders() {
		Yaml yamlp = new Yaml(new Constructor(VMProvider.class));
		InputStream input = null;
		try {
			input = ISProxyLocalYaml.class.getClassLoader().getResourceAsStream("vmproviders.yml");
			allVMProviders = new HashSet<>();
			for (Object data : yamlp.loadAll(input)) {
				allVMProviders.add((VMProvider) data);
			}

			LOGGER.info("Loaded " + this.allVMProviders.size() + " VMProviders: " + this.allVMProviders);

		} finally {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadResourceTemplates() {
		Yaml yamlp = new Yaml(new Constructor(ResourceTemplate.class));
		InputStream input99 = null;
		try {
			input99 = ISProxyLocalYaml.class.getClassLoader().getResourceAsStream("resourcetemplates.yml");
			allResourceTemplate = new HashSet<>();
			for (Object data : yamlp.loadAll(input99)) {
				allResourceTemplate.add((ResourceTemplate) data);
			}
			LOGGER.info("Loaded " + this.allResourceTemplate.size() + " ResourceTemplate: " + this.allResourceTemplate);
		} finally {
			try {
				input99.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadNodeTemplates() {
		Yaml yamlnt = new Yaml(new Constructor(NodeTemplate.class));
		InputStream input5 = null;
		try {
			input5 = ISProxyLocalYaml.class.getClassLoader().getResourceAsStream("nodetemplates.yml");

			allNodeTemplates = new HashSet<>();
			for (Object data : yamlnt.loadAll(input5)) {
				allNodeTemplates.add((NodeTemplate) data);
			}
			LOGGER.info("Loaded " + this.allNodeTemplates.size() + " NodeTemplates: " + this.allNodeTemplates);

		} finally {
			try {
				input5.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadServiceProfiles() {
		Yaml yamls = new Yaml(new Constructor(ServiceProfile.class));
		InputStream input3 = null;
		try {
			input3 = ISProxyLocalYaml.class.getClassLoader().getResourceAsStream("serviceprofiles.yml");

			allServiceProfiles = new HashSet<>();
			for (Object data : yamls.loadAll(input3)) {
				allServiceProfiles.add((ServiceProfile) data);
				// System.out.println(allServiceProfiles);
			}
			LOGGER.info("Loaded " + this.allServiceProfiles.size() + " ServiceProfiles: " + this.allServiceProfiles);
		} finally {
			try {
				input3.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public ISProxyLocalYaml() {

		this.loadVMProviders();
		this.loadServiceProfiles();
		this.loadNodeTemplates();
		this.loadNodes();
		this.loadResourceTemplates();

	}

	public int getCore(String id) {
		for (ResourceTemplate x12 : this.allResourceTemplate) {
			if (x12.getId().equals(id)) {
				return x12.getCores();
			}
		}
		return 0;
	}

	public Long getMemory(String id) {
		for (ResourceTemplate a : this.allResourceTemplate) {
			if (a.getId().equals(id)) {
				return a.getMemory();
			}
		}
		return null;
	}

	@Override
	public VMProvider getVMProviderById(String vmpId) {
		for (VMProvider vmp : this.allVMProviders) {
			if (vmp.getId().equals(vmpId)) {
				return vmp;
			}
		}
		return null;
	}

	@Override
	public NodeTemplate getNodeTemplate(String serviceProfileId, String vmProviderId) {

		VMProvider vmps = this.getVMProviderById(vmProviderId);
		for (NodeTemplate nt : getVMProviderNodeTemplates(vmps.getId())) {
			if (nt.getServiceProfile() != null && nt.getServiceProfile().getRefId().equals(serviceProfileId)) {
				return nt;
			}
		}
		return null;
	}

	@Override
	public NodeTemplate getNodeTemplateById(String nodeTemplateId) {
		for (NodeTemplate nt : allNodeTemplates) {
			if (nt.getId().equals(nodeTemplateId)) {
				return nt;
			}
		}
		return null;
	}

	@Override
	public void addNode(Node node) {
		this.allNodes.add(node);
		this.dumpNodes();
	}

	public void updateIs() throws CommunicationException, UnknownHostException {
		for (VMProvider a : this.allVMProviders) {
			this.connectorFactory = new ConnectorFactory();
			FHNConnector connector = this.connectorFactory.getConnector(a);
			connector.connect();
			Collection<VM> listvm = connector.listVM();
			for (VM vm : listvm) {
				for (VMNetwork vmn : vm.getNetworks()) {
					Node isnode = this.getNodeById(NodeHelper.createNodeId(a.getId(), vm.getEndpoint().toString()));
					if (isnode != null) {
						InetAddress inetAddress = InetAddress.getByName(vmn.getAddress());
						System.out.println(inetAddress);
						isnode.setHostname(inetAddress.getHostName());
						System.out.println(inetAddress.getHostName());
						isnode.setStatus(vm.getStatus());
						System.out.println(vm.getStatus());

					}
				}
			}
		}
		this.dumpNodes();

	}

	public void dumpNodes() {
		Yaml yaml = new Yaml();
		try {
			File file = new File(NODES_STORAGE_FILE);
			yaml.dumpAll(this.allNodes.iterator(), new FileWriter(file));
			LOGGER.debug("Nodes stored to " + file.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ServiceProfile getServiceProfileById(String serviceProfileId) {
		for (ServiceProfile sp : this.allServiceProfiles) {
			if (sp.getId().equals(serviceProfileId)) {
				return sp;
			}
		}
		return null;
	}

	@Override
	public Set<Node> findNodes(String serviceProfileId, String vmProviderId) {
		Set<Node> n1 = getNodeByServiceProfileId(serviceProfileId);
		LOGGER.debug("Nodes filtered by ServiceProfile: " + n1);
		Set<Node> n2 = getNodesByVMProviderId(vmProviderId);
		LOGGER.debug("Nodes filtered by VMProvider: " + n2);
		Set<Node> intersection = new HashSet<Node>(n1);
		intersection.retainAll(n2);
		LOGGER.debug("Returning: " + intersection);
		return intersection;
	}

	private Set<Node> getNodeByServiceProfileId(String serviceProfileId) {
		if (serviceProfileId == null) {
			return this.allNodes;
		}

		Set<Node> result = new HashSet<>();

		for (Node vn2 : this.allNodes) {
			if (vn2.getServiceProfile().getRefId().equals(serviceProfileId)) {
				result.add(vn2);
			}
		}
		return result;
	}

	private Set<Node> getNodesByVMProviderId(String vmProviderId) {
		if (vmProviderId == null) {
			return this.allNodes;
		}

		Set<Node> result = new HashSet<>();

		for (Node vn2 : this.allNodes) {
			if (vn2.getVmProvider().getRefId().equals(vmProviderId)) {
				result.add(vn2);
			}
		}
		return result;
	}

	public Set<NodeTemplate> getVMProviderNodeTemplates(VMProvider vm) {
		Set<NodeTemplate> nodeTemplates = new HashSet<NodeTemplate>();
		for (ResourceReference<NodeTemplate> ref : vm.getNodeTemplates()) {
			nodeTemplates.add(getNodeTemplateById(ref.getRefId()));
		}
		return nodeTemplates;
	}

	@Override
	public Set<NodeTemplate> getVMProviderNodeTemplates(String vmProviderId) {
		Set<NodeTemplate> nodeTemplates = new HashSet<NodeTemplate>();
		VMProvider vmp = this.getVMProviderById(vmProviderId);
		for (ResourceReference<NodeTemplate> ref : vmp.getNodeTemplates()) {
			nodeTemplates.add(getNodeTemplateById(ref.getRefId()));
		}
		return nodeTemplates;
	}

	public Set<VMProvider> getVMProvidersByServiceProfile(String serviceProfileId) {
		Set<VMProvider> result = new HashSet<>();
		for (VMProvider vmp : this.allVMProviders) {
			for (NodeTemplate nt : getVMProviderNodeTemplates(vmp)) {
				LOGGER.debug("nt: " + nt.getId() + ", serviceProfile: " + nt.getServiceProfile());
				if (nt.getServiceProfile() != null && nt.getServiceProfile().getRefId().equals(serviceProfileId)) {
					result.add(vmp);
				}
			}

		}
		return result;
	}

	@Override
	public Set<VMProvider> findVMProvidersbyServiceProfile(String serviceProfileId) {
		LOGGER.debug("All VMProviders are: " + this.allVMProviders);
		Set<VMProvider> s1 = serviceProfileId == null ? this.allVMProviders
				: getVMProvidersByServiceProfile(serviceProfileId);
		LOGGER.debug("VMProviders filtered by ServiceProfile: " + s1);
		return s1;
	}

	@Override
	public VMProvider findVMProviderbyId(String vmProviderId) {
		for (VMProvider vmp : this.allVMProviders) {
			if (vmp.getId().equals(vmProviderId)) {
				return vmp;
			}
		}
		return null;
	}

	public Set<ServiceProfile> getAllServiceProfiles() {
		return this.allServiceProfiles;
	}

	@Override
	public Node getNodeById(String nodeId) {
		for (Node n : this.allNodes) {
			if (n.getId().equals(nodeId)) {
				return n;
			}
		}
		return null;
	}

	public void deleteNode(Node node) {
		this.allNodes.remove(node);
		this.dumpNodes();
	}

	public static void main(String[] args) throws UnknownHostException {
		ISProxyLocalYaml a = new ISProxyLocalYaml();
		// a.loadResourceTemplates();
		// a.getMemory("http://fedcloud.egi.eu/occi/compute/flavour/1.0#small");
		// a.loadNodes();
		a.loadServiceProfiles();
	}

	@Override
	public Set<VMProvider> getAllVMProviders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Node> getAllNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteHostingNode(String hostname) {
		// TODO Auto-generated method stub
		
	}

}
