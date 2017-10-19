package org.gcube.resources.federation.fhnmanager.api.type;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by ggiammat on 9/6/16.
 */

@XmlRootElement
public class OccopusInfrastructure extends FHNResource {


	private String infrastructureTemplate;
	Map<String, OccopusInstanceSet> instanceSets;

	public OccopusInfrastructure() {

	}

	public Map<String, OccopusInstanceSet> getInstanceSets() {
		return instanceSets;
	}

	public void setInstanceSets(Map<String, OccopusInstanceSet> instanceSets) {
		this.instanceSets = instanceSets;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInfrastructureTemplate() {
		return infrastructureTemplate;
	}

	public void setInfrastructureTemplate(String infrastructureTemplate) {
		this.infrastructureTemplate = infrastructureTemplate;
	}

}
