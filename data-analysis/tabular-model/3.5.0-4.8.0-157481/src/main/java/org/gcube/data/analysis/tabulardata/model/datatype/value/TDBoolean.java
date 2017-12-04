package org.gcube.data.analysis.tabulardata.model.datatype.value;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TDBoolean extends TDTypeValue {

	/**
	 * 
	 */
	private static final long serialVersionUID = -904329447835953527L;
	Boolean value=null;
	
	@SuppressWarnings("unused")
	private TDBoolean() {}

	public TDBoolean(boolean value) {
		super();
		this.value = value;
	}

	public Boolean getValue() {
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
		TDBoolean other = (TDBoolean) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public void validate() throws MalformedExpressionException {
		if(value==null) throw new MalformedExpressionException("Boolean constant value cannot be null");
	}
	
	@Override
	public int compareTo(TDTypeValue o) {
		return this.getValue().compareTo(((TDBoolean)o).getValue());
	}
	
	@Override
	public DataType getReturnedDataType() {
		return new BooleanType();
	}
}
