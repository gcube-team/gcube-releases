/**
 * 
 */
package org.gcube.informationsystem.model.entity;

import java.util.List;
import java.util.UUID;

import org.gcube.informationsystem.model.annotations.Abstract;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsRelatedTo;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Resources
 */
@Abstract
// @JsonDeserialize(as=ResourceImpl.class) Do not uncomment to manage subclasses
public interface Resource extends Entity {
	
	public static final String NAME = "Resource"; //Resource.class.getSimpleName();
	
	@JsonIgnore
	public List<? extends Facet> getIdentificationFacets();
	
	//@JsonManagedReference
	public List<ConsistsOf<? extends Resource, ? extends Facet>> getConsistsOf();
	
	//@JsonManagedReference
	public List<IsRelatedTo<? extends Resource, ? extends Resource>> getIsRelatedTo();
	
	
	public <F extends Facet> void addFacet(F facet);
	
	public <C extends ConsistsOf<? extends Resource, ? extends Facet>> void addFacet(C relation);
	
	
	public void attachFacet(UUID uuid);
	
	public void attachFacet(ConsistsOf<? extends Resource, ? extends Facet> relation);
	
	
	public void detachFacet(UUID uuid);
	
	
	public void attachResource(UUID uuid);
	
	public <I extends IsRelatedTo<? extends Resource, ? extends Resource>> void attachResource(I relation);
	
	
}
