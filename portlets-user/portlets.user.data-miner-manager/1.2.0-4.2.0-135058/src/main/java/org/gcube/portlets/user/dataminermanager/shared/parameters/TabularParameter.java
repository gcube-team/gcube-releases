/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.shared.parameters;

import java.util.ArrayList;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class TabularParameter extends Parameter {

	private static final long serialVersionUID = 8038591467145151553L;
	private ArrayList<String> templates = new ArrayList<String>();
	private String defaultMimeType;
	private ArrayList<String> supportedMimeTypes;

	/**
	 * 
	 */
	public TabularParameter() {
		super();
		this.typology = ParameterType.TABULAR;
	}

	/**
	 * 
	 * @param name
	 * @param description
	 * @param tableName
	 */
	public TabularParameter(String name, String description, String tableName,
			String defaultMimeType, ArrayList<String> supportedMimeTypes) {
		super(name, ParameterType.TABULAR, description);
		this.value = tableName;
		this.templates = null;
		this.defaultMimeType = defaultMimeType;
		this.supportedMimeTypes = supportedMimeTypes;
	}

	/**
	 * 
	 * @param name
	 * @param description
	 * @param tableName
	 * @param templates
	 */
	public TabularParameter(String name, String description, String tableName,
			ArrayList<String> templates, String defaultMimeType,
			ArrayList<String> supportedMimeTypes) {
		super(name, ParameterType.TABULAR, description);
		this.value = tableName;
		this.templates = templates;
		this.defaultMimeType = defaultMimeType;
		this.supportedMimeTypes = supportedMimeTypes;
	}

	public String getTableName() {
		return value;
	}

	public void setTableName(String tableName) {
		this.value = tableName;
	}

	public ArrayList<String> getTemplates() {
		return templates;
	}

	public void setTemplates(ArrayList<String> templates) {
		this.templates = templates;
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

	@Override
	public String toString() {
		return "TabularParameter [tableName=" + value + ", templates="
				+ templates + ", defaultMimeType=" + defaultMimeType
				+ ", supportedMimeTypes=" + supportedMimeTypes + ", name="
				+ name + ", description=" + description + ", typology="
				+ typology + "]";
	}

}
