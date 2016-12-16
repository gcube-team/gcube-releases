package org.gcube.opensearch.opensearchlibrary.utils;

import java.util.Collection;
import java.util.Map;

import org.gcube.opensearch.opensearchlibrary.OpenSearchConstants;
import org.gcube.opensearch.opensearchlibrary.queryelements.QueryElement;
import org.gcube.opensearch.opensearchlibrary.queryelements.QueryElementFactory;
import org.gcube.opensearch.opensearchlibrary.urlelements.URLElement;
import org.gcube.opensearch.opensearchlibrary.urlelements.URLElementFactory;

/**
 * A utility class that is used to create a pair of URL and Query element factories which respectively construct {@link URLElement} and 
 * {@link QueryElement} instances capable of processing the corresponding URL and query elements.
 * The specific classes belonging to the chain of responsibility are specified in the factory parameter
 * Note that the implementation of classes implementing functionality of a specific OpenSearch extension is not a necessary
 * condition for the operation of the library's core logic. However, the correct interpretation of extension-related parameters
 * and elements will be facilitated if all classes needed to implement the functionality of all extensions used are implemented.
 * 
 * @author gerasimos.farantatos, NKUA
 *
 */
public class FactoryResolver {

	private static Collection<String> queryNamespaces = null;
	private static Map<String, FactoryClassNamePair> factories = null;
	
	private FactoryResolver() { }
	
	/**
	 * @param queryNamespaces A list of all namespaces present in an OpenSearch query, including both the standard namespace and the namespaces of each extension, if present
	 * @param factories The mapping from namespace URIs to {@link FactoryClassNamePair} entries, which will be used to construct the factories using reflection
	 */
	public static void initialize(Collection<String> queryNamespaces, Map<String, FactoryClassNamePair> factories) {
		FactoryResolver.queryNamespaces = queryNamespaces;
		FactoryResolver.factories = factories;
	}
	
	/**
	 * 
	 * @return A {@link FactoryPair} of URL and Query element factories
	 * @throws Exception If the base-type factory class implementing standard OpenSearch functionality is not specified or the constructor of a factory class does
	 * not conform to the convention used for reflection-based instance creation (i.e. it does not specify a single constructor with a single factory parameter)
	 */
	public static FactoryPair getFactories() throws Exception {
		if(queryNamespaces == null || factories == null)
			throw new Exception("Factory resolver not initialized");
		if(!factories.containsKey(OpenSearchConstants.OpenSearchNS))
			throw new Exception("Base-type factory is not specified");
		URLElementFactory urlf = (URLElementFactory)Class.forName(factories.get(OpenSearchConstants.OpenSearchNS).urlElementFactoryClass).newInstance();
		QueryElementFactory queryf = (QueryElementFactory)Class.forName(factories.get(OpenSearchConstants.OpenSearchNS).queryElementFactoryClass).newInstance();
		
		for(String namespace : queryNamespaces) {
			if(!namespace.equals(OpenSearchConstants.OpenSearchNS) && factories.containsKey(namespace)) {
				urlf = (URLElementFactory)Class.forName(factories.get(namespace).urlElementFactoryClass).getConstructors()[0].newInstance(urlf);
				queryf = (QueryElementFactory)Class.forName(factories.get(namespace).queryElementFactoryClass).getConstructors()[0].newInstance(queryf);
			}
		}
		
		return new FactoryPair(urlf, queryf);
	}
}
