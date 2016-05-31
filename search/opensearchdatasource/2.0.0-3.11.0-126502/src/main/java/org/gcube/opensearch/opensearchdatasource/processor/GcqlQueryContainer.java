package org.gcube.opensearch.opensearchdatasource.processor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A class that contains all the data needed by a specific
 * resource to answer a query. The data depend on the technology,
 * that the resource was implemented in.
 * 
 *
 */
abstract public class GcqlQueryContainer {
	
	public GcqlQueryContainer(Map<String, String> projectedFields) {
		this.projectedFields = projectedFields;
	}

	/**
	 * a list of the fields that are the projections of the query
	 */
	protected Map<String, String> projectedFields = new LinkedHashMap<String, String>();

	public Map<String, String> getProjectedFields() {
		return projectedFields;
	}

	public void setProjectedFields(Map<String, String> projectedFields) {
		this.projectedFields = projectedFields;
	}

}
