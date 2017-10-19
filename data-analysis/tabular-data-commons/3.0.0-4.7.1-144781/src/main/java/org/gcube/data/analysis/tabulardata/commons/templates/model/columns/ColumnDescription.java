package org.gcube.data.analysis.tabulardata.commons.templates.model.columns;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.commons.utils.Cardinality;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ColumnDescription implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1761343717899968917L;
	
	private ColumnCategory columnCategory;
	private Cardinality cardinality;
	
	@SuppressWarnings("unused")
	private ColumnDescription(){}
	
	public ColumnDescription(ColumnCategory columnCategory){
		this.columnCategory= columnCategory;
		this.cardinality=new Cardinality(0, Integer.MAX_VALUE);
	}
	
	public ColumnDescription(ColumnCategory columnCategory,
			Cardinality cardinality) {
		super();
		this.columnCategory = columnCategory;
		this.cardinality = cardinality;
	}

	/**
	 * @return the columnCategory
	 */
	public ColumnCategory getColumnCategory() {
		return columnCategory;
	}

	/**
	 * @return the cardinality
	 */
	public Cardinality getCardinality() {
		return cardinality;
	}

	


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((cardinality == null) ? 0 : cardinality.hashCode());
		result = prime * result
				+ ((columnCategory == null) ? 0 : columnCategory.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ColumnDescription other = (ColumnDescription) obj;
		if (cardinality == null) {
			if (other.cardinality != null)
				return false;
		} else if (!cardinality.equals(other.cardinality))
			return false;
		if (columnCategory != other.columnCategory)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ColumnDescription [columnCategory=" + columnCategory
				+ ", cardinality=" + cardinality + "]";
	}
	
	
	
}
