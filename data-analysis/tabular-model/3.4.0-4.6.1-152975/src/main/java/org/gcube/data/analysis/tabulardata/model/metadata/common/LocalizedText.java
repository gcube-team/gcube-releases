package org.gcube.data.analysis.tabulardata.model.metadata.common;


public interface LocalizedText {

	public String getValue();

	/**
	 * 
	 * @return ISO-1 two letters locale code
	 */
	public String getLocale();

}