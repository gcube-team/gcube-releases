package org.gcube.portlets.user.td.expressionwidget.shared.model.leaf;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class C_ColumnReference extends C_Leaf {

	private static final long serialVersionUID = 1007646449141930835L;

	protected String id = "ColumnReference";
	protected ColumnData column;

	public C_ColumnReference() {

	}

	public C_ColumnReference(ColumnData column) {
		this.column = column;
		if (column != null) {
			this.readableExpression = "ColumnReference(" + column.getLabel()
					+ ")";
		}

	}

	@Override
	public String getId() {
		return id;
	}

	public ColumnData getColumn() {
		return column;
	}

	public void setColumn(ColumnData column) {
		this.column = column;
	}

	@Override
	public String toString() {
		return "C_ColumnReference [id=" + id + ", column=" + column + "]";
	}

}
