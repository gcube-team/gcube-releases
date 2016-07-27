/**
 * 
 */
package org.gcube.informationsystem.model.entity;

import org.gcube.informationsystem.model.annotations.Abstract;
import org.gcube.informationsystem.model.embedded.RelationProperty;



/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@Abstract
public interface Resource extends Entity {
	
	public static final String NAME = Resource.class.getSimpleName();
	
	
	public void addFacet(Facet facet);
	
	public void addFacet(Facet facet, RelationProperty relationProperty);
	
	
	public void attachFacet(String uuid);
	
	public void attachFacet(String uuid, RelationProperty relationProperty);
	
	
	public void attachResource(String uuid);
	
	public void attachResource(String uuid, RelationProperty relationProperty);
	
	
}
