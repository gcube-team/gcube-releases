package org.gcube.vremanagement.vremodeler.portallayout;

import java.sql.ResultSet;

import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.publisher.ISPublisher;
import org.gcube.common.core.resources.GCUBEGenericResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.vremanagement.vremodeler.db.DBInterface;
import org.gcube.vremanagement.vremodeler.impl.ServiceContext;
import org.gcube.vremanagement.vremodeler.impl.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class GenericResourcePortlet {
		
	public static void createResource(String vreId, String vreName) throws Exception{
		
		GCUBEGenericResource resource= GHNContext.getImplementation(GCUBEGenericResource.class);
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		DBInterface.connect();
		/*
		ResultSet rs= DBInterface.queryDB("select DISTINCT pf.PORTLETCLASS from PORTLETRELTOFUNCT as pf, VRERELATEDFUNCT as vf where vf.vreid='"+vreId+"' and pf.FUNCID=vf.FUNCID");
		Element root= doc.createElement("ListPortlets");
		while(rs.next())
			root.appendChild(Util.createTextElement(doc, "Portlet", rs.getString(1)));
		
		doc.appendChild(root);
		resource.setBody(Util.docToString(doc));
		
		resource.setDescription("List of selectable Portlet for the VRE "+vreName);
		resource.setName("AvailablePortlets");
		resource.setSecondaryType("PortletLayoutResource");
		resource.addScope(GCUBEScope.getScope(ServiceContext.getContext().getScope()+"/"+vreName));
		
		ISPublisher publisher = GHNContext.getImplementation(ISPublisher.class);
		publisher.registerGCUBEResource(resource,GCUBEScope.getScope(ServiceContext.getContext().getScope()+"/"+vreName) , ServiceContext.getContext());
		*/
	}
	
	
	
}
