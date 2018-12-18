/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.event.operation;

import org.gcube.portlets.user.tdtemplate.shared.TemplateExpression;

import com.google.gwt.event.shared.GwtEvent;


/**
 * The Class TableRuleOperationEvent.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 25, 2015
 */
public class TableRuleOperationEvent extends GwtEvent<TableRuleOperationEventHandler>  {
	
	public static final GwtEvent.Type<TableRuleOperationEventHandler> TYPE = new Type<TableRuleOperationEventHandler>();
	private TemplateExpression templateExpression;

	/**
	 * Instantiates a new table rule operation event.
	 *
	 * @param expression the expression
	 */
	public TableRuleOperationEvent(TemplateExpression expression) {
		this.templateExpression = expression;
	}

	/**
	 * Gets the template expression.
	 *
	 * @return the templateExpression
	 */
	public TemplateExpression getTemplateExpression() {
		return templateExpression;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<TableRuleOperationEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	/* (non-Javadoc)
	 * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
	 */
	@Override
	protected void dispatch(TableRuleOperationEventHandler handler) {
		handler.onAddTableRule(this);
	}
}
