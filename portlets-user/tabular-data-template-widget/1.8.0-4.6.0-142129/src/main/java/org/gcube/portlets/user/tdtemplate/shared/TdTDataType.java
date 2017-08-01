/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.shared;


import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 27, 2014
 *
 */
public class TdTDataType extends BaseModelData implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1597540853228358234L;
	protected String id;
	protected String name;

	protected TdTFormatReferenceIndexer formatReferenceIndexer;
	
	//USED TO SUBMIT OR UPDATE TEMPLATE
	protected TdTFormatReference formatReference;
	/**
	 * 
	 */
	public TdTDataType() {
	}
	
	/**
	 * @param name
	 * @param id
	 */
	public TdTDataType(String id, String name) {
		this.id = id;
		this.name = name;
		set("id", id);
		set("name", name);
	}
	
	/**
	 * @param name
	 * @param id
	 */
	public TdTDataType(String id, String name, TdTFormatReferenceIndexer formatRefIndexer) {
		this(id,name);
		this.formatReferenceIndexer = formatRefIndexer;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public TdTFormatReferenceIndexer getFormatReferenceIndexer() {
		return formatReferenceIndexer;
	}

	public void setFormatReferenceIndexer(
			TdTFormatReferenceIndexer formatReferenceIndexer) {
		this.formatReferenceIndexer = formatReferenceIndexer;
	}

	public TdTFormatReference getFormatReference() {
		return formatReference;
	}

	public void setFormatReference(TdTFormatReference reference) {
		this.formatReference = reference;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TdTDataType [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", formatReferenceIndexer=");
		builder.append(formatReferenceIndexer);
		builder.append(", formatReference=");
		builder.append(formatReference);
		builder.append("]");
		return builder.toString();
	}
}
