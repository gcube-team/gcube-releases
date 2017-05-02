/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.shared.process;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class OperatorsClassification implements Serializable {

	private static final long serialVersionUID = 7347445659350838584L;
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

	@Override
	public String toString() {
		return "OperatorsClassification [name=" + name
				+ ", operatorCategories=" + operatorCategories + ", operators="
				+ operators + "]";
	}
	
	
	
}
