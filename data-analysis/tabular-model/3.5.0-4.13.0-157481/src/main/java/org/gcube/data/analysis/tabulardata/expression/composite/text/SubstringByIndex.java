package org.gcube.data.analysis.tabulardata.expression.composite.text;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.expression.NotEvaluableDataTypeException;
import org.gcube.data.analysis.tabulardata.expression.Operator;
import org.gcube.data.analysis.tabulardata.expression.composite.CompositeExpression;
import org.gcube.data.analysis.tabulardata.expression.leaf.LeafExpression;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SubstringByIndex extends CompositeExpression implements TextExpression{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5567106578891064168L;
	private Expression sourceString;
	private Expression fromIndex;
	private Expression toIndex;
	
	
	@SuppressWarnings("unused")
	private SubstringByIndex() {
	}
	
	
	public SubstringByIndex(Expression sourceString, Expression fromIndex,
			Expression toIndex) {
		super();
		this.sourceString = sourceString;
		this.fromIndex = fromIndex;
		this.toIndex = toIndex;
	}

	@Override
	public Operator getOperator() {
		return Operator.SUBSTRING_BY_INDEX;
	}

	@Override
	public void validate() throws MalformedExpressionException {
		if(sourceString==null) throw new MalformedExpressionException("Source string cannot be null. "+this);
		if(fromIndex==null) throw new MalformedExpressionException("From Index cannot be null. "+this);
		if(toIndex==null) throw new MalformedExpressionException("Source string cannot be null. "+this);
		sourceString.validate();
		fromIndex.validate();
		toIndex.validate();
		try{
			DataType sourceType=sourceString.getReturnedDataType();
			if(!(sourceType instanceof TextType)) throw new MalformedExpressionException("Source string expression must return string type. Returned Type is "+sourceType.getName()+"."+this);
		}catch(NotEvaluableDataTypeException e){/* not evaluable*/}
		try{
			DataType fromType=fromIndex.getReturnedDataType();
			if(!(fromType instanceof IntegerType)) throw new MalformedExpressionException("From index expression must return integer type. Returned Type is "+fromType.getName()+"."+this);
		}catch(NotEvaluableDataTypeException e){/* not evaluable*/}
		try{
			DataType toType=toIndex.getReturnedDataType();
			if(!(toType instanceof IntegerType)) throw new MalformedExpressionException("To index expression must return integer type. Returned Type is "+toType.getName()+"."+this);
		}catch(NotEvaluableDataTypeException e){/* not evaluable*/}
	}

	@Override
	public DataType getReturnedDataType() throws NotEvaluableDataTypeException {
		return new TextType();
	}

	@Override
	public List<Expression> getLeavesByType(Class<? extends LeafExpression> type) {
		List<Expression> toReturn = new ArrayList<>();
		if (type.isInstance(sourceString))
				toReturn.add(sourceString);
		else toReturn.addAll(sourceString.getLeavesByType(type));
		if (type.isInstance(fromIndex))
			toReturn.add(fromIndex);
		else toReturn.addAll(fromIndex.getLeavesByType(type));
		if (type.isInstance(toIndex))
			toReturn.add(toIndex);
		else toReturn.addAll(toIndex.getLeavesByType(type));
		return toReturn;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SubstringByIndex [sourceString=");
		builder.append(sourceString);
		builder.append(", fromIndex=");
		builder.append(fromIndex);
		builder.append(", toIndex=");
		builder.append(toIndex);
		builder.append("]");
		return builder.toString();
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fromIndex == null) ? 0 : fromIndex.hashCode());
		result = prime * result
				+ ((sourceString == null) ? 0 : sourceString.hashCode());
		result = prime * result + ((toIndex == null) ? 0 : toIndex.hashCode());
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
		SubstringByIndex other = (SubstringByIndex) obj;
		if (fromIndex == null) {
			if (other.fromIndex != null)
				return false;
		} else if (!fromIndex.equals(other.fromIndex))
			return false;
		if (sourceString == null) {
			if (other.sourceString != null)
				return false;
		} else if (!sourceString.equals(other.sourceString))
			return false;
		if (toIndex == null) {
			if (other.toIndex != null)
				return false;
		} else if (!toIndex.equals(other.toIndex))
			return false;
		return true;
	}


	/**
	 * @return the sourceString
	 */
	public Expression getSourceString() {
		return sourceString;
	}


	/**
	 * @param sourceString the sourceString to set
	 */
	public void setSourceString(Expression sourceString) {
		this.sourceString = sourceString;
	}


	/**
	 * @return the fromIndex
	 */
	public Expression getFromIndex() {
		return fromIndex;
	}


	/**
	 * @param fromIndex the fromIndex to set
	 */
	public void setFromIndex(Expression fromIndex) {
		this.fromIndex = fromIndex;
	}


	/**
	 * @return the toIndex
	 */
	public Expression getToIndex() {
		return toIndex;
	}


	/**
	 * @param toIndex the toIndex to set
	 */
	public void setToIndex(Expression toIndex) {
		this.toIndex = toIndex;
	}

	
	
}
