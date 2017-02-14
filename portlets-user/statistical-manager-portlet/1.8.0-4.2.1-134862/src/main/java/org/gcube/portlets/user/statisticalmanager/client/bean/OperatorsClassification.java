/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.bean;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author ceras
 *
 */
public class OperatorsClassification implements IsSerializable {

	private String name;
	private List<OperatorCategory> operatorCategories = new ArrayList<OperatorCategory>();
	private List<Operator> operators = new ArrayList<Operator>();
	
	public OperatorsClassification() {
		super();
	}
	
	/**
	 * 
	 */
	public OperatorsClassification(String name) {
		super();
		this.name = name;
	}
	
	/**
	 * @param operatorCategories
	 * @param operators
	 */
	public OperatorsClassification(String name, List<OperatorCategory> operatorCategories,
			List<Operator> operators) {
		this(name);
		this.operatorCategories = operatorCategories;
		this.operators = operators;
	}

	/**
	 * @return the operatorCategories
	 */
	public List<OperatorCategory> getOperatorCategories() {
		return operatorCategories;
	}

	/**
	 * @param operatorCategories the operatorCategories to set
	 */
	public void setOperatorCategories(List<OperatorCategory> operatorCategories) {
		this.operatorCategories = operatorCategories;
	}

	/**
	 * @return the operators
	 */
	public List<Operator> getOperators() {
		return operators;
	}

	/**
	 * @param operators the operators to set
	 */
	public void setOperators(List<Operator> operators) {
		this.operators = operators;
	}
	
	public Operator getOperatorById(String id) {
		if (id==null)
			return null;
		Operator operator = null;
		for (Operator op: operators)
			if (op.getId().contentEquals(id)) {
				operator = op;
				break;
			}
		return operator;
	}
	
	public OperatorCategory getCategoryById(String id) {
		OperatorCategory category = null;
		for (OperatorCategory cat: operatorCategories)
			if (cat.getId().contentEquals(id)) {
				category = cat;
				break;
			}
		return category;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
