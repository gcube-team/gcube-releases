/**
 * 
 */
package org.gcube.portlets.user.tdcolumnoperation.shared.operation;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @May 29, 2014
 *
 */
public class TdSplitOperationModel extends TdBaseOperationModel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6239099927576299729L;
	
	private String regexValue = null;
	
	private TdIndexValue startIndex = null;
	private TdIndexValue endIndex = null;
	
	/**
	 * 
	 */
	public TdSplitOperationModel() {
	}


	/**
	 * @param tableId
	 * @param columnName
	 * @param operatorId
	 */
	public TdSplitOperationModel(Long tableId, String columnName,String operatorId) {
		super(tableId, columnName, operatorId);
	}


	public String getRegexValue() {
		return regexValue;
	}

	public TdIndexValue getStartIndex() {
		return startIndex;
	}

	public TdIndexValue getEndIndex() {
		return endIndex;
	}

	public void setRegexValue(String regexValue) {
		this.regexValue = regexValue;
	}

	public void setStartIndex(TdIndexValue startIndex) {
		this.startIndex = startIndex;
	}

	public void setEndIndex(TdIndexValue endIndex) {
		this.endIndex = endIndex;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TdMergeOperationModel [regexValue=");
		builder.append(regexValue);
		builder.append(", startIndex=");
		builder.append(startIndex);
		builder.append(", endIndex=");
		builder.append(endIndex);
		builder.append("]");
		return builder.toString();
	}
	
}
