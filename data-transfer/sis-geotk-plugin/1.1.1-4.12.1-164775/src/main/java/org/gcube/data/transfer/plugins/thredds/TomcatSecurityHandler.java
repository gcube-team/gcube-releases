package org.gcube.data.transfer.plugins.thredds;

import java.io.File;
import java.io.IOException;

import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class TomcatSecurityHandler {

	XPathHelper helper;
	
	public TomcatSecurityHandler(String securityFile) throws SAXException, IOException {
		helper=CommonXML.getHelper(CommonXML.getDocument(new File(securityFile)));		
	}
	
	public String getThreddsAdminUser() {
		return getAdminUserElement().getAttribute("username");
	}
	
	public String getThreddsAdminPassword() {
		return getAdminUserElement().getAttribute("password");
	}
	
	public Element getAdminUserElement() {
		return (Element) helper.evaluateForNodes("//*[local-name()='user'][contains(@roles,'tdsConfig')]").item(0);
	}
}
