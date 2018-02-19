package org.gcube.data.analysis.tabulardata.model.column;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.expression.NotEvaluableDataTypeException;
import org.gcube.data.analysis.tabulardata.expression.leaf.LeafExpression;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.table.TableId;

@XmlRootElement(name="ColumnReference")
@XmlAccessorType(XmlAccessType.FIELD)
public class ColumnReference extends LeafExpression{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1322874213333736355L;

	private TableId tableId;

	private ColumnLocalId columnId;

	private DataType type;
	
	protected ColumnReference() {}



	public ColumnReference(TableId tableId, ColumnLocalId columnId) {		
		this.tableId = tableId;
		this.columnId = columnId;
	}

	public ColumnReference(TableId tableId, ColumnLocalId columnId, DataType type) {		
		this.tableId = tableId;
		this.columnId = columnId;
		this.type = type;
	}


	public TableId getTableId() {
		return tableId;
	}

	public void setTableId(TableId tableId) {
		this.tableId = tableId;
	}

	public ColumnLocalId getColumnId() {
		return columnId;
	}

	public void setColumnId(ColumnLocalId columnId) {
		this.columnId = columnId;
	}
		
	public DataType getType() {
		return type;
	}

	public void setType(DataType type) {
		this.type = type;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((columnId == null) ? 0 : columnId.hashCode());
		result = prime * result + ((tableId == null) ? 0 : tableId.hashCode());
		return result;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ColumnReference other = (ColumnReference) obj;
		if (columnId == null) {
			if (other.columnId != null)
				return false;
		} else if (!columnId.equals(other.columnId))
			return false;
		if (tableId == null) {
			if (other.tableId != null)
				return false;
		} else if (!tableId.equals(other.tableId))
			return false;
		return true;
	}

	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ColumnReference [tableId=");
		builder.append(tableId);
		builder.append(", columnId=");
		builder.append(columnId);
		builder.append(", datatype=");
		builder.append(type);
		builder.append("]");
		return builder.toString();
	}



	public void validate() throws MalformedExpressionException {
		if(tableId==null) throw new MalformedExpressionException("Table id cannot be null,"+this);
		if(columnId==null) throw new MalformedExpressionException("Column id cannot be null,"+this);
	}


	@Override
	public DataType getReturnedDataType() throws NotEvaluableDataTypeException{
		if (type == null)
			throw new NotEvaluableDataTypeException("Column reference data type cannot be evaluated");
		else return type;
	}

}
