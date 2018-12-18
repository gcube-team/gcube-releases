package org.gcube.resources.federation.fhnmanager.occopus.model;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.resources.federation.fhnmanager.api.type.OccopusInstanceSet;

@XmlRootElement
public class GetInfraResponse {
	  Map<String, OccopusInstanceSet> instanceSets;

	public Map<String, OccopusInstanceSet> getInstanceSets() {
		return instanceSets;
	}

	public void setInstanceSets(Map<String, OccopusInstanceSet> instanceSets) {
		this.instanceSets = instanceSets;
	}
	  
	  
	  
}
