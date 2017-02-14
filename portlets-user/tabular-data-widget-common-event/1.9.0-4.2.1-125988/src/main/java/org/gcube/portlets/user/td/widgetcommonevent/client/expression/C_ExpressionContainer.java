package org.gcube.portlets.user.td.widgetcommonevent.client.expression;

import java.io.Serializable;

import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class C_ExpressionContainer implements Serializable {

	private static final long serialVersionUID = -4021332410069520707L;

	public enum Contains {
		C_Expression, Rule;
	}

	private Contains id;
	private boolean conditionAllRows;
	private C_Expression exp;

	/**
	 * 
	 */
	public C_ExpressionContainer() {
		super();
	}

	/**
	 * 
	 * @param id
	 * @param exp
	 */
	public C_ExpressionContainer(Contains id, C_Expression exp) {
		super();
		this.id = id;
		this.conditionAllRows = false;
		this.exp = exp;
	}

	public C_ExpressionContainer(Contains id, boolean conditionAllRows,
			C_Expression exp) {
		super();
		this.id = id;
		this.conditionAllRows = conditionAllRows;
		this.exp = exp;
	}

	public Contains getId() {
		return id;
	}

	public void setId(Contains id) {
		this.id = id;
	}

	public boolean isConditionAllRows() {
		return conditionAllRows;
	}

	public void setConditionAllRows(boolean conditionAllRows) {
		this.conditionAllRows = conditionAllRows;
	}

	public C_Expression getExp() {
		return exp;
	}

	public void setExp(C_Expression exp) {
		this.exp = exp;
	}

	@Override
	public String toString() {
		return "C_ExpressionContainer [id=" + id + ", conditionAllRows="
				+ conditionAllRows + ", exp=" + exp + "]";
	}

}
