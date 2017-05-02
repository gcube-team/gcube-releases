/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.shared;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 10, 2015
 *
 */
public class TdTTimePeriod implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5877800144378111274L;
	
	private String name;
	private Map<String, String> valueFormats;
	
	public TdTTimePeriod(){}
	/**
	 * @param name
	 * @param valueFormats
	 */
	public TdTTimePeriod(String name, Map<String, String> valueFormats) {
		super();
		this.name = name;
		this.valueFormats = valueFormats;
	}
	public String getName() {
		return name;
	}
	
	public Map<String, String> getValueFormats() {
		return valueFormats;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setValueFormats(Map<String, String> valueFormats) {
		this.valueFormats = valueFormats;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TdTTimePeriod [name=");
		builder.append(name);
		builder.append(", valueFormats=");
		builder.append(valueFormats);
		builder.append("]");
		return builder.toString();
	}
}
