/**
 *
 */
package org.gcube.portlets.user.workspaceexplorerapp.shared;


/**
 * The Enum SizeFormatter.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 22, 2016
 */
public enum SizeFormatter {

	KB("KB", 1024),
	MB("MB", 1048576);

	private long value;
	private String unit;

	/**
	 * Instantiates a new size formatter.
	 *
	 * @param unit the unit
	 * @param value the value
	 */
	private SizeFormatter(String unit, long value) {
		this.unit = unit;
		this.value = value;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public long getValue() {
		return value;
	}

	/**
	 * Gets the unit.
	 *
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}
}
