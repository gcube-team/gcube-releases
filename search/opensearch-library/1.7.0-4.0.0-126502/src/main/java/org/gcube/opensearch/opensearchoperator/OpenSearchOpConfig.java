package org.gcube.opensearch.opensearchoperator;

import java.util.HashMap;
import java.util.Map;

import org.gcube.opensearch.opensearchlibrary.OpenSearchConstants;
import org.gcube.opensearch.opensearchlibrary.queryelements.BasicQueryElementFactory;
import org.gcube.opensearch.opensearchlibrary.urlelements.BasicURLElementFactory;
import org.gcube.opensearch.opensearchlibrary.utils.FactoryClassNamePair;
import org.gcube.opensearch.opensearchoperator.resource.ISOpenSearchResourceCache;
import org.gcube.opensearch.opensearchoperator.resource.ISResourceRepository;
import org.gcube.opensearch.opensearchoperator.resource.LocalResourceRepository;
import org.gcube.opensearch.opensearchoperator.resource.OpenSearchResource;

/**
 * Configuration class parameterizing the operation of the OpenSearchOp
 * 
 * @author gerasimos.farantatos
 *
 */
public class OpenSearchOpConfig {
	public static final String dirPathDef = System.getenv("HOME") + "/workspace_RR/OpenSearch_Library/src/main/resources"; //TODO fix
	public static final String schemaPathDef = System.getenv("HOME") + "/workspace_RR/OpenSearch_Library/src/main/resources/Schema/OpenSearchResource.xsd";
	/**
	 * The default number of results requested per page
	 */
	public static final Integer resultsPerPageDef = 100;
	/**
	 * The default value for the sequentialResults configuration parameter
	 */
	public static final Boolean sequentialResultsDef = false;
	/**
	 * The default value for {@link OpenSearchOpConfig#useLocalResourceRepository}
	 */
	public static final Boolean useLocalResourceRepositoryDef = false;
	
	/**
	 * The directory to look for resources when operating in local mode
	 */
	public String dirPath = dirPathDef;
	/**
	 * The directory to look for a schema defining the OpenSearchResource validation is enabled
	 */
	public String schemaPath = schemaPathDef;
	/**
	 * Resource cache, to be further populated by {@link ISResourceRepository}.
	 * Set only if {@link this#useLocalResourceRepository} is false
	 */
	public ISOpenSearchResourceCache ISCache = null;
	/**
	 * The number of results that the operator requests for each page
	 */
	public Integer resultsPerPage = resultsPerPageDef;
	/**
	 * If false, result retrieval from brokered resources is performed by separate threads
	 */
	public Boolean sequentialResults = sequentialResultsDef;
	/**
	 * If false, {@link OpenSearchResource} retrieval is performed using an {@link ISResourceRepository}
	 * Otherwise, {@link OpenSearchResource}s are retrieved locally (either from the local filesystem or from a url) using a {@link LocalResourceRepository}
	 */
	public Boolean useLocalResourceRepository = useLocalResourceRepositoryDef;
	/**
	 * The factory class names of the implemented factories associated with the namespaces for which they provide functionality
	 * A factory associated with the standard OpenSearch namespace is expected to be always present in this mapping
	 */
	public Map<String, FactoryClassNamePair> factories = new HashMap<String, FactoryClassNamePair>();

	/**
	 * No-arg constructor. Default values are implied
	 */
	public OpenSearchOpConfig() {
		factories.put(OpenSearchConstants.OpenSearchNS,
				new FactoryClassNamePair(BasicURLElementFactory.class.getName(), 
						BasicQueryElementFactory.class.getName()));
	}
	
	/**
	 * Creates a new OpenSearchOpConfig with values overriding the defaults
	 * 
	 * @param resultsPerPage The number of results to be requested per page. If null, the default value will be used
	 * @param serializeSources true if the client wishes the results to be obtained from the OpenSearch provider serially, false
	 * otherwise. Use false to increase performance.
	 * @param useLocalResourceRespository true if an IS is not available for the retrieval and persistence of OpenSearch resources.
	 * In that case, the local FS and/or the network will be used instead. False, if an IS can be used.
	 * @param factories The factory class names of the implemented factories associated with the namespaces for which they provide functionality
	 */
	public OpenSearchOpConfig(Integer resultsPerPage, Boolean serializeSources, Boolean useLocalResourceRespository, Map<String, FactoryClassNamePair> factories) {
		if(resultsPerPage != null)
			this.resultsPerPage = resultsPerPage;
		if(serializeSources != null)
			this.sequentialResults = serializeSources;
		if(useLocalResourceRespository != null)
			this.useLocalResourceRepository = useLocalResourceRespository;
		this.factories.put(OpenSearchConstants.OpenSearchNS,
				new FactoryClassNamePair(BasicURLElementFactory.class.getName(), 
						BasicQueryElementFactory.class.getName()));
		if(factories != null)
			this.factories.putAll(factories);
	}
	
}

