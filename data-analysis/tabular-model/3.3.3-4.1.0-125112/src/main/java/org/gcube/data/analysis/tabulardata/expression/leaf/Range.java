package org.gcube.data.analysis.tabulardata.expression.leaf;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.expression.MultivaluedExpression;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Range extends LeafExpression implements MultivaluedExpression{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2332849744212415199L;
	
	private TDTypeValue minimum;
	private TDTypeValue maximum;
	
	@SuppressWarnings("unused")
	private Range() {}
	
	
	
	
	public Range(TDTypeValue minimum, TDTypeValue maximum) {
		super();
		this.minimum = minimum;
		this.maximum = maximum;
	}


	


	/**
	 * @return the minimum
	 */
	public TDTypeValue getMinimum() {
		return minimum;
	}




	/**
	 * @param minimum the minimum to set
	 */
	public void setMinimum(TDTypeValue minimum) {
		this.minimum = minimum;
	}




	/**
	 * @return the maximum
	 */
	public TDTypeValue getMaximum() {
		return maximum;
	}




	/**
	 * @param maximum the maximum to set
	 */
	public void setMaximum(TDTypeValue maximum) {
		this.maximum = maximum;
	}




	


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((maximum == null) ? 0 : maximum.hashCode());
		result = prime * result + ((minimum == null) ? 0 : minimum.hashCode());
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
		Range other = (Range) obj;
		if (maximum == null) {
			if (other.maximum != null)
				return false;
		} else if (!maximum.equals(other.maximum))
			return false;
		if (minimum == null) {
			if (other.minimum != null)
				return false;
		} else if (!minimum.equals(other.minimum))
			return false;
		return true;
	}




	@Override
	public void validate() throws MalformedExpressionException{
		if(minimum==null) throw new MalformedExpressionException("Minimum value cannot be null");
		if(maximum==null) throw new MalformedExpressionException("Maximum value cannot be null");
		minimum.validate();
		maximum.validate();
		if(minimum.compareTo(maximum)>-1) throw new MalformedExpressionException("Incorrect range, values : min "+minimum+" ,max "+maximum);
		DataType minType=minimum.getReturnedDataType();
		DataType maxType=maximum.getReturnedDataType();
		if(!minType.getClass().equals(maxType.getClass())) throw new MalformedExpressionException(String.format("Incorrect range, minimum data type %s and maximum data type %s must be the same",minType.getName(),maxType.getName()));
	}

	@Override
	public DataType getReturnedDataType(){
		return minimum.getReturnedDataType();		
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Range [minimum=");
		builder.append(minimum);
		builder.append(", maximum=");
		builder.append(maximum);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
