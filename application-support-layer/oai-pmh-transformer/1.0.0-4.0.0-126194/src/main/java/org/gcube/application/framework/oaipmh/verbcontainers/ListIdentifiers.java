package org.gcube.application.framework.oaipmh.verbcontainers;

import java.util.ArrayList;

import org.gcube.application.framework.oaipmh.constants.ResponseConstants;
import org.gcube.application.framework.oaipmh.objectmappers.Identifier;
import org.gcube.application.framework.oaipmh.tools.ElementGenerator;
import org.w3c.dom.Element;

public class ListIdentifiers {

	
	/**
	 * Gets an ArrayList of initialized "Identifier" objects
	 * @param identifiers
	 * @return
	 */
	public static Element formulateListIdentifiersElement(ArrayList<Identifier> identifiers, int cursor, int total){
		Element listIdentifiers = ElementGenerator.getDocument().createElement("ListIdentifiers");
		
		for(Identifier identifier : identifiers)
			listIdentifiers.appendChild(identifier.getIdentifier());
		//append the resumption token (if has more)
		Element resToken = ElementGenerator.getDocument().createElement("resumptionToken");
		resToken.setAttribute("completeListSize", String.valueOf(total));
		resToken.setAttribute("cursor", String.valueOf(cursor));
		resToken.appendChild(ElementGenerator.getDocument().createTextNode(String.valueOf(cursor + ResponseConstants.RESULTS_PER_PAGE)));
		listIdentifiers.appendChild(resToken);
		return listIdentifiers;
	}
	
}
