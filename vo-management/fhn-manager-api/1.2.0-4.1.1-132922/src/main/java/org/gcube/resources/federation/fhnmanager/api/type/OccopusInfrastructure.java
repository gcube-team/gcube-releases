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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

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
