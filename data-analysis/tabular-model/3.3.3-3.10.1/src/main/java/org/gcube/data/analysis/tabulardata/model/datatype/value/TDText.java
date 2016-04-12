package org.gcube.data.analysis.tabulardata.model.datatype.value;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TDText extends TDTypeValue {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3520769186550834685L;
			
	String value;
	
	@SuppressWarnings("unused")
	private TDText() {}
	
	public TDText(String value) {
		super();
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
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
		TDText other = (TDText) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	@Override
	public void validate() throws MalformedExpressionException {
		if(value==null) throw new MalformedExpressionException("Text constant value cannot be null");
	}
	

	@Override
	public int compareTo(TDTypeValue o) {		
		return this.getValue().compareTo(((TDText)o).getValue());
	}
	
	@Override
	public DataType getReturnedDataType() {		
		return new TextType();
	}
}
