/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.shared;

import java.io.Serializable;
import java.util.List;

import org.gcube.portlets.user.tdtemplate.shared.validator.ViolationDescription;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 8, 2014
 *
 */
public class TdTTemplateType implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3978657302027370463L;
	
	protected String name;
	protected String id;
	protected List<ViolationDescription> constraintDescription;
	
	public TdTTemplateType(){}
	
	public TdTTemplateType(String id, String name, List<ViolationDescription> constraints){
		this.id = id;
		this.name = name;
		this.constraintDescription = constraints;
	}

	/**
	 * @return the label
	 */
	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLabel(String label) {
		this.name = label;
	}

	public List<ViolationDescription> getConstraintDescription() {
		return constraintDescription;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setConstraintDescription(
			List<ViolationDescription> constraintDescription) {
		this.constraintDescription = constraintDescription;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TdTTemplateType [name=");
		builder.append(name);
		builder.append(", id=");
		builder.append(id);
		builder.append(", constraintDescription=");
		builder.append(constraintDescription);
		builder.append("]");
		return builder.toString();
	}
	
}
