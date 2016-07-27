package org.gcube.portlets.user.td.expressionwidget.client.operation;

import java.io.Serializable;

import org.gcube.portlets.user.td.expressionwidget.shared.model.C_OperatorType;



/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class Operation implements Serializable {
	
	private static final long serialVersionUID = 3713817747863556150L;
	
	protected Integer id;
	protected String name;
	protected C_OperatorType operatorType;
	protected String label;
	
	public Operation(Integer id,String name, String label, C_OperatorType operatorType){
		this.id=id;
		this.name=name;
		this.label=label;
		this.operatorType=operatorType;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public C_OperatorType getOperatorType() {
		return operatorType;
	}

	public void setOperatorType(C_OperatorType operatorType) {
		this.operatorType = operatorType;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "Operation [id=" + id + ", name=" + name + ", operatorType="
				+ operatorType + ", label=" + label + "]";
	}

	
	
	
	
}
