package org.gcube.application.framework.oaipmh.verbcontainers;

import org.gcube.application.framework.oaipmh.objectmappers.Record;
import org.gcube.application.framework.oaipmh.tools.ElementGenerator;
import org.w3c.dom.Element;

public class GetRecord {
	
	
	/**
	 * <b>Never call this function! Use Response.getGetRecordResponse() with Record as the input Element.</b>
	 * Simply wraps the created record element into a parent "GetRecord" node. 
	 * @param record The record element created through the Record class (createRecordByTemplate static function)
	 * @return
	 */
	public static Element formulateGetRecordElement(Record record){
		Element getRecord = ElementGenerator.getDocument().createElement("GetRecord");
		getRecord.appendChild(record.getRecordElement());
		return getRecord;
	}
	
	
}
