/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.event.operation;

import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.ColumnDefinitionView;
import org.gcube.portlets.user.tdtemplate.shared.TemplateExpression;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 8, 2014
 *
 */
public class AddColumnOperationEvent extends GwtEvent<AddColumnOperationEventHandler>  {
	
	public static final GwtEvent.Type<AddColumnOperationEventHandler> TYPE = new Type<AddColumnOperationEventHandler>();
	private ColumnDefinitionView column;
	private TemplateExpression templateExpression;

	/**
	 * @return the column
	 */
	public ColumnDefinitionView getColumn() {
		return column;
	}

	/**
	 * @param column
	 * @param expression 
	 */
	public AddColumnOperationEvent(ColumnDefinitionView column, TemplateExpression expression) {
		this.column = column;
		this.templateExpression = expression;
	}

	/**
	 * @return the templateExpression
	 */
	public TemplateExpression getTemplateExpression() {
		return templateExpression;
	}

	@Override
	public Type<AddColumnOperationEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(AddColumnOperationEventHandler handler) {
		handler.onAddColumnOperation(this);
	}
}
