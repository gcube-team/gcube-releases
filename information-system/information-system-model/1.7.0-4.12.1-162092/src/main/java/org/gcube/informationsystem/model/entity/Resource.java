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
	
	public static final String CONSISTS_OF_PROPERTY = "consistsOf";
	public static final String IS_RELATED_TO_PROPERTY = "isRelatedTo";
	
	@JsonIgnore
	public List<? extends Facet> getIdentificationFacets();
	
	// @JsonManagedReference
	public List<ConsistsOf<? extends Resource, ? extends Facet>> getConsistsOf();
	
	@JsonIgnore
	public <C extends ConsistsOf<? extends Resource, ? extends Facet>> List<C> getConsistsOf(Class<C> clz);
	
	@JsonIgnore
	public <F extends Facet, C extends ConsistsOf<? extends Resource, F>> List<C> getConsistsOf(Class<C> clz, Class<F> target);
	
	
	// @JsonManagedReference
	public List<IsRelatedTo<? extends Resource, ? extends Resource>> getIsRelatedTo();
	
	@JsonIgnore
	public <I extends IsRelatedTo<? extends Resource, ? extends Resource>> List<I> getIsRelatedTo(Class<I> clz);
	
	
	@JsonIgnore
	public List<? extends Facet> getFacets();
	
	@JsonIgnore
	public <F extends Facet> List<F> getFacets(Class<F> clz);
	
	@JsonIgnore
	public <F extends Facet, C extends ConsistsOf<? extends Resource, F>> List<F> getFacets(Class<C> clz, Class<F> target);
	
	
	public void addFacet(UUID uuid);
	
	public <F extends Facet> void addFacet(F facet);
	
	public <C extends ConsistsOf<? extends Resource, ? extends Facet>> void addFacet(C relation);
	
	
	
	public void attachResource(UUID uuid);
	
	public <R extends Resource> void attachResource(R resource);
	
	public <I extends IsRelatedTo<? extends Resource, ? extends Resource>> void attachResource(I relation);
	
}
