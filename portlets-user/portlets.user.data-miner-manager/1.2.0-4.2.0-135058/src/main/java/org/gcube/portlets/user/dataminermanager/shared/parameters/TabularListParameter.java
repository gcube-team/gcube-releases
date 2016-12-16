/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.shared.parameters;

import java.util.ArrayList;

/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TabularListParameter extends Parameter{

	private static final long serialVersionUID = -1786477950530892502L;
	private String separator;
	private ArrayList<String> templates = new ArrayList<String>();
	private String defaultMimeType;
	private ArrayList<String> supportedMimeTypes;

	// private List<String> tableNames = new ArrayList<String>();

	public TabularListParameter() {
		super();
		this.typology = ParameterType.TABULAR_LIST;
	}

	/**
	 * @param defaultValue
	 * @param value
	 */
	public TabularListParameter(String name, String description,
			String separator, String defaultMimeType,
			ArrayList<String> supportedMimeTypes) {
		super(name, ParameterType.TABULAR_LIST, description);
		this.separator = separator;
		this.defaultMimeType = defaultMimeType;
		this.supportedMimeTypes = supportedMimeTypes;
	}

	/**
	 * @return the separator
	 */
	public String getSeparator() {
		return separator;
	}

	/**
	 * @param templates
	 *            the templates to set
	 */
	public void setTemplates(ArrayList<String> templates) {
		this.templates = templates;
	}

	/**
	 * @return the templates
	 */
	public ArrayList<String> getTemplates() {
		return templates;
	}

	public void addTemplate(String template) {
		templates.add(template);
	}

	public String getDefaultMimeType() {
		return defaultMimeType;
	}

	public void setDefaultMimeType(String defaultMimeType) {
		this.defaultMimeType = defaultMimeType;
	}

	public ArrayList<String> getSupportedMimeTypes() {
		return supportedMimeTypes;
	}

	public void setSupportedMimeTypes(ArrayList<String> supportedMimeTypes) {
		this.supportedMimeTypes = supportedMimeTypes;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	@Override
	public String toString() {
		return "TabularListParameter [value=" + value + ", separator="
				+ separator + ", templates=" + templates + ", defaultMimeType="
				+ defaultMimeType + ", supportedMimeTypes="
				+ supportedMimeTypes + ", name=" + name + ", description="
				+ description + ", typology=" + typology + "]";
	}

	

}
