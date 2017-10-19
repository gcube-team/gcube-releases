/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.shared;

import java.io.Serializable;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Oct 17, 2014
 *
 */
public abstract class TdBaseBeanModel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8706832318866972356L;
	protected String id;
	protected String label;
	
	/**
	 * 
	 */
	public TdBaseBeanModel() {
	}
	
	/**
	 * @param id
	 * @param label
	 * @param sourceClass
	 */
	public TdBaseBeanModel(String id, String label) {
		super();
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TdBaseBeanModel [id=");
		builder.append(id);
		builder.append(", label=");
		builder.append(label);
		builder.append("]");
		return builder.toString();
	}
	
}
