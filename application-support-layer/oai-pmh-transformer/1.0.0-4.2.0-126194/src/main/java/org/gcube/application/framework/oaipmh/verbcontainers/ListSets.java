package org.gcube.application.framework.oaipmh.verbcontainers;

import org.gcube.application.framework.oaipmh.objectmappers.Repository;
import org.gcube.application.framework.oaipmh.tools.ElementGenerator;
import org.w3c.dom.Element;

public class ListSets {
	
	public static Element formulateListSetsElement(Repository repo){
		Element listSetsElem = ElementGenerator.getDocument().createElement("ListSets");
		
		for(Object setSpec : repo.getSets().keySet()){
			String spec = (String)setSpec;
			String name = (String)repo.getSets().get(setSpec);
			Element setEl = ElementGenerator.getDocument().createElement("set");
			Element setSpecEl = ElementGenerator.getDocument().createElement("setSpec");
			setSpecEl.appendChild(ElementGenerator.getDocument().createTextNode(spec));
			Element setNameEl = ElementGenerator.getDocument().createElement("setName");
			setNameEl.appendChild(ElementGenerator.getDocument().createTextNode(name));
			setEl.appendChild(setSpecEl);
			setEl.appendChild(setNameEl);
			listSetsElem.appendChild(setEl);
		}
		return listSetsElem;
	}
	
	

}
