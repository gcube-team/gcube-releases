/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 5, 2014
 *
 */
public class ExpressionDialogOpenedEvent extends GwtEvent<ExpressionDialogOpenedEventHandler> {

	public static final GwtEvent.Type<ExpressionDialogOpenedEventHandler> TYPE = new Type<ExpressionDialogOpenedEventHandler>();
	private int columnIndex = -1;
	private int expressionIndex;
	private ExpressionDialogType expressionDialogType;
	
	public static enum ExpressionDialogType {NEW, UPDATE}

	@Override
	public Type<ExpressionDialogOpenedEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	/**
	 * @param expressionIndex 
	 * 
	 */
	public ExpressionDialogOpenedEvent(ExpressionDialogType type, int columnIndex, int expressionIndex) {
		this.columnIndex = columnIndex;
		this.expressionIndex = expressionIndex;
		this.expressionDialogType = type;
	}

	@Override
	protected void dispatch(ExpressionDialogOpenedEventHandler handler) {
		handler.onExpressionDialogOpen(this);
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public int getExpressionIndex() {
		return expressionIndex;
	}

	public ExpressionDialogType getExpressionDialogType() {
		return expressionDialogType;
	}

}
