package org.gcube.indexmanagement.gcqlwrapper;

import java.util.LinkedHashMap;

/**
 * A class that contains all the data needed by a specific
 * resource to answer a query. The data depend on the technology,
 * that the resource was implemented in.
 * 
 * @author bill
 *
 */
abstract public class GcqlQueryContainer {
	
	public GcqlQueryContainer(LinkedHashMap<String, String> projectedFields) {
		super();
		this.projectedFields = projectedFields;
	}

	/**
	 * a list of the fields that are the projections of the query
	 */
	protected LinkedHashMap<String, String> projectedFields = new LinkedHashMap<String, String>();

	public LinkedHashMap<String, String> getProjectedFields() {
		return projectedFields;
	}

	public void setProjectedFields(LinkedHashMap<String, String> projectedFields) {
		this.projectedFields = projectedFields;
	}

}
