/**
 * 
 */
package org.gcube.portlets.user.tdcolumnoperation.shared.operation;

import java.io.Serializable;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @May 29, 2014
 *
 */
public class TdIndexValue implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3566268350257048352L;
	
	public static final Integer INDEX_MIN_DEFAULT_VALUE = 0;
	public static final Integer INDEX_MAX_DEFAULT_VALUE = Integer.MAX_VALUE;
	public static final Integer INDEX_UNDEFINED_DEFAULT_VALUE = Integer.MIN_VALUE;
	
	public static final String SUBSTRING_UNDEFINED_DEFAULT_VALUE = null;
	
	private int index = INDEX_UNDEFINED_DEFAULT_VALUE;
	private String subString = SUBSTRING_UNDEFINED_DEFAULT_VALUE;
	
	
	/**
	 * 
	 */
	public TdIndexValue() {
	}
	/**
	 * 
	 */
	public TdIndexValue(String subString) {
		this.subString = subString;
	}
	
	public TdIndexValue(int index) {
		this.index = index;
	}

	
	public int getIndex() {
		return index;
	}

	public String getSubString() {
		return subString;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setSubString(String subString) {
		this.subString = subString;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TdIndexValue [index=");
		builder.append(index);
		builder.append(", subString=");
		builder.append(subString);
		builder.append("]");
		return builder.toString();
	}
	
	/*
	private void validateSubustring(){
		if(subString==null)
			subString = "";
	}
	
	private void validateIndex(){
		if(index<=-1)
			index = 0;
	}*/

}
