/**
 * 
 */
package org.gcube.data.tm;

import static org.gcube.common.core.contexts.GCUBEServiceContext.Status.*;
import static org.gcube.common.mycontainer.Utils.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.xml.namespace.QName;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.state.GCUBEWSResourceKey;
import org.gcube.common.mycontainer.Gar;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.tm.context.ServiceContext;
import org.gcube.data.tm.context.TReaderContext;
import org.gcube.data.tm.context.TWriterContext;
import org.gcube.data.tmf.api.Source;
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

/**
 * @author Fabio Simeoni
 * 
 */
public class TestUtils {

	// shared logger
	public static Logger log = LoggerFactory.getLogger("test");

	//local container coordinates
	public static final String host = "localhost";
	public static final String port = String.valueOf(DEFAULT_PORT);

	//test scope
	public static final String devsec = "/gcube/devsec";

	//service deployment
	static Gar gar() {
		return new Gar("tree-manager").addInterfaces("../wsdl").addConfigurations("../config");
	}
	
	//helper
	static void setCurrentScope(String scope) {
		ScopeProvider.instance.set(scope);
	}

	static void serviceIsReady() {
		assertTrue(ServiceContext.getContext().getStatus() == READIED);
	}

	static String queryProperties(EndpointReferenceType address) {
		try {

			String dialect = WSRFConstants.XPATH_1_DIALECT;
			String expression = "/";

			WSResourcePropertiesServiceAddressingLocator locator = new WSResourcePropertiesServiceAddressingLocator();

			QueryExpressionType query = new QueryExpressionType();
			query.setDialect(dialect);
			query.setValue(expression);

			QueryResourceProperties_PortType port = locator.getQueryResourcePropertiesPort(address);

			// client.setOptions((Stub)port);

			QueryResourceProperties_Element request = new QueryResourceProperties_Element();
			request.setQueryExpression(query);

			QueryResourcePropertiesResponse response = port.queryResourceProperties(request);

			if (response == null || response.get_any() == null || response.get_any().length == 0)
				throw new Exception("could not access ws resource");

			return AnyHelper.toSingleString(response);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	//instance references
	
	//why this wrapping? our mocks will run inside  my-container, which has its own context classloader.
	//Mockito will not use this classloader if the class to be mocked has been loaded by the app classloader.
	//It will create another one and link it to the app classloader.This creates a problem
	//when mocks are serialized, as they can no longer be deserialised:
	//         app cl
	//         /      \
	//   mock cl      container CL
	//     /                \
	// can deser.mock        cannot deser.mock
	public static <T> T serializableMock(Class<T> clazz) {
		ClassLoader current = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(clazz.getClassLoader());
		T mock = mock(clazz,withSettings().serializable());
		Thread.currentThread().setContextClassLoader(current);
		return mock;
	}
	
	public static void removeSource(Source source) throws Exception {
		
		GCUBEWSResourceKey key = key(source.id());
		if (source.reader()!=null)
				TReaderContext.getContext().getWSHome().remove(key);
		if (source.writer()!=null)
			TWriterContext.getContext().getWSHome().remove(key);
	}
	
	public static GCUBEWSResourceKey key(String sourceId) throws Exception {
		return new GCUBEWSResourceKey(new SimpleResourceKey(new QName(Constants.NS,"ResourceKey"), sourceId));
	}
	
	//sample tree with given id
	public static Tree mockTree() {
		return mockTree("__id__");
	}
	
	//sample tree
	public static Tree mockTree(String id) {
		Tree t = serializableMock(Tree.class);
		when(t.id()).thenReturn(id);
		return t;
	}
}
