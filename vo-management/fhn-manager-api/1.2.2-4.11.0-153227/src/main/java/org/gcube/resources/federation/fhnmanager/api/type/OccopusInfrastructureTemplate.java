package org.gcube.resources.federation.fhnmanager.api.type;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by ggiammat on 9/6/16.
 */
@XmlRootElement
public class OccopusInfrastructureTemplate extends FHNResource {
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
	
	private Set<NodeDefinition> setnd;

	public Set<NodeDefinition> getSetnd() {
		return setnd;
	}

	public void setSetnd(Set<NodeDefinition> setnd) {
		this.setnd = setnd;
	}

}
