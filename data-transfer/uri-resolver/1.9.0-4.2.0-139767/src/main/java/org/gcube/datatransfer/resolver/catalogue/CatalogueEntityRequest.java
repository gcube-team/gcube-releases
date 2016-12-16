/**
 *
 */
package org.gcube.datatransfer.resolver.catalogue;

import java.util.HashMap;
import java.util.Map;


/**
 * The Class CatalogueEntityRequest.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Dec 2, 2016
 */
public class CatalogueEntityRequest {


	private Map<String,String> parameters = new HashMap<String, String>();

	/**
	 * Instantiates a new catalogue entity request.
	 */
	public CatalogueEntityRequest() {
	}


	/**
	 * Adds the parameter to request.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public void addParameterToRequest(String key, String value) {

		this.parameters.put(key, value);

	}

	/**
	 * @return the parameters
	 */
	public Map<String, String> getParameters() {

		return parameters;
	}


	/**
	 * Gets the value of parameter.
	 *
	 * @param key the key
	 * @return the value of parameter
	 */
	public String getValueOfParameter(String key) {

		return this.parameters.get(key);
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("CatalogueEntityRequest [parameters=");
		builder.append(parameters);
		builder.append("]");
		return builder.toString();
	}



}
