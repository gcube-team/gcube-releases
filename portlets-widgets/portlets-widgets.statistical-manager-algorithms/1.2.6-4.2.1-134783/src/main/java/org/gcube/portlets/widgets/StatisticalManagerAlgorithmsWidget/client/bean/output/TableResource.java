/**
 * 
 */
package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.output;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author ceras
 *
 */
public class TableResource extends Resource implements IsSerializable {

	private static final long serialVersionUID = -1506902532089828988L;
	private String template;
	
	public TableResource() {
		super();
		this.setResourceType(ResourceType.TABULAR);
	}
	
	public TableResource(String template) {
		this();
		this.template = template;
	}

	/**
	 * @return the template
	 */
	public String getTemplate() {
		return template;
	}

	/**
	 * @param template the template to set
	 */
	public void setTemplate(String template) {
		this.template = template;
	}
	
}
