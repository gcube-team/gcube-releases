package org.gcube.opensearch.opensearchdatasource.processor;

import java.util.HashSet;
import java.util.Set;

import org.gcube.opensearch.opensearchlibrary.OpenSearchConstants;

public class OpenSearchParameterContainer {
	private Set<String> parameters = new HashSet<String>();
	
	public OpenSearchParameterContainer() { }
	
	public void add(String parameter) throws Exception {
		if(parameter == null)
			parameter = OpenSearchConstants.OpenSearchNS;
		if(parameters.contains(parameter))
			throw new Exception("Duplicate OpenSearch parameter in query");
		parameters.add(parameter);
	}
	
}
