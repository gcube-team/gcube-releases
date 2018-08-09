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
public abstract class UnaryExpression extends CompositeExpression{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8091996373243839954L;
	
	private static final List<Class<? extends DataType>> DEFAULT_ACCEPTED_TYPES=new ArrayList<>();
	
	static {
		DEFAULT_ACCEPTED_TYPES.add(TextType.class);
		DEFAULT_ACCEPTED_TYPES.add(BooleanType.class);
		DEFAULT_ACCEPTED_TYPES.add(DateType.class);		
		DEFAULT_ACCEPTED_TYPES.add(IntegerType.class);
		DEFAULT_ACCEPTED_TYPES.add(NumericType.class);		
	}
	
	private Expression argument=null;
	
	protected UnaryExpression(){}
	
	protected UnaryExpression(Expression argument){
		this.setArgument(argument);
	}

	public Expression getArgument() {
		return argument;
	}

	public void setArgument(Expression argument) {
		this.argument = argument;
	}
	

	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((argument == null) ? 0 : argument.hashCode());
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
		UnaryExpression other = (UnaryExpression) obj;
		if (argument == null) {
			if (other.argument != null)
				return false;
		} else if (!argument.equals(other.argument))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UnaryExpression [");
		builder.append(getOperator());
		builder.append(",");
		builder.append(argument);		
		builder.append("]");
		return builder.toString();
	}	
	
	@Override
	public void validate() throws MalformedExpressionException {
		if (argument==null) throw new MalformedExpressionException("Argument cannot be null, expression is "+this);
		try{
			DataType type=argument.getReturnedDataType();
			if(!allowedDataTypes().contains(type.getClass())) throw new MalformedExpressionException(String.format("Unexpected argument data type %s, allowed types are %s ", type,allowedDataTypes()));
		}catch(NotEvaluableDataTypeException e){
			//skip check
		}
	}
	
	public List<Class<? extends DataType>> allowedDataTypes(){
		return DEFAULT_ACCEPTED_TYPES;
	}

	@Override
	public List<Expression> getLeavesByType(
			Class<? extends LeafExpression> type) {
		List<Expression> expressions = new ArrayList<>();
		if (type.isInstance(this.argument))
			expressions.add(this.argument);
		else expressions.addAll(this.argument.getLeavesByType(type));
		return expressions;
	}
		
}
