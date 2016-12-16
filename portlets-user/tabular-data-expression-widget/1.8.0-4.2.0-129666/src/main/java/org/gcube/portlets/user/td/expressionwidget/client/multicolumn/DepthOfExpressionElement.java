package org.gcube.portlets.user.td.expressionwidget.client.multicolumn;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class DepthOfExpressionElement {

	private int id; // For insert in table only
	private DepthOfExpressionType type;
	private String label;

	public DepthOfExpressionElement() {
		super();
	}

	public DepthOfExpressionElement(int id, DepthOfExpressionType type,
			String label) {
		super();
		this.id = id;
		this.type = type;
		this.label = label;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DepthOfExpressionType getType() {
		return type;
	}

	public void setType(DepthOfExpressionType type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return "DepthOfExpressionElement [id=" + id + ", type=" + type
				+ ", label=" + label + "]";
	}

}
