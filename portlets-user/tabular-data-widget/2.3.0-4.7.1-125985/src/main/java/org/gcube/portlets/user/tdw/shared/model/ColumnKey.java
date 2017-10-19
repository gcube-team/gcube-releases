/**
 * 
 */
package org.gcube.portlets.user.tdw.shared.model;

import java.io.Serializable;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class ColumnKey implements Serializable {
	
	private static final long serialVersionUID = 5029536626273428710L;
	
	protected String jsonIndex;
	protected int index;
	
	public ColumnKey(){}
	
	/**
	 * @param jsonIndex
	 * @param index
	 */
	public ColumnKey(String jsonIndex, int index) {
		this.jsonIndex = jsonIndex;
		this.index = index;
	}

	/**
	 * @return the value
	 */
	public String getJSonIndex() {
		return jsonIndex;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ColumnKey [jsonIndex=");
		builder.append(jsonIndex);
		builder.append(", index=");
		builder.append(index);
		builder.append("]");
		return builder.toString();
	}
}
