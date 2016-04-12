/**
 * 
 */
package org.gcube.portlets.user.tdcolumnoperation.shared.operation;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @May 29, 2014
 *
 */
public class TdMergeOperationModel extends TdBaseOperationModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6239099927576299729L;
	
	private String splitValue;
	
	/**
	 * 
	 */
	public TdMergeOperationModel() {
	}
	
	

	/**
	 * @param splitValue
	 */
	public TdMergeOperationModel(String splitValue) {
		super();
		this.splitValue = splitValue;
	}



	/**
	 * @return the splitValue
	 */
	public String getSplitValue() {
		return splitValue;
	}

	/**
	 * @param splitValue the splitValue to set
	 */
	public void setSplitValue(String splitValue) {
		this.splitValue = splitValue;
	}
}
