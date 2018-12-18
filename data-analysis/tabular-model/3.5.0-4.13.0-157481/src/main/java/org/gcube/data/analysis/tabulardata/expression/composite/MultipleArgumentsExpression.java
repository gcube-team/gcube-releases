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
public abstract class MultipleArgumentsExpression extends CompositeExpression{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3064772540730484570L;
	
	private static final List<Class<? extends DataType>> DEFAULT_ACCEPTED_TYPES=new ArrayList<>();
	
	static {
		DEFAULT_ACCEPTED_TYPES.add(TextType.class);
		DEFAULT_ACCEPTED_TYPES.add(BooleanType.class);
		DEFAULT_ACCEPTED_TYPES.add(DateType.class);		
		DEFAULT_ACCEPTED_TYPES.add(IntegerType.class);
		DEFAULT_ACCEPTED_TYPES.add(NumericType.class);
	}
	
	
	protected List<Expression> arguments=null;
	
	protected MultipleArgumentsExpression(){}
	
	protected MultipleArgumentsExpression(List<Expression> arguments){
		this.arguments=arguments;
	}
	

	/**
	 * @return the arguments
	 */
	public List<Expression> getArguments() {
		return arguments;
	}

	/**
	 * @param arguments the arguments to set
	 */
	public void setArguments(List<Expression> arguments) {
		this.arguments = arguments;
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
		MultipleArgumentsExpression other = (MultipleArgumentsExpression) obj;
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
		builder.append("MultipleArgumentsExpression [");
		builder.append(getOperator());
		builder.append(",");
		builder.append(arguments);
		builder.append("]");
		return builder.toString();
	}
	
	@Override
	public void validate() throws MalformedExpressionException {
		if (arguments==null) throw new MalformedExpressionException("Null arguments list in expression "+this);
		for(Expression expr:arguments){
			if(expr==null) throw new MalformedExpressionException("Null argument in expression "+this);
		}
		for(Expression expr:arguments)expr.validate();
		for(Expression expr:arguments){
			try{
				DataType type=expr.getReturnedDataType();
				if(!allowedDataTypes().contains(type.getClass())) throw new MalformedExpressionException(String.format("Unexpected argument data type %s, allowed types are %s ", type,allowedDataTypes()));
			}catch(NotEvaluableDataTypeException e){
				//skip check
			}
		}
	}
	
	public List<Class<? extends DataType>> allowedDataTypes(){
		return DEFAULT_ACCEPTED_TYPES;
	}

	@Override
	public List<Expression> getLeavesByType(
			Class<? extends LeafExpression> type) {
		List<Expression> toReturn = new ArrayList<>();
		for (Expression expr : arguments){
			if(type.isInstance(expr))
				toReturn.add(expr);
			else toReturn.addAll(expr.getLeavesByType(type));
		}
		return toReturn;
	}
		
	
}
