package org.gcube.data.analysis.tabulardata.model.datatype.value;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TDNumeric extends TDTypeValue {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7482958149525781700L;
	Float value;
	Double doubleValue;
	
	@SuppressWarnings("unused")
	private TDNumeric() {}

	public TDNumeric(Double value) {
		super();
		this.doubleValue = value;
	}

	public TDNumeric(Float value) {
		super();
		this.value = value;
	}
	
	public Double getValue() {
		if (doubleValue==null)
			return new Double(value);
		else return doubleValue;
	}
	
	@Override
	public String toString() {
		return getValue().toString();
	}

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((doubleValue == null) ? 0 : doubleValue.hashCode());
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
		TDNumeric other = (TDNumeric) obj;
		if (doubleValue == null) {
			if (other.doubleValue != null)
				return false;
		} else if (!doubleValue.equals(other.doubleValue))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public void validate() throws MalformedExpressionException {
		if(getValue()==null) throw new MalformedExpressionException("Numeric constant value cannot be null");
	}
	
	@Override
	public int compareTo(TDTypeValue o) {
		return this.getValue().compareTo(((TDNumeric)o).getValue());		
	}
	
	@Override
	public DataType getReturnedDataType() {		
		return new NumericType();
	}
}
