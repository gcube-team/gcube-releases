package org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.gis;

import static org.gcube.application.aquamaps.aquamapsservice.stubs.fw.AquaMapsServiceConstants.gisTypesNS;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.collections.StringArray;

@XmlRootElement(namespace=gisTypesNS)
public class TransectInfoType {
	@XmlElement(namespace=gisTypesNS)
	private boolean enabled;
	@XmlElement(namespace=gisTypesNS)
	private String table;
	@XmlElement(namespace=gisTypesNS, name="maxelements")
	private int maxElements;
	@XmlElement(namespace=gisTypesNS, name="minimumgap")
	private int minimumGap;
	@XmlElement(namespace=gisTypesNS)
	private StringArray fields;
	
	public TransectInfoType() {
		// TODO Auto-generated constructor stub
	}

	public TransectInfoType(boolean enabled, String table, int maxElements,
			int minimumGap, StringArray fields) {
		super();
		this.enabled = enabled;
		this.table = table;
		this.maxElements = maxElements;
		this.minimumGap = minimumGap;
		this.fields = fields;
	}

	/**
	 * @return the enabled
	 */
	public boolean enabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void enabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the table
	 */
	public String table() {
		return table;
	}

	/**
	 * @param table the table to set
	 */
	public void table(String table) {
		this.table = table;
	}

	/**
	 * @return the maxElements
	 */
	public int maxElements() {
		return maxElements;
	}

	/**
	 * @param maxElements the maxElements to set
	 */
	public void maxElements(int maxElements) {
		this.maxElements = maxElements;
	}

	/**
	 * @return the minimumGap
	 */
	public int minimumGap() {
		return minimumGap;
	}

	/**
	 * @param minimumGap the minimumGap to set
	 */
	public void minimumGap(int minimumGap) {
		this.minimumGap = minimumGap;
	}

	/**
	 * @return the fields
	 */
	public StringArray fields() {
		return fields;
	}

	/**
	 * @param fields the fields to set
	 */
	public void fields(StringArray fields) {
		this.fields = fields;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TransectInfoType [enabled=");
		builder.append(enabled);
		builder.append(", table=");
		builder.append(table);
		builder.append(", maxElements=");
		builder.append(maxElements);
		builder.append(", minimumGap=");
		builder.append(minimumGap);
		builder.append(", fields=");
		builder.append(fields);
		builder.append("]");
		return builder.toString();
	}
	
	
}
