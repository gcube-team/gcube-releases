/**
 * 
 */
package org.gcube.portlets.user.tdcolumnoperation.shared;

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
	private static final long serialVersionUID = 2341132987652197369L;
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
	
	

}
