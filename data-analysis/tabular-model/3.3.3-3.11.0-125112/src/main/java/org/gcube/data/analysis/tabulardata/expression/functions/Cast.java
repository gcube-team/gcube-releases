package org.gcube.data.analysis.tabulardata.expression.functions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.expression.NotEvaluableDataTypeException;
import org.gcube.data.analysis.tabulardata.expression.Operator;
import org.gcube.data.analysis.tabulardata.expression.composite.UnaryExpression;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Cast extends UnaryExpression {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5205666076797065546L;

	private DataType castToType;
	
	@SuppressWarnings("unused")
	private Cast() {
	}
	
	
	
	/**
	 * @return the castToType
	 */
	public DataType getCastToType() {
		return castToType;
	}



	/**
	 * @param castToType the castToType to set
	 */
	public void setCastToType(DataType castToType) {
		this.castToType = castToType;
	}



	public Cast(Expression argument, DataType castToType) {
		super(argument);
		this.castToType = castToType;
		if(castToType==null) throw new IllegalArgumentException("Cast to type cannot be null");
	}

	@Override
	public Operator getOperator() {
		return Operator.CAST;
	}

	@Override
	public DataType getReturnedDataType() throws NotEvaluableDataTypeException {
		return castToType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Cast [castToType=");
		builder.append(castToType);
		builder.append(", getArgument()=");
		builder.append(getArgument());
		builder.append("]");
		return builder.toString();
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((castToType == null) ? 0 : castToType.hashCode());
		return result;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cast other = (Cast) obj;
		if (castToType == null) {
			if (other.castToType != null)
				return false;
		} else if (!castToType.equals(other.castToType))
			return false;
		return true;
	}

	
	@Override
	public void validate() throws MalformedExpressionException {		
		super.validate();
		try{
			if(!isCastSupported(getArgument().getReturnedDataType(), castToType)) 
				throw new MalformedExpressionException(String.format("Cast from %s to %s is not supported",getArgument().getReturnedDataType(),castToType));
		}catch(NotEvaluableDataTypeException e){
			// skip
		}
	}
	
	
	public static final boolean isCastSupported(DataType sourceType,DataType targetType){
		// from / to text 
		if(targetType instanceof TextType||sourceType instanceof TextType) return true;
		// same class
		if(sourceType.getClass().equals(targetType.getClass())) return true;
		// integer <--> numeric 
		if((sourceType instanceof IntegerType && targetType instanceof NumericType)
				|| (sourceType instanceof NumericType && targetType instanceof IntegerType))return true;
		return false;
	}
}
