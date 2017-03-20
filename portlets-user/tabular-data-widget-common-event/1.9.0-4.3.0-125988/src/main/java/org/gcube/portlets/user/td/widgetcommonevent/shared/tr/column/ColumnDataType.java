package org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public enum ColumnDataType {
	Boolean("Boolean"), Geometry("Geometry"), Integer("Integer"), Numeric(
			"Numeric"), Date("Date"), Text("Text");

	/**
	 * @param text
	 */
	private ColumnDataType(final String id) {
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
	
	public String getId(){
		return id;
	}
	
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public static ColumnDataType getColumnDataTypeFromId(String id) {
		if(id==null||id.isEmpty()) return null;
		
		for (ColumnDataType columnDataType : values()) {
			if (columnDataType.id.compareToIgnoreCase(id) == 0) {
				return columnDataType;
			}
		}
		return null;
	}
	
	public static List<ColumnDataType> asList() {
		List<ColumnDataType> list=Arrays.asList(values());
		return list;
	}
	

}
