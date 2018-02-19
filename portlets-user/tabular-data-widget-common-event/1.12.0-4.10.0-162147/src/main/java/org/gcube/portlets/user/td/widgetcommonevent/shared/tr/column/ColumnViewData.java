package org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class ColumnViewData implements Serializable {

	private static final long serialVersionUID = 6727733264842637144L;
	
	/**
	 * ColumnId of dimension column in view 
	 */
	protected String sourceTableDimensionColumnId;
	
	/**
	 * ColumnId of dimension column in table
	 */
	protected String targetTableColumnId;
	
	/**
	 * TableId of table 
	 */
	protected long targetTableId;

	public ColumnViewData() {

	}

	public ColumnViewData(String sourceTableDimensionColumnId,
			String targetTableColumnId, long targetTableId) {
		this.sourceTableDimensionColumnId = sourceTableDimensionColumnId;
		this.targetTableColumnId = targetTableColumnId;
		this.targetTableId = targetTableId;
	}

	public String getSourceTableDimensionColumnId() {
		return sourceTableDimensionColumnId;
	}

	public void setSourceTableDimensionColumnId(
			String sourceTableDimensionColumnId) {
		this.sourceTableDimensionColumnId = sourceTableDimensionColumnId;
	}

	public String getTargetTableColumnId() {
		return targetTableColumnId;
	}

	public void setTargetTableColumnId(String targetTableColumnId) {
		this.targetTableColumnId = targetTableColumnId;
	}

	public long getTargetTableId() {
		return targetTableId;
	}

	public void setTargetTableId(long targetTableId) {
		this.targetTableId = targetTableId;
	}

	@Override
	public String toString() {
		return "ColumnViewData [sourceTableDimensionColumnId="
				+ sourceTableDimensionColumnId + ", targetTableColumnId="
				+ targetTableColumnId + ", targetTableId=" + targetTableId
				+ "]";
	}

}
