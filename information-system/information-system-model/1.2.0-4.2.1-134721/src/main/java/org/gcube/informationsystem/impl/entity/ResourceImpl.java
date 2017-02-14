/**
 * 
 */
package org.gcube.informationsystem.impl.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.gcube.informationsystem.impl.relation.ConsistsOfImpl;
import org.gcube.informationsystem.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.exceptions.InvalidResource;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsIdentifiedBy;
import org.gcube.informationsystem.model.relation.IsRelatedTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=Resource.NAME)
public abstract class ResourceImpl extends EntityImpl implements Resource {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -3117918737458706846L;

	private static Logger logger = LoggerFactory.getLogger(ResourceImpl.class);
	
	protected List<ConsistsOf<? extends Resource, ? extends Facet>> consistsOf;
	protected List<IsRelatedTo<? extends Resource, ? extends Resource>> isRelatedTo;
	
	/**
	 * @param name
	 * @param description
	 * @param version
	 */
	protected ResourceImpl() {
		super();
		consistsOf = new ArrayList<>();
		isRelatedTo = new ArrayList<>();
	}

	
	
	@Override
	public <F extends Facet >void addFacet(F facet) {
		consistsOf.add(new ConsistsOfImpl<Resource, Facet>(this, facet, null));
		//addedFacets.put(facet, new ConsistsOfImpl<Resource, Facet>(this, facet, null));
	}

	@Override
	public <C extends ConsistsOf<? extends Resource, ? extends Facet>> void addFacet(C relation) {
		if (relation.getSource() != this) {
			String message = String.format(
					"%s Source %s is not this. %s != %s", ConsistsOf.NAME,
					Resource.NAME, relation.getSource().toString(),
					this.toString());
			logger.error(message);
			throw new RuntimeException(message);
		}
		consistsOf.add(relation);
	}

	/*
	@Override
	public <F extends Facet> void removeFacet(F facet) {
		throw new UnsupportedOperationException();
	}
	*/
	
	@Override
	public void attachFacet(UUID uuid) {
		Facet facet = new DummyFacet(uuid);
		consistsOf.add(new ConsistsOfImpl<Resource, Facet>(this, facet, null));
	}

	@Override
	public void attachFacet(ConsistsOf<? extends Resource, ? extends Facet> relation) {
		consistsOf.add(relation);
	}

	@Override
	public void detachFacet(UUID uuid) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void attachResource(UUID uuid) {
		Resource resource = new DummyResource(uuid);
		isRelatedTo.add(new IsRelatedToImpl<Resource, Resource>(this, resource, null));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void attachResource(@SuppressWarnings("rawtypes") IsRelatedTo relation) {
		if (relation.getSource() != this) {
			String message = String.format(
					"%s Source %s is not this. %s != %s", ConsistsOf.NAME,
					Resource.NAME, relation.getSource().toString(),
					this.toString());
			logger.error(message);
			throw new RuntimeException(message);
		}
		isRelatedTo.add(relation);
	}

	/*
	@Override
	public void detachResource(UUID uuid) {
		throw new UnsupportedOperationException();
	}
	*/

	@Override
	public List<? extends Facet> getIdentificationFacets() {
		List<Facet> identificationFacets = new ArrayList<>();
		/*
		for (UUID uuid : attachedFacets.keySet()) {
			ConsistsOf<Resource, Facet> consistOf = attachedFacets.get(uuid);
			if (IsIdentifiedBy.class.isAssignableFrom(consistOf.getClass())) {
				identificationFacets.add(consistOf.getTarget());
			}
		}
		
		for(Facet facet : addedFacets.keySet()){
			ConsistsOf<Resource, Facet> consistOf = addedFacets.get(facet);
			if (IsIdentifiedBy.class.isAssignableFrom(consistOf.getClass())) {
				identificationFacets.add(facet);
			}
		}
		*/
		for(ConsistsOf<? extends Resource, ? extends Facet> consistsOfInstance : consistsOf){
			if (IsIdentifiedBy.class.isAssignableFrom(consistsOfInstance.getClass())) {
				identificationFacets.add(consistsOfInstance.getTarget());
			}
		}
		return identificationFacets;
	}

	@Override
	public void validate() throws InvalidResource {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<ConsistsOf<? extends Resource, ? extends Facet>> getConsistsOf() {
		/*
		List<ConsistsOf<Resource, Facet>> consistsOf = new ArrayList<>();
		consistsOf.addAll(attachedFacets.values());
		consistsOf.addAll(addedFacets.values());
		*/
		return consistsOf;
	}

	@Override
	public List<IsRelatedTo<? extends Resource, ? extends Resource>> getIsRelatedTo() {
		/*
		List<IsRelatedTo<Resource, Resource>> isRelatedTo = new ArrayList<>();
		isRelatedTo.addAll(attachedResources.values());
		*/
		return isRelatedTo;
	}

}
