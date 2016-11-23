package org.gcube.resources.federation.fhnmanager.is;

import static org.gcube.resources.discovery.icclient.ICFactory.client;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.gcube.common.encryption.StringEncrypter;
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
import org.gcube.resources.federation.fhnmanager.api.type.OccopusInfrastructureTemplate;
import org.gcube.resources.federation.fhnmanager.api.type.Node;
import org.gcube.resources.federation.fhnmanager.api.type.NodeTemplate;
import org.gcube.resources.federation.fhnmanager.api.type.OccopusInfrastructure;
import org.gcube.resources.federation.fhnmanager.api.type.OccopusInstanceSet;
import org.gcube.resources.federation.fhnmanager.api.type.OccopusNode;
import org.gcube.resources.federation.fhnmanager.api.type.OccopusScalingParams;
import org.gcube.resources.federation.fhnmanager.api.type.ResourceReference;
import org.gcube.resources.federation.fhnmanager.api.type.ResourceTemplate;
import org.gcube.resources.federation.fhnmanager.api.type.ServiceProfile;
import org.gcube.resources.federation.fhnmanager.api.type.VMProvider;
import org.gcube.resources.federation.fhnmanager.api.type.VMProviderCredentials;
import org.gcube.resources.federation.fhnmanager.impl.ConnectorFactory;
import org.gcube.resources.federation.fhnmanager.occopus.OccopusClient;
import org.gcube.resources.federation.fhnmanager.occopus.OccopusNodeDefinitionImporter;
import org.gcube.resources.federation.fhnmanager.occopus.model.GetInfraResponse;
import org.gcube.resources.federation.fhnmanager.utils.NodeHelper;
import org.gcube.vomanagement.occi.FHNConnector;
import org.gcube.vomanagement.occi.datamodel.cloud.VM;
import org.gcube.vomanagement.occi.datamodel.cloud.VMNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

import cz.cesnet.cloud.occi.api.exception.CommunicationException;

public class ISProxyImpl implements ISProxyInterface {
	private static final Logger LOGGER = LoggerFactory.getLogger(ISProxyImpl.class);

	private ConnectorFactory connectorFactory;

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

	private OccopusInfrastructureTemplate convertInfraTemplate(GenericResource a) {
		OccopusInfrastructureTemplate infra = new OccopusInfrastructureTemplate();
		infra.setOccopusDescription(a.profile().body().getTextContent());
		return infra;

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
				vmc.setVo(b.profile().accessPoints().iterator().next().propertyMap().get("vo").value());
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
		GenericResource r = this.getGenericResourceById(nodeTemplateId);

		if (r == null) {
			return null;
		}

		return this.convertNt(r);
	}

	@Override
	public Set<OccopusInfrastructure> getAllInfrastructures() {
		Set<OccopusInfrastructure> infraset = new HashSet<OccopusInfrastructure>();
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/SecondaryType/text() eq 'OccopusInfras'").setResult("$resource");
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		List<GenericResource> ds = client.submit(query);
		for (GenericResource a : ds) {
			infraset.add(this.convertInfra(a));
		}
		return infraset;

	}

	
	@Override
	public List<OccopusInfrastructure> getAllInfrastructuresList() {
		List<OccopusInfrastructure> infraset = new LinkedList<OccopusInfrastructure>();
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/SecondaryType/text() eq 'OccopusInfras'").setResult("$resource");
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		List<GenericResource> ds = client.submit(query);
		for (GenericResource a : ds) {
			infraset.add(this.convertInfra(a));
		}
		return infraset;

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
		LOGGER.debug("Found " + out.size() + " service profiles");
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

	@Override
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

		LOGGER.debug("Found " + out.size() + " node templates");
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

	private OccopusInfrastructure convertInfra(GenericResource b) {
		OccopusInfrastructure out = new OccopusInfrastructure();
		out.setId(b.profile().name());
		out.setInfrastructureTemplate(b.profile().body().getFirstChild().getTextContent());

		Map<String, OccopusInstanceSet> istanceSets = new HashMap<String, OccopusInstanceSet>();
		NodeList instanceSetNodeList = b.profile().body().getElementsByTagName("instanceset");
		for (int i = 0; i < instanceSetNodeList.getLength(); i++) {
			org.w3c.dom.Node iset = instanceSetNodeList.item(i);

			OccopusInstanceSet isetObj = new OccopusInstanceSet();
			String name = null;
			Map<String, OccopusNode> nodes = new HashMap<String, OccopusNode>();
			for (int j = 0; j < iset.getChildNodes().getLength(); j++) {
				org.w3c.dom.Node cn = iset.getChildNodes().item(j);
				if (cn.getNodeName().equals("name")) {
					name = cn.getTextContent();
					LOGGER.debug("FOUND name " + name);
				}
				if (cn.getNodeName().equals("scaling")) {
					OccopusScalingParams sp = new OccopusScalingParams();
					for (int k = 0; k < cn.getChildNodes().getLength(); k++) {
						org.w3c.dom.Node sn = cn.getChildNodes().item(k);
						if (sn.getNodeName().equals("min")) {
							sp.setMin(Integer.valueOf(sn.getTextContent()));
						}
						if (sn.getNodeName().equals("max")) {
							sp.setMax(Integer.valueOf(sn.getTextContent()));
						}
						if (sn.getNodeName().equals("actual")) {
							sp.setActual(Integer.valueOf(sn.getTextContent()));
						}
						if (sn.getNodeName().equals("target")) {
							sp.setTarget(Integer.valueOf(sn.getTextContent()));
						}
					}
					isetObj.setScaling(sp);
				}
				if (cn.getNodeName().equals("node")) {
					String id = null;
					OccopusNode n = new OccopusNode();
					for (int k = 0; k < cn.getChildNodes().getLength(); k++) {
						org.w3c.dom.Node nn = cn.getChildNodes().item(k);
						if (nn.getNodeName().equals("id")) {
							id = nn.getTextContent();
							LOGGER.debug("FOUND id " + id);

						}
						if (nn.getNodeName().equals("ip")) {
							n.setResource_address(nn.getTextContent());
							LOGGER.debug("FOUND ip " + n.getResource_address());

						}
						if (nn.getNodeName().equals("state")) {
							n.setState(nn.getTextContent());
							LOGGER.debug("FOUND state " + n.getState());

						}
					}
					if (id != null) {
						LOGGER.debug("FOUND id " + id);
						nodes.put(id, n);
					}

				}

			}
			isetObj.setInstances(nodes);
			if (name != null) {
				istanceSets.put(name, isetObj);

			}
		}

		out.setInstanceSets(istanceSets);
		return out;
	}

	@Override
	public OccopusInfrastructure returnInfra(OccopusInfrastructureTemplate a) {
		OccopusInfrastructure out = new OccopusInfrastructure();
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/Name/text() eq '" + a.getId() + "'").setResult("$resource");
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		List<GenericResource> ds = client.submit(query);
		for (GenericResource b : ds) {
			out = this.convertInfra(b);
		}
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

	@Override
	public OccopusInfrastructureTemplate returnInfraTemplate(String infraid) {
		GenericResource r = this.getGenericResourceByName(infraid);

		if (r == null) {
			return null;
		}

		OccopusInfrastructureTemplate t = new OccopusInfrastructureTemplate();
		t.setId(r.profile().name());
		t.setOccopusDescription(this.decodeMultilenString(r.profile().body().getFirstChild().getTextContent()));

		return t;
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
			NodeTemplate nt = getNodeTemplateById(ref.getRefId());
			if (nt != null) {
				nodeTemplates.add(nt);
			}
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

	public void updateNode(Node node) {

		ScopedPublisher scopedPublisher = RegistryPublisherFactory.scopedPublisher();

		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/Name/text() eq '" + node.getId() + "'").setResult("$resource");
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		List<GenericResource> ds = client.submit(query);
		if (ds.isEmpty()) {
			return;
		}
		GenericResource a = ds.get(0);
		a.profile().newBody(this.getNodeBody(node));
		try {
			scopedPublisher.update(a);
		} catch (RegistryNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void updateInfra(OccopusInfrastructure infra) {
		ScopedPublisher scopedPublisher = RegistryPublisherFactory.scopedPublisher();

		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/Name/text() eq '" + infra.getId() + "'").setResult("$resource");
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		List<GenericResource> ds = client.submit(query);
		if (ds.isEmpty()) {
			return;
		}
		GenericResource a = ds.get(0);
		a.profile().newBody(this.getInfraBody(infra));
		try {
			scopedPublisher.update(a);
		} catch (RegistryNotFoundException e) {
			e.printStackTrace();
		}
	}

	private String getInfraBody(OccopusInfrastructure infra) {

		String isetsXml = "";

		if (infra.getInstanceSets() != null) {
			for (String k : infra.getInstanceSets().keySet()) {
				OccopusInstanceSet iset = infra.getInstanceSets().get(k);
				OccopusScalingParams iscal = iset.getScaling();

				String nodesXml = "";
				if (iset.getInstances() != null) {
					for (String nId : iset.getInstances().keySet()) {
						OccopusNode node = iset.getInstances().get(nId);
						String nodeXml = "<node>" + "<id>" + nId + "</id>" + "<ip>" + node.getResource_address()
								+ "</ip>" + "<state>" + node.getState() + "</state>" + "</node>";
						nodesXml = nodesXml + nodeXml;
					}

					isetsXml = isetsXml + "<instanceset>" + "<name>" + k + "</name>" + "<scaling>" + "<min>"
							+ iscal.getMin() + "</min>" + "<max>" + iscal.getMax() + "</max>" + "<actual>"
							+ iscal.getActual() + "</actual>" + "<target>" + iscal.getTarget() + "</target>"
							+ "</scaling>" + nodesXml + "</instanceset>";
				}
			}
		}

		return "<infraTemplateId>" + infra.getInfrastructureTemplate() + "</infraTemplateId>" + "\n" + isetsXml;

	}

	// TODO: use jaxb serialization
	private String getNodeBody(Node node) {
		return "<nodeTemplateId>" + node.getNodeTemplate().getRefId() + "</nodeTemplateId>" + "\n" + "<vmProviderId>"
				+ node.getVmProvider().getRefId() + "</vmProviderId>" + "\n" + "<serviceProfileId>"
				+ node.getServiceProfile().getRefId() + "</serviceProfileId>" + "\n" + "<resourceTemplateId>"
				+ node.getResourceTemplate().getRefId() + "</resourceTemplateId>" + "\n" + "<status>" + node.getStatus()
				+ "</status>" + "\n" + "<hostname>" + node.getHostname() + "</hostname>";
	}

	@Override
	public void addNode(Node node) {
		GenericResource a = new GenericResource();

		// List<String> b = new LinkedList<>();
		// String scope = ScopeProvider.instance.get();
		// b.add(scope);
		a.newProfile().name(node.getId()).type("FHN-nodes").description("Node deployed to Provider "
				+ node.getVmProvider().getRefId() + "with serviceProfile" + node.getServiceProfile().getRefId());
		// a.profile().newBody().setAttribute("nodeTemplate",
		// node.getNodeTemplate().getRefId());

		// String id = UUID.randomUUID().toString();

		a.profile().newBody(this.getNodeBody(node));
		try {
			publishScopedResource(a, Arrays.asList(new String[] { ScopeProvider.instance.get() }));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// this.getAllNodes().add(node);
	}

	public void addNodeTemplate(NodeTemplate nt) {
		GenericResource res = new GenericResource();
		res.newProfile().name("node-template-" + nt.getId()).type("NodeTemplate").description(nt.getDescription());
		res.profile()
				.newBody("" + "<scriptURL>" + nt.getScript().toString() + "</scriptURL>" + "<osTemplateId>"
						+ nt.getOsTemplateId() + "</osTemplateId>" + "<serviceProfileId>"
						+ nt.getServiceProfile().getRefId() + "</serviceProfileId>");

		try {
			publishScopedResource(res, Arrays.asList(new String[] { ScopeProvider.instance.get() }));
		} catch (RegistryNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String encodeMultilineString(String in) {
		//String in2 = in.replaceAll("occi_Flavor:", "occi_Flavor");
		return in.replaceAll("\\n", "\\$%\\$");
	}

	private String decodeMultilenString(String in) {
		//String in2 = in.replaceAll("occi_Flavor", "occi_Flavor:");
		return in.replaceAll("\\$%\\$", "\n");
	}

	public void addInfraTemplate(OccopusInfrastructureTemplate it) {
		GenericResource res = new GenericResource();
		res.newProfile().name("occopusInfraTemplate" + UUID.randomUUID()).type("OccopusTemplates");
		res.profile()
				.newBody("<description>" + this.encodeMultilineString(it.getOccopusDescription()) + "</description>");
		try {
			publishScopedResource(res, Arrays.asList(new String[] { ScopeProvider.instance.get() }));
		} catch (RegistryNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void addServiceProfile(ServiceProfile sp) {
		GenericResource gr = new GenericResource();
		gr.newProfile().name("gCubeSmartExecutor" + UUID.randomUUID()).description("gCubeSmartExecutor details")
				.type("ServiceProfile");
		gr.profile()
				.newBody("<deployedSoftware>" + sp.getDeployedSoftware() + "</deployedSoftware>" + "\n"
						+ "<description>" + sp.getDescription() + "</description>" + "\n" + "<creationDate>"
						+ sp.getCreationDate() + "</creationDate>" + "\n" + "<version>" + sp.getVersion() + "</version>"
						+ "\n" + "<minRam>" + sp.getMinRam() + "</minRam>" + "\n" + "<minCores>" + sp.getMinCores()
						+ "</minCores>" + "\n" + "<suggestedRam>" + sp.getSuggestedRam() + "</suggestedRam>" + "\n"
						+ "<suggestedCores>" + sp.getSuggestedRam() + "</suggestedCores>" + "\n");

		try {
			publishScopedResource(gr, Arrays.asList(new String[] { ScopeProvider.instance.get() }));
		} catch (RegistryNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void addVMProvider(VMProvider vmp) {
		GenericResource gr = new GenericResource();
		gr.newProfile().name("FedCloud-Cesnet-Metacloud" + UUID.randomUUID())
				.description("Cesnet-Metacloud provider profile").type("VMProviders");
		gr.profile()
				.newBody("<name>" + vmp.getName() + "</name>" + "\n" + "<resourceTemplates>"
						+ vmp.getResourceTemplates() + "</resourceTemplates>" + "\n" + "<nodeTemplateId>"
						+ vmp.getNodeTemplates().iterator().next().getRefId() + "</nodeTemplateId>" + "\n"
						+ "<endpoint>" + vmp.getEndpoint() + "</endpoint>" + "\n");

		try {
			publishScopedResource(gr, Arrays.asList(new String[] { ScopeProvider.instance.get() }));
		} catch (RegistryNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void addInfra(OccopusInfrastructure infra) {
		GenericResource a = new GenericResource();
		// List<String> b = new LinkedList<>();
		// String scope = ScopeProvider.instance.get();
		// b.add(scope);

		a.newProfile().name(infra.getId()).type("OccopusInfras");
		a.profile().newBody(this.getInfraBody(infra));

		// a.profile().newBody().setAttribute("nodeTemplate",
		// node.getNodeTemplate().getRefId());

		// String id = UUID.randomUUID().toString();
		//
		// a.profile().newBody("<nodeTemplateId>" +
		// node.getNodeTemplate().getRefId() + "</nodeTemplateId>" + "\n"
		// + "<vmProviderId>" + node.getVmProvider().getRefId() +
		// "</vmProviderId>" + "\n" + "<serviceProfileId>"
		// + node.getServiceProfile().getRefId() + "</serviceProfileId>" + "\n"
		// + "<resourceTemplateId>"
		// + node.getResourceTemplate().getRefId() + "</resourceTemplateId>" +
		// "\n" + "<status>" + node.getStatus()
		// + "</status>" + "\n" + "<hostname>" + node.getHostname() +
		// "</hostname>");
		// try {
		// publishScopedResource(a, b);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// this.getAllNodes().add(node);
		try {
			publishScopedResource(a, Arrays.asList(new String[] { ScopeProvider.instance.get() }));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	public Node getNodeById(String nodeId) {
		GenericResource r = this.getGenericResourceByName(nodeId);

		if (r != null) {
			return this.convertNode(r);
		} else {
			return null;
		}
	}

	private GenericResource getGenericResourceByName(String name) {
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/Name/text() eq '" + name + "'").setResult("$resource");
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		List<GenericResource> ds = client.submit(query);
		if (ds.isEmpty()) {
			return null;
		}

		if (ds.size() > 1) {
			LOGGER.warn("Multiple generic resources found with Name=" + name + "!!! Returning the first one");
		}
		return ds.get(0);
	}

	private GenericResource getGenericResourceById(String id) {
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/ID/text() eq '" + id + "'").setResult("$resource");
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		List<GenericResource> ds = client.submit(query);
		LOGGER.debug("Found " + ds.size() + " results");
		if (ds.isEmpty()) {
			return null;
		}

		if (ds.size() > 1) {
			LOGGER.warn("Multiple generic resources found with ID=" + id + "!!! Returning the first one");
		}
		return ds.get(0);
	}

	@Override
	public OccopusInfrastructure getInfrastructureById(String infraId) {
		OccopusInfrastructure out = new OccopusInfrastructure();
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/Name/text() eq '" + infraId + "'").setResult("$resource");
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		List<GenericResource> ds = client.submit(query);
		for (GenericResource a : ds) {
			out = this.convertInfra(a);
		}

		return out;
	}

	@Override
	public String setD4ScienceOccopusAuth() throws Exception {
		File d = null;
		this.connectorFactory = new ConnectorFactory();

		SimpleQuery query2 = queryFor(ServiceEndpoint.class);
		query2.addCondition("$resource/Profile/Name/text() eq '" + "occopusD4ScienceOccopusAuth" + "'").setResult("$resource");
		DiscoveryClient<ServiceEndpoint> client2 = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> df = client2.submit(query2);
		for (ServiceEndpoint b : df) {
			String proxy = (b.profile().accessPoints().iterator().next().propertyMap().get("encodedCredentials")
					.value());

			String certPath = StringEncrypter.getEncrypter().decrypt(proxy);
			certPath = this.connectorFactory.adaptIS(certPath);
			certPath = this.connectorFactory.adaptCert(certPath);
			certPath = this.connectorFactory.removeHeader(certPath);

			File f = new File("/tmp/temp");
			if (!f.exists()) {
				f.createNewFile();
				// Clear all permissions for all users
				f.setReadable(false, false);
				f.setWritable(false, false);
				f.setExecutable(false, false);

				f.setReadable(true, true); // Only the owner can read
				f.setWritable(true, true); // Only the owner can write

			}
			FileWriter fw = new FileWriter(f.getPath());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(certPath);
			bw.flush();
			bw.close();

			String c = this.connectorFactory.generateSecondLevelProxy(f.getPath(),
					b.profile().accessPoints().iterator().next().propertyMap().get("vo").value());
			d = new File(c);
			d.renameTo(new File("/tmp/occopusCert"));
		}

		return d.getPath();

	}

	
	
	
	@Override
	public String setFedCloudOccopusAuth() throws Exception {
		File d = null;
		this.connectorFactory = new ConnectorFactory();

		SimpleQuery query2 = queryFor(ServiceEndpoint.class);
		query2.addCondition("$resource/Profile/Name/text() eq '" + "occopusFedCloudOccopusAuth" + "'").setResult("$resource");
		DiscoveryClient<ServiceEndpoint> client2 = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> df = client2.submit(query2);
		for (ServiceEndpoint b : df) {
			String proxy = (b.profile().accessPoints().iterator().next().propertyMap().get("encodedCredentials")
					.value());

			String certPath = StringEncrypter.getEncrypter().decrypt(proxy);
			certPath = this.connectorFactory.adaptIS(certPath);
			certPath = this.connectorFactory.adaptCert(certPath);
			certPath = this.connectorFactory.removeHeader(certPath);

			File f = new File("/tmp/temp2");
			if (!f.exists()) {
				f.createNewFile();
				// Clear all permissions for all users
				f.setReadable(false, false);
				f.setWritable(false, false);
				f.setExecutable(false, false);

				f.setReadable(true, true); // Only the owner can read
				f.setWritable(true, true); // Only the owner can write

			}
			FileWriter fw = new FileWriter(f.getPath());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(certPath);
			bw.flush();
			bw.close();

			String c = this.connectorFactory.generateSecondLevelProxy(f.getPath(),
					b.profile().accessPoints().iterator().next().propertyMap().get("vo").value());
			d = new File(c);
			d.renameTo(new File("/tmp/occopusCert2"));
		}

		return d.getPath();

	}

	
	
	
	
	
	
	@Override
	public Node getNodeByURI(String UriId) {
		Node out = new Node();
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/Name/text() eq '" + UriId + "'").setResult("$resource");
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		List<GenericResource> ds = client.submit(query);
		for (GenericResource a : ds) {
			out = this.convertNode(a);
			System.out.println(out.getId());
		}
		return out;
	}

	@Override
	public String getNodeNameById(String Id) {
		String b = new String();
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/ID/text() eq '" + Id + "'").setResult("$resource/Profile/Name/text()");
		DiscoveryClient<String> client = client();
		List<String> ds = client.submit(query);
		for (String a : ds) {
			b = a;
		}
		return b;

	}

	@Override
	public void deleteInfrastructure(String infra) {
		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/SecondaryType/text() eq 'OccopusInfras'").setResult("$resource");
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		List<GenericResource> ds = client.submit(query);
		for (GenericResource a : ds) {
			if (a.profile().name().equals(infra)) {
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
	public void deleteHostingNode(String hostname) {

		SimpleQuery query = queryFor(HostingNode.class);
		// query.addCondition("$resource/Profile/GHNDescription/Name/text() eq
		// '" + hostname+"*" + "'").setResult("$resource");
		// query.addCondition("$resource/Profile/GHNDescription/Name/text() eq
		// '" + hostname+":80" + "'").setResult("$resource");
		DiscoveryClient<HostingNode> client = clientFor(HostingNode.class);
		List<HostingNode> ds = client.submit(query);
		for (HostingNode a : ds) {
			if (a.profile().description().name().startsWith(hostname)) {
				// if (a.profile().description().name().equals(hostname+":80"))
				// {
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
	
		// for each VMProvider
		for (VMProvider a : this.getAllVMProviders()) {
			try{
			this.connectorFactory = new ConnectorFactory();
			FHNConnector connector = this.connectorFactory.getConnector(a);
		
			connector.connect();
			// for all VMs on that provider
			Collection<VM> listvm = connector.listVM();
			for (VM vm : listvm) {
				// get status from the vm
				String vmStatus = vm.getStatus();
				// retrive the hostname from the vm's networks. If multiple
				// networks are
				// present, the latest is used
				String hostname = "";
				for (VMNetwork vmn : vm.getNetworks()) {
					if(InetAddress.getByName(vmn.getAddress()).isSiteLocalAddress()){
						continue;
					}
					hostname = InetAddress.getByName(vmn.getAddress()).getHostName();
				}
				
				// retrieve the corresponding node on the is
				Node isnode = this.getNodeById(NodeHelper.createNodeId(a.getId(), vm.getEndpoint().toString()));
				if (isnode == null) {
					LOGGER.info("VM " + hostname + " not found on the IS. Skipping synchronization");
					continue;
				}
				// update the node and save it in the IS
				isnode.setStatus(vmStatus);
				isnode.setHostname(hostname);
				this.updateNode(isnode);
			}
		  }catch (Exception aa){
			  aa.getMessage();
			  continue;
		  }
			
		}	
		try {
			OccopusNodeDefinitionImporter od = new OccopusNodeDefinitionImporter();
			od.importer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}

	
	
	public static void main(String[] args) throws Exception {
		ScopeProvider.instance.set("/gcube/devsec");

		ISProxyImpl a = new ISProxyImpl();

		//GenericResource b = a.getGenericResourceById("1cdc9b59-a226-4c66-9c63-84824c1af141");
		// System.out.println(a.convertInfra(b).getInstanceSets().get("node1").getInstances().get("node").getResource_address());
//		System.out.println(a.convertInfra(b).getInstanceSets().get("node2").getInstances()
//				.get("0bce683d-ee42-41f0-a5c9-de460c386a63").getResource_address());
//		// String occopusServerUrl = "http://127.0.0.1:5000/";
	
		//a.updateIs();
		
		//
		// OccopusClient oc = new OccopusClient(occopusServerUrl);
		//
		// GetInfraResponse r =
		// oc.getInfrastructure("8b8f5001-08c7-48e2-b25b-929794b773e8");
		//
		//
		// OccopusInfrastructure oi = new OccopusInfrastructure();
		// oi.setInfrastructureTemplate("blabla");
		// oi.setInstanceSets(r.getInstanceSets());
		//
		// System.out.println(a.getInfraBody(oi));

		// a.getInfrastructureById("081c5af1-3ee5-4084-b97f-fec9ba0ee7b6");

		// InfrastructureTemplate it =
		// a.returnInfraTemplate("occopusInfraTemplatefb302136-4c53-4a59-949c-58b6b993b4e8");

		// System.out.println(it.getOccopusDescription());
		// System.out.println(a.getAllInfrastructures());
		// System.out.println(a.setOCCIAuth());
		// a.updateIs();
		// System.out.println(a.getNodeById("d3492201-0294-434d-a9ad-a5eb522b16c9"));
		// System.out.println(a.getNodeByURI("https://carach5.ics.muni.cz:11443/compute/73905"));
		// a.getServiceProfileById("30a2b7bd-2156-424d-ad40-0721f4e4888e");
		// a.getNodeTemplateById("05f9d5fe-e4be-4b66-ba2b-dc83b9ec9484");
		// a.getAllVMProviders();
		// a.deleteHostingNode("stoor109.meta.zcu.cz:80");
		// System.out.println(a.getAllServiceProfiles());
		// a.getVMProviderById("58d494a2-505d-4550-8d48-83ade4c2b49e");
		// a.findVMProviderbyId("58d494a2-505d-4550-8d48-83ade4c2b49e");
		// a.findVMProvidersbyServiceProfile("30a2b7bd-2156-424d-ad40-0721f4e4888e");
		// a.getNodeTemplateById("05f9d5fe-e4be-4b66-ba2b-dc83b9ec9484");
		// System.out.println(a.findVMProvidersbyServiceProfile("30a2b7bd-2156-424d-ad40-0721f4e4888e"));
		// a.getVMProviderNodeTemplates("58d494a2-505d-4550-8d48-83ade4c2b49e");
		// a.getVMProviderNodeTemplates("58d494a2-505d-4550-8d48-83ade4c2b49e");
		// a.deleteHostingNode("stoor33.meta.zcu.cz");

		// System.out.println(a.returnInfra("88a66f67-c831-4aed-ad0f-6f0e9036e17c"));

		// System.out.println(a.getNodeNameById("2cab6b1e-88a0-4f9a-a7ec-43dc9aebf13b"));
		// a.getScopes(ISProxyImpl.ctx);
		// a.onStart();
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
		// a.deleteHostingNode("stoor71.meta.zcu.cz");
		// Node node = new Node();
		// node.setId("https://carach5.ics.muni.cz:11443/compute/72965");
		// a.deleteNode(node);
		// System.out.println(a.getAllNodes());
		// System.out.println(a.getAllNodes());
		// try {
		// a.updateIs();
		// } catch (UnknownHostException | CommunicationException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//a.getAllInfrastructures();
		a.getAllInfrastructuresList();
		// a.getScopeProviders();
		// a.findNodes("30a2b7bd-2156-424d-ad40-0721f4e4888e",
		// "58d494a2-505d-4550-8d48-83ade4c2b49e");
		// System.out.println(a.findNodes("a", "b"));
		// System.out.println(a.getAllVMProviders());
		// InfrastructureTemplate aa = new InfrastructureTemplate();
		// aa.setId("occopusInfraTemplate132093e8-a6d2-4221-8371-074f426a7792");
		// System.out.println(a.returnInfra(aa));
		// a.getInfrastructureById("234d38fd-d6b8-43e9-a0a9-0d4c79dd86e3");
		// a.returnInfraTemplate("occopusInfraTemplate132093e8-a6d2-4221-8371-074f426a7792");
		// System.out.println(a.returnInfra(aa));
		// System.out.println(a.getAllVMProviders());
		// System.out.println(a.getNodeById("https://carach5.ics.muni.cz:11443/compute/73088"));
	}

}
