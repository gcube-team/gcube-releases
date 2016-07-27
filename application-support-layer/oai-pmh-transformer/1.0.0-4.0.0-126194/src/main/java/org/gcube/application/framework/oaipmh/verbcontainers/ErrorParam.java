package org.gcube.application.framework.oaipmh.verbcontainers;

import org.gcube.application.framework.oaipmh.tools.ElementGenerator;
import org.w3c.dom.Element;

public class ErrorParam {

	public static Element formulateErrorElement(String message){
		Element errorElem = ElementGenerator.getDocument().createElement("error");
		errorElem.setAttribute("code", "badArgument");
		errorElem.appendChild(ElementGenerator.getDocument().createTextNode(message));
		return errorElem;
	}
	
	
}
