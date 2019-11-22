package org.gcube.portlets.user.dataminerexecutor.shared.process;

import java.io.Serializable;

import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class InvocationModel implements Serializable {

	private static final long serialVersionUID = -6912479380989389840L;
	private InvocationAction invocationAction;
	private Operator operator;

	public InvocationModel() {
		super();
	}

	public InvocationModel(InvocationAction invocationAction, Operator operator) {
		super();
		this.operator = operator;
		this.invocationAction = invocationAction;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public InvocationAction getInvocationAction() {
		return invocationAction;
	}

	public void setInvocationAction(InvocationAction invocationAction) {
		this.invocationAction = invocationAction;
	}

	@Override
	public String toString() {
		return "InvocationModel [invocationAction=" + invocationAction + ", operator=" + operator + "]";
	}

}
