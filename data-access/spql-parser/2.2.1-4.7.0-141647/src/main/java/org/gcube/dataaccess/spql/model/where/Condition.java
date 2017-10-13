/**
 * 
 */
package org.gcube.dataaccess.spql.model.where;

import java.util.Collections;
import java.util.List;

import org.gcube.dataaccess.spql.model.CheckableElement;
import org.gcube.dataaccess.spql.model.RelationalOperator;
import org.gcube.dataaccess.spql.model.error.InvalidValueError;
import org.gcube.dataaccess.spql.model.error.QueryError;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class Condition implements CheckableElement {
	
	protected ConditionParameter parameter;
	protected RelationalOperator operator;
	protected ParsableValue<?> value;
	
	public Condition(){}

	/**
	 * @param parameter
	 * @param operator
	 * @param value
	 */
	public Condition(ConditionParameter parameter, RelationalOperator operator, ParsableValue<?> value) {
		this.parameter = parameter;
		this.operator = operator;
		this.value = value;
	}

	/**
	 * @return the parameter
	 */
	public ConditionParameter getParameter() {
		return parameter;
	}

	/**
	 * @param parameter the parameter to set
	 */
	public void setParameter(ConditionParameter parameter) {
		this.parameter = parameter;
	}

	/**
	 * @return the operator
	 */
	public RelationalOperator getOperator() {
		return operator;
	}

	/**
	 * @param operator the operator to set
	 */
	public void setOperator(RelationalOperator operator) {
		this.operator = operator;
	}

	/**
	 * @return the value
	 */
	public ParsableValue<?> getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(ParsableValue<?> value) {
		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Condition [parameter=");
		builder.append(parameter);
		builder.append(", operator=");
		builder.append(operator);
		builder.append(", value=");
		builder.append(value);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public List<QueryError> check() {
		try {
			value.parse();
			return Collections.emptyList();
		} catch (Exception e) {
			return Collections.<QueryError>singletonList(new InvalidValueError("Invalid value \""+value.getTextValue()+"\""+e.getMessage()));
		}
	}
}
