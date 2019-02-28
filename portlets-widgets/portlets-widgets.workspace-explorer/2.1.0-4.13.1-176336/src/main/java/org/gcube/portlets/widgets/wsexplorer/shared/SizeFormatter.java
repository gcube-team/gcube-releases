/**
 * 
 */
package org.gcube.portlets.widgets.wsexplorer.shared;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jul 10, 2015
 */
public enum SizeFormatter {

	
	KB("KB", 1024),
	MB("MB", 1048576);
	
	private long value;
	private String unit;

	/**
	 * 
	 */
	private SizeFormatter(String unit, long value) {
		this.unit = unit;
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public long getValue() {
		return value;
	}

	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}
	
	
}
