package org.gcube.portlets.user.td.widgetcommonevent.shared.tr;


public enum TableType {
	GENERIC("Generic"),
	CODELIST("Codelist"), 
	DATASET("Dataset");
	
	
	/**
	 * @param text
	 */
	private TableType(final String id) {
		this.id = id;
	}

	private final String id;
	
	@Override
	public String toString() {
		return id;
	}
	
	public String getTableTypeLabel() {
		return id;
	}
	
	
	public static TableType getColumnDataTypeFromId(String id) {
		for (TableType tableType : values()) {
			if (tableType.id.compareToIgnoreCase(id) == 0) {
				return tableType;
			}
		}
		return null;
	}
	
	
}
