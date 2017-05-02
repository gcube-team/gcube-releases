/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.shared;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Feb 11, 2015
 *
 */
public class TdTFormatReference extends BaseModelData implements Serializable, TdTFormatReferenceId{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3876954218713188261L;
	
	
	private String id;
	private String value;
	/**
	 * 
	 */
	public TdTFormatReference() {
		
	}
	
	/**
	 * @param id
	 * @param value
	 */
	public TdTFormatReference(String id, String value) {
		this.id = id;
		set("id", id);
		this.value = value;
		set("value", value);
	}

	@Override
	public String getId() {
		return id;
	}

	public String getExample() {
		return value;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setExample(String example) {
		this.value = example;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TdTFormatReference [id=");
		builder.append(id);
		builder.append(", value=");
		builder.append(value);
		builder.append("]");
		return builder.toString();
	}
	
}
