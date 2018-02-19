/**
 * 
 */
package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * @author ceras
 *
 */
public class TabularListParameter extends Parameter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7942229226556736598L;
	private String value;
	private String separator;
	private List<String> templates = new ArrayList<String>();
	private List<String> tableNames = new ArrayList<String>();


	public TabularListParameter() {
		super();
		this.typology = ParameterTypology.TABULAR_LIST;
	}

	/**
	 * @param defaultValue
	 * @param value
	 */
	public TabularListParameter(String name, String description, String separator) {
		super(name, ParameterTypology.TABULAR_LIST, description);
		this.separator = separator;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.bean.Parameter#setValue()
	 */
	@Override
	public void setValue(String value) {
		this.value = value;
	}
		
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.bean.Parameter#getValue()
	 */
	@Override
	public String getValue() {
		return value;
	}
	
	/**
	 * @return the separator
	 */
	public String getSeparator() {
		return separator;
	}
	
	/**
	 * @param templates the templates to set
	 */
	public void setTemplates(List<String> templates) {
		this.templates = templates;
	}
	
	/**
	 * @return the templates
	 */
	public List<String> getTemplates() {
		return templates;
	}
	
	public void addTemplate(String template) {
		templates.add(template);
	}
	

}
