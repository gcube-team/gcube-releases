/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.shared;

import java.io.Serializable;

import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 24, 2014
 *
 */
public class TemplateExpression implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3824676236321835022L;
	
	
	private String humanDescription;
	private String serverExpression;
	private C_Expression clientExpression;

	
	/**
	 * 
	 */
	public TemplateExpression() {
	}
	
	/**
	 *  Used by Server
	 * @param expression is server expression toString()
	 * @param humanDescription
	 */
	public TemplateExpression(String serverExpression, String humanDescription) {
		this.serverExpression = serverExpression;
		this.humanDescription = humanDescription;
	}
	
	/**
	 * Used by Client
	 * @param expression
	 * @param humanDescription
	 */
	public TemplateExpression(C_Expression expression, String humanDescription) {
		this.clientExpression = expression;
		this.humanDescription = humanDescription;
	}


	public String getHumanDescription() {
		return humanDescription;
	}

	public void setHumanDescription(String humanDescription) {
		this.humanDescription = humanDescription;
	}

	public String getServerExpression() {
		return serverExpression;
	}

	public void setServerExpression(String serverExpression) {
		this.serverExpression = serverExpression;
	}

	public C_Expression getClientExpression() {
		return clientExpression;
	}

	public void setClientExpression(C_Expression clientExpression) {
		this.clientExpression = clientExpression;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TemplateExpression [humanDescription=");
		builder.append(humanDescription);
		builder.append(", serverExpression=");
		builder.append(serverExpression);
		builder.append(", clientExpression=");
		builder.append(clientExpression);
		builder.append("]");
		return builder.toString();
	}

}
