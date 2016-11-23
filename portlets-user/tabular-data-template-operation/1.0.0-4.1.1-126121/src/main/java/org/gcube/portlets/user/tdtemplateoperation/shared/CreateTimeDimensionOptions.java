/**
 * 
 */
package org.gcube.portlets.user.tdtemplateoperation.shared;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Mar 20, 2014
 *
 */
public enum CreateTimeDimensionOptions {

	YEAR("Year", "Year"),
	YEAR_QUARTER("Year_Quarter", "Year, Quarter of Year"),
	YEAR_MONTH("Year_Month", "Year, Month"),
	YEAR_MONTH_DAY("Year_Month_Day", "Year, Month, Day");
	
	private String id;
	private String label;

	/**
	 * 
	 */
	private CreateTimeDimensionOptions(String id, String label) {
		this.id = id;
		this.label = label;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
