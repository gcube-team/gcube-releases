package org.gcube.portlets.user.td.expressionwidget.client.multicolumn;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class LogicalDepth implements Serializable {
	private static final long serialVersionUID = 3597411498776039440L;

	private DepthOfExpressionType type;
	private ArrayList<C_Expression> arguments;

	public LogicalDepth(DepthOfExpressionType type,
			ArrayList<C_Expression> arguments) {
		super();
		this.type = type;
		this.arguments = arguments;
	}

	public DepthOfExpressionType getType() {
		return type;
	}

	public void setType(DepthOfExpressionType type) {
		this.type = type;
	}

	public ArrayList<C_Expression> getArguments() {
		return arguments;
	}

	public void setArguments(ArrayList<C_Expression> arguments) {
		this.arguments = arguments;
	}

	@Override
	public String toString() {
		return "LogicalDepth [type=" + type + ", arguments=" + arguments + "]";
	}

}
