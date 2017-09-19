package org.gcube.data.analysis.tabulardata.expression.leaf;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Deprecated
public class TypedColumnReference extends ColumnReference {

	/**
	 * 
	 */
	private static final long serialVersionUID = 265306034621164027L;
	private DataType dataType;

	@SuppressWarnings("unused")
	private TypedColumnReference() {}
	
	
	public TypedColumnReference(TableId tableId, ColumnLocalId columnId,
			DataType dataType) {
		super(tableId, columnId);
		this.dataType = dataType;
	}

	/**
	 * @return the dataType
	 */
	public DataType getDataType() {
		return dataType;
	}

	/**
	 * @param dataType the dataType to set
	 */
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((dataType == null) ? 0 : dataType.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TypedColumnReference other = (TypedColumnReference) obj;
		if (dataType == null) {
			if (other.dataType != null)
				return false;
		} else if (!dataType.equals(other.dataType))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TypedColumnReference [dataType=");
		builder.append(dataType);
		builder.append(", getTableId()=");
		builder.append(getTableId());
		builder.append(", getColumnId()=");
		builder.append(getColumnId());
		builder.append("]");
		return builder.toString();
	}

	@Override
	public DataType getReturnedDataType() {		
		return getDataType();
	}
	
	
	@Override
	public void validate() throws MalformedExpressionException {		
		super.validate();
		if(dataType==null) throw new MalformedExpressionException("Datatype cannot be null, "+this);
	}
	
	
}

