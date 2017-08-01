/**
 * 
 */
package org.gcube.portlets.widgets.lighttree.client;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class FilterCriteria implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6379862643017586699L;
	
	protected List<String> allowedMimeTypes;
	protected Map<String, String> requiredProperties;
	
	public FilterCriteria(){}

	/**
	 * @param allowedMimeTypes
	 * @param requiredProperties
	 */
	public FilterCriteria(List<String> allowedMimeTypes,
			Map<String, String> requiredProperties) {
		this.allowedMimeTypes = allowedMimeTypes;
		this.requiredProperties = requiredProperties;
	}

	/**
	 * @return the allowedMimeTypes
	 */
	public List<String> getAllowedMimeTypes() {
		return allowedMimeTypes;
	}

	/**
	 * @return the requiredProperties
	 */
	public Map<String, String> getRequiredProperties() {
		return requiredProperties;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FilterCriteria [allowedMimeTypes=");
		builder.append(allowedMimeTypes);
		builder.append(", requiredProperties=");
		builder.append(requiredProperties);
		builder.append("]");
		return builder.toString();
	}
}
