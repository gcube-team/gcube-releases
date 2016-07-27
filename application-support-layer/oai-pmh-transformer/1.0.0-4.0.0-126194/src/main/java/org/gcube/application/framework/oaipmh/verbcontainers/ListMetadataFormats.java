package org.gcube.application.framework.oaipmh.verbcontainers;

import org.gcube.application.framework.oaipmh.objectmappers.Repository;
import org.gcube.application.framework.oaipmh.tools.ElementGenerator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ListMetadataFormats {

	public static Element formulateMetadataFormatsElement(Repository repo){
		Element metadataFormatElem = ElementGenerator.getDocument().createElement("ListMetadataFormats");
		if(repo.getSupportedMetadataPrefixes().contains("oai_dc"))
			metadataFormatElem.appendChild(ElementGenerator.oaidcMetadataFormat());
		
		//if the custom metadata object is set in the repository, include it in the ListMetadataFormats response
		if(repo.getCustomMetadataXSD() != null)
			metadataFormatElem.appendChild(ElementGenerator.customMetadataFormat(repo));
		
		return metadataFormatElem;
	}
	
}
