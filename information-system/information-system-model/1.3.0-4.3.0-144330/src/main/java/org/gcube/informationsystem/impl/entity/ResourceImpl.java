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
	public <F extends Facet > void addFacet(F facet) {
		consistsOf.add(new ConsistsOfImpl<Resource, Facet>(this, facet, null));
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void attachResource(IsRelatedTo relation) {
		String message = String.format(
				"%s Source %s is not this. %s != %s", ConsistsOf.NAME,
				Resource.NAME, relation.getSource(),
				this.toString());
		
		if (relation.getSource()==null){
			throw new RuntimeException(message);
		}
		
		if (relation.getSource().getHeader()!= null &&
			relation.getSource().getHeader().getUUID() != null &&
			this.header !=null &&
			this.header.getUUID() != null &&
			relation.getSource().getHeader().getUUID().compareTo(this.header.getUUID())!=0) {
			
			throw new RuntimeException(message);

		}
		
		//((RelationImpl) relation).setSource(this);
		isRelatedTo.add(relation);
	}

	@Override
	public List<? extends Facet> getIdentificationFacets() {
		List<Facet> identificationFacets = new ArrayList<>();
		for(ConsistsOf<? extends Resource, ? extends Facet> consistsOfInstance : consistsOf){
			if (IsIdentifiedBy.class.isAssignableFrom(consistsOfInstance.getClass())) {
				identificationFacets.add(consistsOfInstance.getTarget());
			}
		}
		return identificationFacets;
	}

	@Override
	public List<ConsistsOf<? extends Resource, ? extends Facet>> getConsistsOf() {
		return consistsOf;
	}

	@Override
	public List<IsRelatedTo<? extends Resource, ? extends Resource>> getIsRelatedTo() {
		return isRelatedTo;
	}

}
