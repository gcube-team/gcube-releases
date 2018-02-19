package org.gcube.data.analysis.tabulardata.expression.composite;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.expression.MultivaluedExpression;
import org.gcube.data.analysis.tabulardata.expression.NotEvaluableDataTypeException;
import org.gcube.data.analysis.tabulardata.expression.Operator;
import org.gcube.data.analysis.tabulardata.expression.leaf.LeafExpression;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
@XmlRootElement
public class ExternalReferenceExpression extends CompositeExpression implements MultivaluedExpression{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6558378164024433500L;

	private LeafExpression selectArgument;
	private Expression externalCondition;
	
	
	@Override
	public Operator getOperator() {
		return Operator.SELECT_IN;
	}
	
	
	
	@Override
	public DataType getReturnedDataType() throws NotEvaluableDataTypeException{
		return selectArgument.getReturnedDataType();
	}
	
	
	@SuppressWarnings("unused")
	private ExternalReferenceExpression() {		
	}



	public ExternalReferenceExpression(LeafExpression selectArgument,
			Expression externalCondition) {
		super();
		this.selectArgument = selectArgument;
		this.externalCondition = externalCondition;
	}

	@Override
	public void validate() throws MalformedExpressionException {
		if(selectArgument==null) throw new MalformedExpressionException("Select argument cannot be null."+this);
		if(externalCondition==null) throw new MalformedExpressionException("Condition cannot be null."+this);
		selectArgument.validate();
		externalCondition.validate();
	}



	/**
	 * @return the selectArgument
	 */
	public LeafExpression getSelectArgument() {
		return selectArgument;
	}



	/**
	 * @param selectArgument the selectArgument to set
	 */
	public void setSelectArgument(LeafExpression selectArgument) {
		this.selectArgument = selectArgument;
	}



	/**
	 * @return the externalCondition
	 */
	public Expression getExternalCondition() {
		return externalCondition;
	}



	/**
	 * @param externalCondition the externalCondition to set
	 */
	public void setExternalCondition(Expression externalCondition) {
		this.externalCondition = externalCondition;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((externalCondition == null) ? 0 : externalCondition
						.hashCode());
		result = prime * result
				+ ((selectArgument == null) ? 0 : selectArgument.hashCode());
		return result;
	}

	@Override
	public List<Expression> getLeavesByType(Class<? extends LeafExpression> type) {
		List<Expression> expressions = new ArrayList<>();
		if (type.isInstance(externalCondition))
			expressions.add(externalCondition);
		else expressions.addAll(externalCondition.getLeavesByType(type));
		return expressions;
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
		ExternalReferenceExpression other = (ExternalReferenceExpression) obj;
		if (externalCondition == null) {
			if (other.externalCondition != null)
				return false;
		} else if (!externalCondition.equals(other.externalCondition))
			return false;
		if (selectArgument == null) {
			if (other.selectArgument != null)
				return false;
		} else if (!selectArgument.equals(other.selectArgument))
			return false;
		return true;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExternalReferenceExpression [selectArgument=");
		builder.append(selectArgument);
		builder.append(", externalCondition=");
		builder.append(externalCondition);
		builder.append("]");
		return builder.toString();
	}
	
	
}
