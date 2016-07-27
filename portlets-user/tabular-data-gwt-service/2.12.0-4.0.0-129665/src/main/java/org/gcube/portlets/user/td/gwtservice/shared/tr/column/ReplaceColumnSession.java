package org.gcube.portlets.user.td.gwtservice.shared.tr.column;

import java.io.Serializable;

import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ReplaceColumnSession implements Serializable {

	private static final long serialVersionUID = -1896235499708614266L;

	private String value; //Original value
	private String replaceValue; //New value
	private TRId trId;
	private ColumnData columnData;
	private String rowId;
	private boolean replaceDimension;

	public ReplaceColumnSession() {

	}

	//Simple Replace
	public ReplaceColumnSession(String value, String replaceValue, TRId trId,
			ColumnData columnData, String rowId) {
		this.value = value;
		this.replaceValue = replaceValue;
		this.trId = trId;
		this.columnData = columnData;
		this.rowId = rowId;
		this.replaceDimension = false;
	}
	
	
	//Dimension Replace
	public ReplaceColumnSession(String value, String replaceValue, TRId trId,
			ColumnData columnData, String rowId, boolean replaceDimension) {
		this.value = value;
		this.replaceValue = replaceValue;
		this.trId = trId;
		this.columnData = columnData;
		this.rowId = rowId;
		this.replaceDimension = replaceDimension;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getReplaceValue() {
		return replaceValue;
	}

	public void setReplaceValue(String replaceValue) {
		this.replaceValue = replaceValue;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public ColumnData getColumnData() {
		return columnData;
	}

	public void setColumnData(ColumnData columnData) {
		this.columnData = columnData;
	}

	public String getRowId() {
		return rowId;
	}

	public void setRowId(String rowId) {
		this.rowId = rowId;
	}

	public boolean isReplaceDimension() {
		return replaceDimension;
	}

	public void setReplaceDimension(boolean replaceDimension) {
		this.replaceDimension = replaceDimension;
	}

	@Override
	public String toString() {
		return "ReplaceColumnSession [value=" + value + ", replaceValue="
				+ replaceValue + ", trId=" + trId + ", columnData="
				+ columnData + ", rowId=" + rowId + ", replaceDimension="
				+ replaceDimension + "]";
	}

}
