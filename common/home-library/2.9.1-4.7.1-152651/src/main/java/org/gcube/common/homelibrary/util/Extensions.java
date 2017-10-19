/**
 * 
 */
package org.gcube.common.homelibrary.util;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public enum Extensions {
	
	/**
	 * The report extension.
	 */
	REPORT("d4sR", "gr"),
	
	/**
	 * The report template extension. 
	 */
	REPORT_TEMPLATE("d4sT", "grt"),
	
	/**
	 * The query extension. 
	 */
	QUERY("","gq"),
	
	/**
	 * The metadata extension. 
	 */
	METADATA("","xml");
	
	
	
	protected String value;
	protected String name;
	
	Extensions(String name, String value)
	{
		this.name = name;
		this.value = value;
	}
	
	/**
	 * Return the extension value.
	 * @return the extension string.
	 */
	public String getValue()
	{
		return value;
	}

	public String getName() {
		return name;
	}

}