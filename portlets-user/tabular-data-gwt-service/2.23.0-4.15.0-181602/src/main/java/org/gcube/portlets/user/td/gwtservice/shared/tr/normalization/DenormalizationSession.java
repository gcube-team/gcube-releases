package org.gcube.portlets.user.td.gwtservice.shared.tr.normalization;

import java.io.Serializable;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class DenormalizationSession implements Serializable {

	private static final long serialVersionUID = 4139331553059193758L;

	protected TRId trId;
	protected ColumnData valueColumn;
	protected ColumnData attributeColumn;

	public DenormalizationSession() {

	}

	public DenormalizationSession(TRId trId, ColumnData valueColumn, ColumnData attributeColumn) {
		this.trId = trId;
		this.valueColumn = valueColumn;
		this.attributeColumn = attributeColumn;

	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public ColumnData getValueColumn() {
		return valueColumn;
	}

	public void setValueColumn(ColumnData valueColumn) {
		this.valueColumn = valueColumn;
	}

	public ColumnData getAttributeColumn() {
		return attributeColumn;
	}

	public void setAttributeColumn(ColumnData attributeColumn) {
		this.attributeColumn = attributeColumn;
	}

	@Override
	public String toString() {
		return "DenormalizationSession [trId=" + trId + ", valueColumn=" + valueColumn + ", attributeColumn="
				+ attributeColumn + "]";
	}

}
