/**
 * 
 */
package org.gcube.data.analysis.dataminermanagercl.shared.data.output;



/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class TableResource extends Resource {

	private static final long serialVersionUID = -1506902532089828988L;
	private String template;

	public TableResource() {
		super();
		this.setResourceType(ResourceType.TABULAR);
	}

	public TableResource(String resourceId, String name, String description, String template) {
		super(resourceId, name, description, ResourceType.TABULAR);
		this.template = template;
	}

	/**
	 * @return the template
	 */
	public String getTemplate() {
		return template;
	}

	/**
	 * @param template
	 *            the template to set
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	@Override
	public String toString() {
		return "TableResource [template=" + template + ", getResourceId()="
				+ getResourceId() + ", getName()=" + getName()
				+ ", getDescription()=" + getDescription()
				+ ", getResourceType()=" + getResourceType() + "]";
	}
	
	

}
