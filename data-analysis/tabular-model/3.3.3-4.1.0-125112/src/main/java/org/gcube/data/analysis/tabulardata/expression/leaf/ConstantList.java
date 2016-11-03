package org.gcube.data.analysis.tabulardata.expression.leaf;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.expression.MultivaluedExpression;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ConstantList extends LeafExpression implements MultivaluedExpression{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1479945639810020364L;
	private List<TDTypeValue> arguments=null;
	
	@SuppressWarnings("unused")
	private ConstantList() {}

	public ConstantList(List<TDTypeValue> arguments) {
		super();
		this.arguments = arguments;
	}
	
	@Override
	public DataType getReturnedDataType(){
		return arguments.get(0).getReturnedDataType();
	}
	
	@Override
	public void validate() throws MalformedExpressionException {
		if(arguments==null) throw new MalformedExpressionException("ConstantList arguments cannot be null. "+this);
		if(arguments.size()<2) throw new  MalformedExpressionException("ConstantList needs at least 2 arguments. "+this);
		DataType type=null;
		for(TDTypeValue val : arguments){
			if(val==null) throw new MalformedExpressionException("ConstantList cannot contain null values. "+this);
			if(type==null)type=val.getReturnedDataType();
			else {
				DataType current=val.getReturnedDataType();
				if(!type.getClass().equals(current.getClass()))throw new MalformedExpressionException(String.format("ConstantList cannot contain different data types: found %s expected %s",current,type));
			}
			val.validate();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((arguments == null) ? 0 : arguments.hashCode());
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
		ConstantList other = (ConstantList) obj;
		if (arguments == null) {
			if (other.arguments != null)
				return false;
		} else if (!arguments.equals(other.arguments))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ConstantList [arguments=");
		builder.append(arguments);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * @return the arguments
	 */
	public List<TDTypeValue> getArguments() {
		return arguments;
	}

	/**
	 * @param arguments the arguments to set
	 */
	public void setArguments(List<TDTypeValue> arguments) {
		this.arguments = arguments;
	}
	
	
	
}
