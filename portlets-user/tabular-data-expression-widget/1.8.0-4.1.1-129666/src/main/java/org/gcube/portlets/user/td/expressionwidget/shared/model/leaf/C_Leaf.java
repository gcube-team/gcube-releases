package org.gcube.portlets.user.td.expressionwidget.shared.model.leaf;

import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class C_Leaf extends C_Expression {

	
	private static final long serialVersionUID = -2354159908167637688L;
	
	protected String id="Leaf";
	
	public C_Leaf(){
		this.readableExpression = "Leaf()";
	}
	
	@Override
	public String toString() {
		return "Leaf [id=" + id + "]";
	}
	
	@Override
	public String getId() {
		return id;
	}


}
