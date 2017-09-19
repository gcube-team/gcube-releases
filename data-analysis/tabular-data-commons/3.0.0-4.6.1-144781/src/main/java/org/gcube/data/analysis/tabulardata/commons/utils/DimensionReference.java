package org.gcube.data.analysis.tabulardata.commons.utils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.commons.templates.model.ReferenceObject;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.table.TableId;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DimensionReference extends ReferenceObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ColumnLocalId columnId ;
	
	private TableId tableId;

	@SuppressWarnings("unused")
	private DimensionReference(){}
	
	public DimensionReference(TableId tableId, ColumnLocalId columnId) {
		super();
		this.tableId = tableId;
		this.columnId = columnId;
	}

	/**
	 * @return the columnId
	 */
	public ColumnLocalId getColumnId() {
		return columnId;
	}

	/**
	 * @return the tableId
	 */
	public TableId getTableId() {
		return tableId;
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
		DimensionReference other = (DimensionReference) obj;
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

	@Override
	public boolean check(Class<? extends DataType> datatype) {
		return true;
	}

	
	
	
}
