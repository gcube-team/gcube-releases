/**
 * 
 */
package org.gcube.portlets.user.tdtemplateoperation.shared;

import java.util.List;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 9, 2014
 *
 */
public class TdAggregateFunction extends TdBaseComboDataBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1566660274630462567L;
	private List<String> allowedDataTypesForName;


	/**
	 * 
	 */
	public TdAggregateFunction() {
	}

	
	/**
	 * @param id
	 * @param label
	 */
	public TdAggregateFunction(String id, String label) {
		super(id, label);
	}

	public List<String> getAllowedDataTypesForName() {
		return allowedDataTypesForName;
	}


	public void setAllowedDataTypesForName(List<String> allowedDataTypesForName) {
		this.allowedDataTypesForName = allowedDataTypesForName;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TdAggregateFunction [allowedDataTypesForName=");
		builder.append(allowedDataTypesForName);
		builder.append(", toString()=");
		builder.append(super.toString());
		builder.append("]");
		return builder.toString();
	}
}

