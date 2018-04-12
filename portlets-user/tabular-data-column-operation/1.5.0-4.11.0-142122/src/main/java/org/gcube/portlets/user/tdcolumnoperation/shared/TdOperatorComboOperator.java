/**
 * 
 */
package org.gcube.portlets.user.tdcolumnoperation.shared;

import java.io.Serializable;
import java.util.Arrays;



/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 6, 2014
 *
 */
public class TdOperatorComboOperator extends TdBaseComboDataBean implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 198556517185162166L;
	private String[] valueConstraints;
	private TdOperatorEnum operator;
	private String operationServiceId;
	
	/**
	 * 
	 */
	public TdOperatorComboOperator() {
	}

	/**
	 * 
	 * @param id
	 * @param label
	 * @param description
	 * @param constraint
	 */
	public TdOperatorComboOperator(String operationServiceId, TdOperatorEnum operator, String... constraint) {
		super(operator.getId(),operator.getLabel());
		this.operationServiceId = operationServiceId;
		this.operator = operator;
		this.valueConstraints = constraint;
	}

	public String[] getValueConstraints() {
		return valueConstraints;
	}

	public void setValueConstraints(String[] valueConstraints) {
		this.valueConstraints = valueConstraints;
	}


	public TdOperatorEnum getOperator() {
		return operator;
	}

	public void setOperator(TdOperatorEnum operator) {
		this.operator = operator;
	}

	public String getOperationServiceId() {
		return operationServiceId;
	}

	public void setOperationServiceId(String operationServiceId) {
		this.operationServiceId = operationServiceId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TdOperatorComboOperator [valueConstraints=");
		builder.append(Arrays.toString(valueConstraints));
		builder.append(", operator=");
		builder.append(operator);
		builder.append(", operationServiceId=");
		builder.append(operationServiceId);
		builder.append("]");
		return builder.toString();
	}
	
}
