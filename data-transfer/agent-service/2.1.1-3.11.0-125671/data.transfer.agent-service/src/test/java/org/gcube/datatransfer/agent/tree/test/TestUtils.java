package org.gcube.datatransfer.agent.tree.test;

import static org.gcube.data.tml.Constants.writerWSDDName;
import static org.gcube.data.tml.Constants.readerWSDDName;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axis.message.MessageElement;
import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.message.addressing.ReferencePropertiesType;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScopeManager;
import org.gcube.common.core.state.GCUBEWSResourceKey;
import org.gcube.common.mycontainer.Gar;
import org.gcube.common.mycontainer.MyContainer;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.tm.stubs.SourceBinding;
import org.gcube.data.tml.Constants;
import org.gcube.data.tr.requests.BindSource;
import org.gcube.data.tr.requests.Mode;
import org.gcube.data.trees.data.Edge;
import org.gcube.data.trees.data.InnerNode;
import org.gcube.data.trees.data.Tree;
import org.globus.wsrf.WSRFConstants;
import org.globus.wsrf.impl.SimpleResourceKey;
import org.globus.wsrf.utils.AnyHelper;
import org.oasis.wsrf.properties.QueryExpressionType;
import org.oasis.wsrf.properties.QueryResourcePropertiesResponse;
import org.oasis.wsrf.properties.QueryResourceProperties_Element;
import org.oasis.wsrf.properties.QueryResourceProperties_PortType;
import org.oasis.wsrf.properties.WSResourcePropertiesServiceAddressingLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class TestUtils {

	// shared logger
	public static Logger log = LoggerFactory.getLogger("test");

	public static final String HOST = "localhost";
	public static final String PORT = String.valueOf(9999);
	public static final String URI = "http://" + HOST + ":" + PORT;

	public static final String SOURCE_ID = "from_portlet";
	public static final String PLUGIN = "tree-repository";

	public static Gar SERVICE_GAR() {
		return new Gar("tree-manager").addInterfaces("../wsdl")
				.addConfigurations("../config");
	}

	public static final GCUBEScope INFRA = GCUBEScope.getScope("/gcube");
	public static final GCUBEScope VO = GCUBEScope.getScope("/gcube/devsec");
	public static final GCUBEScope VRE1 = GCUBEScope
			.getScope("/gcube/devsec/VRE1");
	public static final GCUBEScope VRE2 = GCUBEScope
			.getScope("/gcube/devsec/VRE2");



	public static File storageDirectory(MyContainer container) {
		return new File(container.storageLocation(), Constants.gcubeName);
	}

	public static File persistentPluginProfile(MyContainer container) {
		return new File(storageDirectory(container), "plugins/" + PLUGIN
				+ ".profile");
	}

	
//	public static BindingParameters bindParametersFor(BindSource request) {
//		return bindParametersFor(request,false);	
//	}
//	
//	public static BindingParameters bindParametersFor(BindSource request, boolean broadcast) {
//		//return new BindingParameters(PLUGIN, request. broadcast);	
//		Document payload = null;
//		try {
//			payload = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
//		} catch (ParserConfigurationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return new BindingParameters(PLUGIN,payload.createElement("foo"),broadcast);
//	}
	
	public static void bindingsMatchRequest(BindSource request, List<SourceBinding> bindings) {

		assertEquals(1, bindings.size());

		SourceBinding binding = bindings.get(0);

		log.debug("source: " + binding.getSourceID());
		log.debug("reader: " + binding.getReaderEndpoint());
		log.debug("writer: " + binding.getWriterEndpoint());

		assertNotNull(binding.getSourceID());

		if (request.mode() == Mode.FULLACESSS
				|| request.mode() == Mode.READABLE)
			assertNotNull(binding.getReaderEndpoint());
		if (request.mode() == Mode.FULLACESSS
				|| request.mode() == Mode.WRITABLE)
			assertNotNull(binding.getWriterEndpoint());
	}


	public static EndpointReferenceType getReader(String... key) throws Exception {
		return endpoint(readerWSDDName, key);
	}

	public static EndpointReferenceType getWriter() throws Exception {
		return endpoint(writerWSDDName);
	}

	public static GCUBEWSResourceKey key(EndpointReferenceType endpoint)
			throws Exception {

		MessageElement element = endpoint.getProperties().get(
				new QName(Constants.binderWSDDName, "ResourceKey"));
		return new GCUBEWSResourceKey(new SimpleResourceKey(element,
				String.class));
	}
	


	public static SourceBinding callBinderAndBroadCastWith(BindSource request)
			throws Exception {

		return callBinderWith(request, false);
	}
	
	public static SourceBinding callBinderWith(BindSource request)
			throws Exception {

		return callBinderWith(request, false);
	}
	
	public static SourceBinding callBinderWith(BindSource request, boolean broadcast)
			throws Exception {

//		TBinderClient client = new BinderClient(new URL(URI));
		//List<SourceBinding> bindings = client.bind(bindParametersFor(request,broadcast));

		//bindingsMatchRequest(request, bindings);

		//return bindings.get(0);
		return new SourceBinding();
	}

	public static EndpointReferenceType endpoint(String serviceName,String... k) {
		try {
			EndpointReferenceType epr = new EndpointReferenceType();
			epr.setAddress(new AttributedURI(URI + "/wsrf/services/" + serviceName));
			ReferencePropertiesType props = new ReferencePropertiesType();
			SimpleResourceKey key = new SimpleResourceKey(new QName(Constants.binderWSDDName,
					"ResourceKey"), k.length == 0 ? SOURCE_ID : k[0]);
			AnyHelper.setAny(props, key.toSOAPElement());
			epr.setProperties(props);
			return epr;
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	static public void setCurrentScope(GCUBEScope scope) {
	//	GCUBEScopeManager.DEFAULT.setScope(scope);
		ScopeProvider.instance.set(scope.toString());
	}
	
	
	public static  Tree getTreeSample() {
		Tree tree = new Tree();
		
		tree.add(new Edge(new QName("test"),new InnerNode()));
		
		tree.add(new Edge(new QName("test2"),new InnerNode()));
		
		return tree;
		
	}

	
	static public String queryProperties(EndpointReferenceType endpoint) {
		
		try {
			
			String dialect = WSRFConstants.XPATH_1_DIALECT;
			String expression = "/";

			WSResourcePropertiesServiceAddressingLocator locator = new WSResourcePropertiesServiceAddressingLocator();
	
			QueryExpressionType query = new QueryExpressionType();
			query.setDialect(dialect);
			query.setValue(expression);
	
			QueryResourceProperties_PortType port = locator
					.getQueryResourcePropertiesPort(endpoint);
	
			// client.setOptions((Stub)port);
	
			QueryResourceProperties_Element request = new QueryResourceProperties_Element();
			request.setQueryExpression(query);
	
			QueryResourcePropertiesResponse response = port
					.queryResourceProperties(request);
	
			if (response == null || response.get_any() == null
					|| response.get_any().length == 0)
				throw new Exception("could not access ws resource");
			
			return AnyHelper.toSingleString(response);
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}
