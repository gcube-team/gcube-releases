/**
 * 
 */
package org.gcube.portlets.widgets.workspacesharingwidget.shared;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Mar 6, 2015
 */
public class ReportAssignmentACL implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7218122043660957432L;
	
	private List<String> validLogins;
	private List<String> errors;
	private String aclType;
	
	/**
	 * 
	 */
	public ReportAssignmentACL() {
	}

	/**
	 * @return the validLogins
	 */
	public List<String> getValidLogins() {
		return validLogins;
	}

	/**
	 * @param validLogins the validLogins to set
	 */
	public void setValidLogins(List<String> validLogins) {
		this.validLogins = validLogins;
	}

	/**
	 * @return the errors
	 */
	public List<String> getErrors() {
		return errors;
	}

	/**
	 * @param errors the errors to set
	 */
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	/**
	 * @return the aclType
	 */
	public String getAclType() {
		return aclType;
	}

	/**
	 * @param aclType the aclType to set
	 */
	public void setAclType(String aclType) {
		this.aclType = aclType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ReportValidationACL [validLogins=");
		builder.append(validLogins);
		builder.append(", errors=");
		builder.append(errors);
		builder.append(", aclType=");
		builder.append(aclType);
		builder.append("]");
		return builder.toString();
	}
}
