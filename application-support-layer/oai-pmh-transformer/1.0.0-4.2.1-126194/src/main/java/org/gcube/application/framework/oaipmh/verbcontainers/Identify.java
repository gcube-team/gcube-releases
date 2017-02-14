package org.gcube.application.framework.oaipmh.verbcontainers;


import org.gcube.application.framework.oaipmh.objectmappers.Repository;
import org.gcube.application.framework.oaipmh.tools.ElementGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class Identify {

	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(Identify.class);
	
	
	/**
	 * 
	 * @param repo The repository instance (should be first filled up with all the information about the repository)
	 * @return
	 */
	public static Element formulateIdentifyElement(Repository repo){
		Element identifyElem = ElementGenerator.getDocument().createElement("Identify");
		
		//start of mandatory fields of the reply
		Element repoName = ElementGenerator.getDocument().createElement("repositoryName");
		repoName.appendChild(ElementGenerator.getDocument().createTextNode(repo.getName()));
		identifyElem.appendChild(repoName);
		
		Element baseURL = ElementGenerator.getDocument().createElement("baseURL");
		baseURL.appendChild(ElementGenerator.getDocument().createTextNode(repo.getBaseURL()));
		identifyElem.appendChild(baseURL);
		
		Element protocolVersion = ElementGenerator.getDocument().createElement("protocolVersion");
		protocolVersion.appendChild(ElementGenerator.getDocument().createTextNode(repo.getProtocolVersion()));
		identifyElem.appendChild(protocolVersion);
		
		for(String email : repo.getAdminEMails()){
			Element adminEmail = ElementGenerator.getDocument().createElement("adminEmail");
			adminEmail.appendChild(ElementGenerator.getDocument().createTextNode(email));
			identifyElem.appendChild(adminEmail);
		}
		
		Element earliestDatestamp = ElementGenerator.getDocument().createElement("earliestDatestamp");
		earliestDatestamp.appendChild(ElementGenerator.getDocument().createTextNode(repo.getEarliestDatestamp()));
		identifyElem.appendChild(earliestDatestamp);
		
		Element deletedRecord = ElementGenerator.getDocument().createElement("deletedRecord");
		deletedRecord.appendChild(ElementGenerator.getDocument().createTextNode(repo.typeDeletedRecord()));
		identifyElem.appendChild(deletedRecord);
		
		Element granularity = ElementGenerator.getDocument().createElement("granularity");
		granularity.appendChild(ElementGenerator.getDocument().createTextNode(repo.getGranularity()));
		identifyElem.appendChild(granularity);
		//end of mandatory fields of the reply
		
		//optional elements:
		//here we could add compression elements
		//here we could add description elements
		
		return identifyElem;
		
	}
	
	
	
}
