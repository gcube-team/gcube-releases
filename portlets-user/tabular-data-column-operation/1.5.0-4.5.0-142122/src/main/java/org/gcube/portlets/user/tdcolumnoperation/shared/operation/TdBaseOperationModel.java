/**
 * 
 */
package org.gcube.portlets.user.tdcolumnoperation.shared.operation;

import java.io.Serializable;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @May 29, 2014
 *
 */
public class TdBaseOperationModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3061286654445666509L;
	
	private Long tableId;
	private String columnName;
	
	private String operatorId; //is value of org.gcube.data.analysis.tabulardata.expression.Operation
	
	/**
	 * 
	 */
	public TdBaseOperationModel() {
	}

	/**
	 * @param tableId
	 * @param columnName
	 * @param operatorId is value of org.gcube.data.analysis.tabulardata.expression.Operation
	 */
	public TdBaseOperationModel(Long tableId, String columnName, String operatorId) {
		this.tableId = tableId;
		this.columnName = columnName;
		this.operatorId = operatorId;
	}

	public Long getTableId() {
		return tableId;
	}

	public String getColumnName() {
		return columnName;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TdBaseOperationModel [tableId=");
		builder.append(tableId);
		builder.append(", columnName=");
		builder.append(columnName);
		builder.append(", operatorId=");
		builder.append(operatorId);
		builder.append("]");
		return builder.toString();
	}
	
}
