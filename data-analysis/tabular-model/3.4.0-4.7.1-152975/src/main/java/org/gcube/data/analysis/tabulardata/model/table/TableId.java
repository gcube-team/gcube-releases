package org.gcube.data.analysis.tabulardata.model.table;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TableId implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4249040709176393892L;
	
	@XmlValue
	private long value;
	
	@SuppressWarnings("unused")
	private TableId(){}

	public TableId(long value) {
		this.value = value;
	}

	public long getValue() {
		return value;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (value ^ (value >>> 32));
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
		TableId other = (TableId) obj;
		if (value != other.value)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TableId [value=");
		builder.append(value);
		builder.append("]");
		return builder.toString();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {		
		return new TableId(value);
	}
}
