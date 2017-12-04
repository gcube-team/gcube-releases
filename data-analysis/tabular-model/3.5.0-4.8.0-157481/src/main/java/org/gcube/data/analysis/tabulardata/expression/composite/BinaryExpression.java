package org.gcube.data.analysis.tabulardata.expression.composite;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.expression.NotEvaluableDataTypeException;
import org.gcube.data.analysis.tabulardata.expression.leaf.LeafExpression;
import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.DateType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class BinaryExpression extends CompositeExpression{

	

	private static final List<Class<? extends DataType>> DEFAULT_ACCEPTED_TYPES=new ArrayList<>();
	
	static {
		DEFAULT_ACCEPTED_TYPES.add(TextType.class);
		DEFAULT_ACCEPTED_TYPES.add(BooleanType.class);
		DEFAULT_ACCEPTED_TYPES.add(DateType.class);		
		DEFAULT_ACCEPTED_TYPES.add(IntegerType.class);
		DEFAULT_ACCEPTED_TYPES.add(NumericType.class);		
	}
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1471639807654392162L;
	private Expression leftArgument=null;
	private Expression rightArgument=null;
	
	protected BinaryExpression() {}

	public BinaryExpression(Expression leftArgument, Expression rightArgument) {
		super();
		this.leftArgument = leftArgument;
		this.rightArgument = rightArgument;
	}


	public Expression getLeftArgument() {
		return leftArgument;
	}

	public void setLeftArgument(Expression leftArgument) {
		this.leftArgument = leftArgument;
	}

	public Expression getRightArgument() {
		return rightArgument;
	}

	public void setRightArgument(Expression rightArgument) {
		this.rightArgument = rightArgument;
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((leftArgument == null) ? 0 : leftArgument.hashCode());
		result = prime * result
				+ ((rightArgument == null) ? 0 : rightArgument.hashCode());
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
		BinaryExpression other = (BinaryExpression) obj;
		if (leftArgument == null) {
			if (other.leftArgument != null)
				return false;
		} else if (!leftArgument.equals(other.leftArgument))
			return false;
		if (rightArgument == null) {
			if (other.rightArgument != null)
				return false;
		} else if (!rightArgument.equals(other.rightArgument))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BinaryExpression [");
		builder.append(leftArgument+" ");
		builder.append(getOperator()+" ");		
		builder.append(rightArgument);		
		builder.append("]");
		return builder.toString();
	}
	
	@Override
	public void validate() throws MalformedExpressionException {
		if(leftArgument==null) throw new MalformedExpressionException("Left Argument cannot be null, expression : "+this);
		if(rightArgument==null) throw new MalformedExpressionException("Right Argument cannot be null, expression : "+this);
		leftArgument.validate();
		rightArgument.validate();
		try{
			DataType left=leftArgument.getReturnedDataType();
			if(!allowedLeftDataTypes().contains(left.getClass())) throw new MalformedExpressionException(String.format("Unexpected left argument data type %s, allowed types are %s ", left,allowedLeftDataTypes()));
			DataType right=rightArgument.getReturnedDataType();
			if(!allowedRightDataTypes().contains(right.getClass())) throw new MalformedExpressionException(String.format("Unexpected right argument data type %s, allowed types are %s ", right,allowedRightDataTypes()));
		}catch(NotEvaluableDataTypeException e){
			//Unable to perform data type check
		}
	}
	
	
	public List<Class<? extends DataType>> allowedLeftDataTypes(){
		return DEFAULT_ACCEPTED_TYPES;
	}
	public List<Class<? extends DataType>> allowedRightDataTypes(){
		return DEFAULT_ACCEPTED_TYPES;
	}

	@Override
	public List<Expression> getLeavesByType(Class<? extends LeafExpression> type) {
		List<Expression> toReturn = new ArrayList<>();
		if (type.isInstance(leftArgument))
			toReturn.add(leftArgument);
		else toReturn.addAll(leftArgument.getLeavesByType(type));
		if (type.isInstance(rightArgument))
			toReturn.add(rightArgument);
		else toReturn.addAll(rightArgument.getLeavesByType(type));
		return toReturn;
	}
	
	
	
}
