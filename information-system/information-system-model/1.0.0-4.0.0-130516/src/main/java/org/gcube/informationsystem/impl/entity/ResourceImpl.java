/**
 * 
 */
package org.gcube.informationsystem.impl.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public abstract class ResourceImpl extends EntityImpl implements Resource {

	protected Map<Facet, RelationProperty> addedFacets;
	protected Map<String, RelationProperty> attachedFacets;
	protected List<Facet> detachedFacets;
	
	
	protected Map<Resource, RelationProperty> attachedResourceProfiles;
	protected List<Resource> detachedResourceProfiles;
	
	/**
	 * @param name
	 * @param description
	 * @param version
	 */
	protected ResourceImpl() {
		addedFacets = new HashMap<Facet, RelationProperty>();
		attachedFacets = new HashMap<String, RelationProperty>();
		detachedFacets = new ArrayList<>();
	}
	
	@Override
	public void addFacet(Facet facet) {
		addedFacets.put(facet, null);
		
	}

	@Override
	public void addFacet(Facet facet, RelationProperty relationProperty) {
		addedFacets.put(facet, relationProperty);
	}

	@Override
	public void attachFacet(String uuid) {
		attachedFacets.put(uuid, null);
	}

	@Override
	public void attachFacet(String uuid, RelationProperty relationProperty) {
		attachedFacets.put(uuid, relationProperty);
	}



	@Override
	public void attachResource(String uuid) {
		
	}

	@Override
	public void attachResource(String uuid,
			RelationProperty relationProperty) {

	}


}
