package org.gcube.application.framework.oaipmh.verbcontainers;

import java.util.ArrayList;

import org.gcube.application.framework.oaipmh.constants.ResponseConstants;
import org.gcube.application.framework.oaipmh.objectmappers.Identifier;
import org.gcube.application.framework.oaipmh.objectmappers.Record;
import org.gcube.application.framework.oaipmh.tools.ElementGenerator;
import org.w3c.dom.Element;

public class ListRecords {

	/**
	 * Gets an ArrayList of initialized "Identifier" objects
	 * @param identifiers
	 * @return
	 */
	public static Element formulateListRecordsElement(ArrayList<Record> records, int cursor, int total){
		Element listRecords = ElementGenerator.getDocument().createElement("ListRecords");
		
		for(Record record : records)
			listRecords.appendChild(record.getRecordElement());
		//append the resumption token (if has more)
		Element resToken = ElementGenerator.getDocument().createElement("resumptionToken");
		resToken.setAttribute("completeListSize", String.valueOf(total));
		resToken.setAttribute("cursor", String.valueOf(cursor));
		resToken.appendChild(ElementGenerator.getDocument().createTextNode(String.valueOf(cursor + ResponseConstants.RESULTS_PER_PAGE)));
		listRecords.appendChild(resToken);
		
		
		return listRecords;
	}
	
	
}
