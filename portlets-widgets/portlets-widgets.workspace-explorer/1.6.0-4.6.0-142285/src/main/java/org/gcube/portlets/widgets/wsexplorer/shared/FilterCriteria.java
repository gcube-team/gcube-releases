/**
 *
 */
package org.gcube.portlets.widgets.wsexplorer.shared;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * The Class FilterCriteria.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jun 18, 2015
 */
public class FilterCriteria implements Serializable {


	private static final long serialVersionUID = 1912760277441333686L;
	protected List<String> allowedMimeTypes;
	protected Map<String, String> requiredProperties;
	protected List<String> allowedFileExtensions;

	/**
	 * Instantiates a new filter criteria.
	 */
	public FilterCriteria(){}


	/**
	 * Instantiates a new filter criteria.
	 *
	 * @param allowedMimeTypes the allowed mime types
	 * @param allowedFileExtensions the allowed file extensions
	 * @param requiredProperties the required gcube properties with key = value
	 */
	public FilterCriteria(List<String> allowedMimeTypes, List<String> allowedFileExtensions, Map<String, String> requiredProperties) {
		this.allowedMimeTypes = allowedMimeTypes;
		this.allowedFileExtensions = allowedFileExtensions;
		this.requiredProperties = requiredProperties;
	}

	/**
	 * Sets the allowed mime types.
	 *
	 * @param allowedMimeTypes the allowedMimeTypes to set
	 */
	public void setAllowedMimeTypes(List<String> allowedMimeTypes) {
		this.allowedMimeTypes = allowedMimeTypes;
	}


	/**
	 * Sets the required properties.
	 *
	 * @param requiredProperties the requiredProperties to set
	 */
	public void setRequiredProperties(Map<String, String> requiredProperties) {
		this.requiredProperties = requiredProperties;
	}


	/**
	 * Sets the allowed file extensions.
	 *
	 * @param allowedFileExtensions the allowedFileExtensions to set
	 */
	public void setAllowedFileExtensions(List<String> allowedFileExtensions) {
		this.allowedFileExtensions = allowedFileExtensions;
	}

	/**
	 * Gets the allowed file extensions.
	 *
	 * @return the allowed file extensions
	 */
	public List<String> getAllowedFileExtensions() {
		return allowedFileExtensions;
	}

	/**
	 * Gets the allowed mime types.
	 *
	 * @return the allowedMimeTypes
	 */
	public List<String> getAllowedMimeTypes() {
		return allowedMimeTypes;
	}

	/**
	 * Gets the required properties.
	 *
	 * @return the requiredProperties
	 */
	public Map<String, String> getRequiredProperties() {
		return requiredProperties;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FilterCriteria [allowedMimeTypes=");
		builder.append(allowedMimeTypes);
		builder.append(", requiredProperties=");
		builder.append(requiredProperties);
		builder.append(", allowedFileExtensions=");
		builder.append(allowedFileExtensions);
		builder.append("]");
		return builder.toString();
	}
}
