/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view.rule;

import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.UserActionInterface;
import org.gcube.portlets.user.tdtemplate.shared.TemplateExpression;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @May 5, 2014
 *
 */
public class RuleStore{
	
	protected String title;
	protected TemplateExpression expression;
	protected UserActionInterface caller;
	protected boolean isDeletable;
	protected boolean isEditable;
	
	/**
	 * 
	 * @param title
	 * @param expression
	 * @param caller
	 * @param isDeletable
	 * @param isEditable
	 */
	public RuleStore(String title, TemplateExpression expression, UserActionInterface caller, boolean isDeletable, boolean isEditable){
		this.title = title;
		this.expression = expression;
		this.caller = caller;
		this.isDeletable = isDeletable;
		this.isEditable = isEditable;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RuleStore [title=");
		builder.append(title);
		builder.append(", expression=");
		builder.append(expression);
		builder.append(", caller=");
		builder.append(caller);
		builder.append(", isDeletable=");
		builder.append(isDeletable);
		builder.append(", isEditable=");
		builder.append(isEditable);
		builder.append("]");
		return builder.toString();
	}
	
	
}
