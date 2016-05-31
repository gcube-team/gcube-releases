package org.gcube.portlets.user.td.widgetcommonevent.shared.expression;

import java.io.Serializable;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class C_Expression implements Serializable {

	
	private static final long serialVersionUID = 7818512507606450235L;
	
	protected String id="Expression";
	protected String readableExpression;
	
	public C_Expression(){
		readableExpression="Expression()";
	}
	
	public String getId() {
		return id;
	}

	public String getReadableExpression() {
		return readableExpression;
	}

	public void setReadableExpression(String readableExpression) {
		this.readableExpression = readableExpression;
	}

	@Override
	public String toString() {
		return "C_Expression [id=" + id + ", readableExpression="
				+ readableExpression + "]";
	}

	
	

}
