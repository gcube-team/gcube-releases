package org.gcube.rest.index.common.entities;

import org.w3c.dom.Document;

public interface ExternalEndpointMethods {
	
	public ExternalEndpointResponse sendGet(String url) throws ExternalEndpointException;
	public Document loadXMLFromString(String xml) throws ExternalEndpointException;
	public ExternalEndpointInfo fillExternalEnpointInfo(Document doc, String descriptionUrl);

}
