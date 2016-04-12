package org.gcube.portlets.user.td.expressionwidget.shared.model.leaf;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class C_ColumnReferencePlaceholder extends C_Leaf {

	private static final long serialVersionUID = 275747262118236529L;
	protected String id = "ColumnReferencePlaceholder";
	protected String columnId;
	protected String label;
	protected ColumnDataType dataType;

	public C_ColumnReferencePlaceholder() {

	}

	public C_ColumnReferencePlaceholder(ColumnDataType dataType,
			String columnId, String label) {
		this.dataType = dataType;
		this.columnId = columnId;
		if (dataType != null && columnId != null && label != null) {
			this.readableExpression = "$" + label + "[" + dataType.getLabel()
					+ "]";
		}

	}

	@Override
	public String getId() {
		return id;
	}

	public String getColumnId() {
		return columnId;
	}

	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public ColumnDataType getDataType() {
		return dataType;
	}

	public void setDataType(ColumnDataType dataType) {
		this.dataType = dataType;
	}

	@Override
	public String toString() {
		return "C_ColumnReferencePlaceholder [id=" + id + ", columnId="
				+ columnId + ", label=" + label + ", dataType=" + dataType
				+ "]";
	}

}
