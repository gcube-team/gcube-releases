package org.gcube.data.analysis.tabulardata.model.datatype.value;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TDInteger extends TDTypeValue {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3652315595200152035L;
	Integer value;
	
	@SuppressWarnings("unused")
	private TDInteger() {}

	public TDInteger(Integer value) {
		super();
		this.value = value;
	}

	public Integer getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value.toString();
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
		TDInteger other = (TDInteger) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	@Override
	public void validate() throws MalformedExpressionException {
		if(value==null) throw new MalformedExpressionException("Integer constant value cannot be null");
	}
	
	@Override
	public int compareTo(TDTypeValue o) {		
		return this.getValue().compareTo(((TDInteger)o).getValue());
	}
	
	@Override
	public DataType getReturnedDataType() {
		return new IntegerType();
	}
}
