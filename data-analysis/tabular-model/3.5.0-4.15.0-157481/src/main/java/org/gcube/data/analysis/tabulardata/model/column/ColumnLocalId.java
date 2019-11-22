package org.gcube.data.analysis.tabulardata.model.column;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ColumnLocalId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2556965908087486347L;
	@XmlValue
	private String value = null;
	
	@SuppressWarnings("unused")
	private ColumnLocalId() {}

	public ColumnLocalId(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		ColumnLocalId other = (ColumnLocalId) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ColumnLocalId [value=");
		builder.append(value);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return new ColumnLocalId(new String(value));
	}
}
