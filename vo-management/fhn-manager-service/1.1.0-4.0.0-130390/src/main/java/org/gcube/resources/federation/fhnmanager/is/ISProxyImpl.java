package org.gcube.resources.federation.fhnmanager.is;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.publisher.AdvancedScopedPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.informationsystem.publisher.ScopedPublisher;
import org.gcube.informationsystem.publisher.exception.RegistryNotFoundException;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.federation.fhnmanager.api.type.Node;
import org.gcube.resources.federation.fhnmanager.api.type.NodeTemplate;
import org.gcube.resources.federation.fhnmanager.api.type.ResourceReference;
import org.gcube.resources.federation.fhnmanager.api.type.ResourceTemplate;
import org.gcube.resources.federation.fhnmanager.api.type.ServiceProfile;
import org.gcube.resources.federation.fhnmanager.api.type.VMProvider;
import org.gcube.resources.federation.fhnmanager.api.type.VMProviderCredentials;
import org.gcube.resources.federation.fhnmanager.impl.ConnectorFactory;
import org.gcube.vomanagement.occi.FHNConnector;
import org.gcube.vomanagement.occi.datamodel.cloud.VM;
import org.gcube.vomanagement.occi.datamodel.cloud.VMNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.core.joran.action.ActionUtil.Scope;
import cz.cesnet.cloud.occi.api.exception.CommunicationException;

public class ISProxyImpl implements ISProxyInterface {
	private static final Logger LOGGER = LoggerFactory.getLogger(ISProxyImpl.class);

	private ConnectorFactory connectorFactory;

//	public ISProxyImpl() {
//		ScopeProvider.instance.set("/gcube");
//		
//		// TODO Auto-generated constructor stub
//	}
	
	private ServiceProfile convertSp(GenericResource a) {
		ServiceProfile out = new ServiceProfile();
		out.setId(a.id());
		out.setDescription(a.profile().body().getElementsByTagName("description").item(0).getTextContent());
		out.setVersion(a.profile().body().getElementsByTagName("version").item(0).getTextContent());
		out.setSuggestedCores(
				Integer.valueOf(a.profile().body().getElementsByTagName("suggestedCores").item(0).getTextContent()));
		out.setMinCores(Integer.valueOf(a.profile().body().getElementsByTagName("minCores").item(0).getTextContent()));
		out.setSuggestedRam(
				Long.valueOf(a.profile().body().getElementsByTagName("suggestedRam").item(0).getTextContent()));
		out.setMinRam(Long.valueOf(a.profile().body().getElementsByTagName("minRam").item(0).getTextContent()));
		out.setCreationDate(a.profile().body().getElementsByTagName("creationDate").item(0).getTextContent());
		Set<ResourceReference<org.gcube.resources.federation.fhnmanager.api.type.Software>> sw = new HashSet<ResourceReference<org.gcube.resources.federation.fhnmanager.api.type.Software>>();
		ResourceReference<org.gcube.resources.federation.fhnmanager.api.type.Software> rr = new ResourceReference<org.gcube.resources.federation.fhnmanager.api.type.Software>();
		rr.setRefId(a.profile().body().getElementsByTagName("deployedSoftware").item(0).getTextContent());
		sw.add(rr);
		out.setDeployedSoftware(sw);
		return out;
	}

	private NodeTemplate convertNt(GenericResource a) {

		NodeTemplate out = new NodeTemplate();

		out.setId(a.id());
		String c = a.profile().body().getElementsByTagName("scriptURL").item(0).getTextContent();
		URL url;
		try {
			url = new URL(c);
			out.setScript(url);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		out.setOsTemplateId(a.profile().body().getElementsByTagName("osTemplateId").item(0).getTextContent());
		ResourceReference<org.gcube.resources.federation.fhnmanager.api.type.ServiceProfile> rr = new ResourceReference<org.gcube.resources.federation.fhnmanager.api.type.ServiceProfile>();
		rr.setRefId(a.profile().body().getElementsByTagName("serviceProfileId").item(0).getTextContent());
		out.setServiceProfile(rr);
		return out;
	}

	private VMProvider convertVMP(GenericResource a) {

		VMProvider vmp = new VMProvider();
		VMProviderCredentials vmc = new VMProviderCredentials();

		vmp.setId(a.id());
		vmp.setName(a.profile().name());
		Set<ResourceReference<NodeTemplate>> sw = new HashSet<ResourceReference<NodeTemplate>>();

		for (int i = 0; i < a.profile().body().getElementsByTagName("nodeTemplateId").getLength(); i++) {
			ResourceReference<NodeTemplate> h1 = new ResourceReference<NodeTemplate>();
			h1.setRefId(a.profile().body().getElementsByTagName("nodeTemplateId").item(i).getTextContent());
			sw.add(h1);
		}
		vmp.setNodeTemplates(sw);

		String serviceEndpointId = a.profile().body().getElementsByTagName("endpoint").item(0).getTextContent();

		SimpleQuery query2 = queryFor(ServiceEndpoint.class);
		query2.addCondition("$resource/ID/text() eq '" + serviceEndpointId + "'").setResult("$resource");
		DiscoveryClient<ServiceEndpoint> client2 = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> df = client2.submit(query2);

		for (ServiceEndpoint b : df) {
			if (b.id().equals(serviceEndpointId)) {
				vmp.setEndpoint(b.profile().accessPoints().iterator().next().address());
				vmc.setType(b.profile().accessPoints().iterator().next().propertyMap().get("type").value());
				vmc.setEncodedCredentails(
						b.profile().accessPoints().iterator().next().propertyMap().get("encodedCredentials").value());
				vmp.setCredentials(vmc);
			}
		}
		return vmp;
	}

	@Override
	public ServiceProfile getServiceProfileById(String serviceProfileId) {
		ServiceProfile out = new ServiceProfile();
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/ID/text() eq '" + serviceProfileId + "'").setResult("$resource");
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		List<GenericResource> ds = client.submit(query);
		for (GenericResource a : ds) {
			out = this.convertSp(a);
		}

		return out;
	}

	@Override
	public NodeTemplate getNodeTemplateById(String nodeTemplateId) {
		// TODO Auto-generated method stub
		NodeTemplate out = new NodeTemplate();
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/ID/text() eq '" + nodeTemplateId + "'").setResult("$resource");
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		List<GenericResource> ds = client.submit(query);
		for (GenericResource a : ds) {
			out = this.convertNt(a);
		}
		return out;
	}

	@Override
	public Set<ServiceProfile> getAllServiceProfiles() {
		Set<ServiceProfile> out = new HashSet<ServiceProfile>();
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/SecondaryType/text() eq 'ServiceProfile'").setResult("$resource");
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		List<GenericResource> ds = client.submit(query);
		for (GenericResource a : ds) {
			out.add(this.convertSp(a));
		}
		return out;
	}

	@Override
	public Set<Node> getAllNodes() {
		Set<Node> out = new HashSet<Node>();
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/SecondaryType/text() eq 'FHN-nodes'").setResult("$resource");
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		List<GenericResource> ds = client.submit(query);
		for (GenericResource a : ds) {
			out.add(this.convertNode(a));
		}
		return out;
	}

	@Override
	public Set<VMProvider> getAllVMProviders() {
		// TODO Auto-generated method stub

		Set<VMProvider> out = new HashSet<VMProvider>();
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/SecondaryType/text() eq 'VMProviders'").setResult("$resource");
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		List<GenericResource> ds = client.submit(query);
		for (GenericResource a : ds) {
			out.add(this.convertVMP(a));
		}
		return out;
	}

	public Set<NodeTemplate> getAllNodeTemplates() {
		// TODO Auto-generated method stub

		Set<NodeTemplate> out = new HashSet<NodeTemplate>();
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/SecondaryType/text() eq 'NodeTemplate'").setResult("$resource");
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		List<GenericResource> ds = client.submit(query);

		for (GenericResource a : ds) {
			out.add(this.convertNt(a));
		}
		return out;
	}

	@Override
	public VMProvider getVMProviderById(String vmProviderid) {
		VMProvider out = new VMProvider();
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/ID/text() eq '" + vmProviderid + "'").setResult("$resource");
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		List<GenericResource> ds = client.submit(query);
		for (GenericResource a : ds) {
			out = this.convertVMP(a);
		}
		return out;
	}

	private Node convertNode(GenericResource a) {

		Node out = new Node();
		out.setId(a.profile().name());
		out.setHostname(a.profile().body().getElementsByTagName("hostname").item(0).getTextContent());
		out.setStatus(a.profile().body().getElementsByTagName("status").item(0).getTextContent());

		ResourceReference<VMProvider> vp = new ResourceReference<VMProvider>();
		vp.setRefId(a.profile().body().getElementsByTagName("vmProviderId").item(0).getTextContent());
		out.setVmProvider(vp);

		ResourceReference<ResourceTemplate> rt = new ResourceReference<ResourceTemplate>();
		rt.setRefId(a.profile().body().getElementsByTagName("resourceTemplateId").item(0).getTextContent());
		out.setResourceTemplate(rt);

		ResourceReference<NodeTemplate> nt = new ResourceReference<NodeTemplate>();
		nt.setRefId(a.profile().body().getElementsByTagName("nodeTemplateId").item(0).getTextContent());
		out.setNodeTemplate(nt);

		ResourceReference<ServiceProfile> sp = new ResourceReference<ServiceProfile>();
		sp.setRefId(a.profile().body().getElementsByTagName("serviceProfileId").item(0).getTextContent());
		out.setServiceProfile(sp);
		return out;

	}

	@Override
	public VMProvider findVMProviderbyId(String vmProviderId) {
		VMProvider out = new VMProvider();
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/ID/text() eq '" + vmProviderId + "'").setResult("$resource");
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		List<GenericResource> ds = client.submit(query);
		for (GenericResource a : ds) {
			out = this.convertVMP(a);
		}
		return out;
	}

	public Set<VMProvider> findVMProvidersbyServiceProfile(String serviceProfileId) {
		// TODO Auto-generated method stub
		Set<VMProvider> s1 = // variabile
				serviceProfileId == null ? // condizione
						this.getAllVMProviders() // valore1
						: getVMProvidersByServiceProfile(serviceProfileId); // else
																			// valore2
		return s1;
	}

	public Set<VMProvider> getVMProvidersByServiceProfile(String serviceProfileId) {

		Set<VMProvider> result = new HashSet<>();
		for (VMProvider vmp : this.getAllVMProviders()) {
			for (NodeTemplate nt : getVMProviderNodeTemplates(vmp)) {
				if (nt.getServiceProfile() != null && nt.getServiceProfile().getRefId().equals(serviceProfileId)) {
					result.add(vmp);
				}
			}
		}
		return result;
	}

	public Set<NodeTemplate> getVMProviderNodeTemplates(VMProvider vm) {
		Set<NodeTemplate> nodeTemplates = new HashSet<NodeTemplate>();
		vm = this.getVMProviderById(vm.getId());
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

	private static void publishScopedResource(GenericResource a, List<String> scopes)
			throws RegistryNotFoundException, Exception {
		StringWriter stringWriter = new StringWriter();
		Resources.marshal(a, stringWriter);

		ScopedPublisher scopedPublisher = RegistryPublisherFactory.scopedPublisher();
		try {
			System.out.println(scopes);
			System.out.println(stringWriter);
			scopedPublisher.create(a, scopes);
		} catch (RegistryNotFoundException e) {
			System.out.println(e);
			throw e;
		}
	}

	private static void unPublishScopedResource(GenericResource resource) throws RegistryNotFoundException, Exception {
		ScopedPublisher scopedPublisher = RegistryPublisherFactory.scopedPublisher();
		AdvancedScopedPublisher advancedScopedPublisher = new AdvancedScopedPublisher(scopedPublisher);
		String id = resource.id();
		LOGGER.debug("Trying to remove {} with ID {} from {}", resource.getClass().getSimpleName(), id,
				ScopeProvider.instance.get());
		// scopedPublisher.remove(resource, scopes);
		advancedScopedPublisher.forceRemove(resource);
		LOGGER.debug("{} with ID {} removed successfully", resource.getClass().getSimpleName(), id);
	}

	private static void unPublishHostingNode(HostingNode resource) throws RegistryNotFoundException, Exception {
		ScopedPublisher scopedPublisher = RegistryPublisherFactory.scopedPublisher();
		AdvancedScopedPublisher advancedScopedPublisher = new AdvancedScopedPublisher(scopedPublisher);
		String id = resource.id();
		LOGGER.debug("Trying to remove {} with ID {} from {}", resource.getClass().getSimpleName(), id,
				ScopeProvider.instance.get());
		// scopedPublisher.remove(resource, scopes);
		advancedScopedPublisher.forceRemove(resource);
		LOGGER.debug("{} with ID {} removed successfully", resource.getClass().getSimpleName(), id);
	}
	
	@Override
	public void addNode(Node node) {
		// this.allNodes.add(node);
		GenericResource a = new GenericResource();

		List<String> b = new LinkedList<>();
		String scope = ScopeProvider.instance.get();
		b.add(scope);

		a.newProfile().name(node.getId()).type("FHN-nodes").description("FHN node_test");
		// a.profile().newBody().setAttribute("nodeTemplate",
		// node.getNodeTemplate().getRefId());

		a.profile().newBody("<nodeTemplateId>" + node.getNodeTemplate().getRefId() + "</nodeTemplateId>" + "\n"
				+ "<vmProviderId>" + node.getVmProvider().getRefId() + "</vmProviderId>" + "\n" + "<serviceProfileId>"
				+ node.getServiceProfile().getRefId() + "</serviceProfileId>" + "\n" + "<resourceTemplateId>"
				+ node.getResourceTemplate().getRefId() + "</resourceTemplateId>" + "\n" + "<status>" + node.getStatus()
				+ "</status>" + "\n" + "<hostname>" + node.getHostname() + "</hostname>");
		try {
			publishScopedResource(a, b);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// this.getAllNodes().add(node);
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
			return this.getAllNodes();
		}

		Set<Node> result = new HashSet<>();

		for (Node vn2 : this.getAllNodes()) {
			if (vn2.getServiceProfile().getRefId().equals(serviceProfileId)) {
				result.add(vn2);
			}
		}
		return result;
	}

	private Set<Node> getNodesByVMProviderId(String vmProviderId) {
		if (vmProviderId == null) {
			return this.getAllNodes();
		}

		Set<Node> result = new HashSet<>();

		for (Node vn2 : this.getAllNodes()) {
			if (vn2.getVmProvider().getRefId().equals(vmProviderId)) {
				result.add(vn2);
			}
		}
		return result;
	}

	@Override
	// id is the uri
	public Node getNodeById(String nodeId) {
		Node out = new Node();
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/Name/text() eq '" + nodeId + "'").setResult("$resource");
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		List<GenericResource> ds = client.submit(query);
		for (GenericResource a : ds) {
			out = this.convertNode(a);
		}
		return out;
	}

	@Override
	public void deleteNode(Node node) {

		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/SecondaryType/text() eq 'FHN-nodes'").setResult("$resource");
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		List<GenericResource> ds = client.submit(query);
		for (GenericResource a : ds) {
			if (a.profile().name().equals(node.getId())) {
				String t = node.getHostname();
				try {
					unPublishScopedResource(a);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		// this.getAllNodes().remove(node);

	}
	
	@Override
	public void deleteHostingNode(String hostname){
		
		SimpleQuery query = queryFor(HostingNode.class);
		query.addCondition("$resource/Profile/GHNDescription/Name/text() eq '" + hostname+":80" + "'").setResult("$resource");
		DiscoveryClient<HostingNode> client = clientFor(HostingNode.class);
		List<HostingNode> ds = client.submit(query);
		for (HostingNode a : ds) {
			if (a.profile().description().name().equals(hostname+":80")) {
				try {
					unPublishHostingNode(a);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
	}
	
	public void updateIs() throws CommunicationException, UnknownHostException {
		for (VMProvider a : this.getAllVMProviders()) {
			this.connectorFactory = new ConnectorFactory();
			FHNConnector connector = this.connectorFactory.getConnector(a);
			connector.connect();
			Collection<VM> listvm = connector.listVM();
			for (VM vm : listvm) {
				for (VMNetwork vmn : vm.getNetworks()) {
					Node isnode = this.getNodeById(vm.getEndpoint().toString());
					if (isnode != null) {
						InetAddress inetAddress = InetAddress.getByName(vmn.getAddress());
						System.out.println(inetAddress);
						System.out.println(isnode.getId());
						isnode.setHostname(inetAddress.getHostName());
						System.out.println(inetAddress.getHostName());
						isnode.setStatus(vm.getStatus());
						System.out.println(vm.getStatus());
						for (Node n : this.getAllNodes()) {
							if (n.getId().equals(isnode.getId())) {
								n.setHostname(isnode.getHostname());
								n.setStatus(isnode.getStatus());
								this.addNode(isnode);
								this.deleteNode(n);
							}

						}

					}
				}
			}
		}
	}
		
	public static void main(String[] args) {
		ScopeProvider.instance.set("/gcube");

		ISProxyInterface a = new ISProxyImpl();
		// a.getNodeById("https://carach5.ics.muni.cz:11443/compute/72964");
		//a.getServiceProfileById("30a2b7bd-2156-424d-ad40-0721f4e4888e");
		// a.getNodeTemplateById("05f9d5fe-e4be-4b66-ba2b-dc83b9ec9484");
		// a.getAllVMProviders();
		//a.deleteHostingNode("stoor109.meta.zcu.cz:80");
		//System.out.println(a.getAllServiceProfiles());
		// a.getVMProviderById("58d494a2-505d-4550-8d48-83ade4c2b49e");
		// a.findVMProviderbyId("58d494a2-505d-4550-8d48-83ade4c2b49e");
		// a.findVMProvidersbyServiceProfile("30a2b7bd-2156-424d-ad40-0721f4e4888e");
		// a.getNodeTemplateById("05f9d5fe-e4be-4b66-ba2b-dc83b9ec9484");
		// System.out.println(a.findVMProvidersbyServiceProfile("30a2b7bd-2156-424d-ad40-0721f4e4888e"));
		// a.getVMProviderNodeTemplates("58d494a2-505d-4550-8d48-83ade4c2b49e");
		// a.getVMProviderNodeTemplates("58d494a2-505d-4550-8d48-83ade4c2b49e");
	
		
		//a.getScopes(ISProxyImpl.ctx);
		//a.onStart();
		// try {
		// a.updateIs();
		// } catch (UnknownHostException | CommunicationException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// System.out.println(a.getAllNodes().iterator().next().getHostname());
		// System.out.println(a.findNodes(null, null));
		// a.findNodes("30a2b7bd-2156-424d-ad40-0721f4e4888e",
		// "58d494a2-505d-4550-8d48-83ade4c2b49e");
		// try {
		// a.updateIs();
		// Timer timer = new Timer();
		// TimerTask task = new ISNodeStatusUpdater();
		// timer.schedule(task, 1000, 10000);
		// } catch (UnknownHostException | CommunicationException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// Node b = new Node();
		// b.setId("10fd77cf-46a0-4e56-a7c4-7e38a4f9ae8b");
		// a.addNode(b);
		//a.deleteHostingNode("stoor71.meta.zcu.cz");
		// Node node = new Node();
		// node.setId("https://carach5.ics.muni.cz:11443/compute/72965");
		// a.deleteNode(node);
		// System.out.println(a.getAllNodes());
		// System.out.println(a.getAllNodes());
		 try {
		 a.updateIs();
		 } catch (UnknownHostException | CommunicationException e) {
		 // TODO Auto-generated catch block
		 e.printStackTrace();
		}
		//a.getScopeProviders();
// a.findNodes("30a2b7bd-2156-424d-ad40-0721f4e4888e",
		// "58d494a2-505d-4550-8d48-83ade4c2b49e");
		// System.out.println(a.findNodes("a", "b"));
//		System.out.println(a.getAllVMProviders());

		// System.out.println(a.getAllVMProviders());
		// System.out.println(a.getNodeById("https://carach5.ics.muni.cz:11443/compute/73088"));
	}

}
