package org.gcube.portlets.user.td.expressionwidget.client.multicolumn;

import java.util.Arrays;
import java.util.List;


/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public enum DepthOfExpressionType {	
	BOTTOM("Cond."),
	STARTOR("Or("),
	ENDOR(")"),
	STARTAND("And["),
	ENDAND("]"),
	COMMA(",");
	
	
	
	/**
	 * @param text
	 */
	private DepthOfExpressionType(final String id) {
		this.id = id;
	}

	private final String id;
	
	@Override
	public String toString() {
		return id;
	}
	
	public String getLabel() {
		return id;
	}
	
	public static List<DepthOfExpressionType> asList(){
		return Arrays.asList(values());
	}
	
	public static DepthOfExpressionType getColumnDataTypeFromId(String id) {
		for (DepthOfExpressionType type : values()) {
			if (type.id.compareToIgnoreCase(id) == 0) {
				return type;
			}
		}
		
		return null;
	}
	
	
	
}
