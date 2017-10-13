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
	
	protected List<ConsistsOf<? extends Resource, ? extends Facet>> consistsOfList;
	protected List<IsRelatedTo<? extends Resource, ? extends Resource>> isRelatedToList;
	
	/*
	private List<Facet> facets;
	@SuppressWarnings("rawtypes")
	private Map<Class<Relation>, List<Relation>> relationByClass;
	@SuppressWarnings("rawtypes")
	private Map<Entry<Class<Relation>, Class<Entity>>, List<Relation>> relationByClassAndTarget;
	private Map<Class<Entity>, List<Entity>> entityByClass;
	@SuppressWarnings("rawtypes")
	private Map<Entry<Class<Relation>, Class<Entity>>, List<Entity>> entityByClassAndTarget;
	*/
	
	/**
	 * @param name
	 * @param description
	 * @param version
	 */
	protected ResourceImpl() {
		super();
		consistsOfList = new ArrayList<>();
		isRelatedToList = new ArrayList<>();
		
		/*
		facets = new ArrayList<>();
		
		relationByClass = new HashMap<>();
		relationByClassAndTarget = new HashMap<>();
		
		entityByClass = new HashMap<>();
		entityByClassAndTarget = new HashMap<>();
		*/
	}
	
	/*
	@SuppressWarnings({ "rawtypes"})
	private void addRelationByClassAndTarget(Entry<Class<Relation>, Class<Entity>> entry, Relation relation){
		List<Relation> relations = relationByClassAndTarget.get(entry);
		if(relations==null){
			relations = new ArrayList<>();
			relationByClassAndTarget.put(entry, relations);
		}
		relations.add(relation);
		
		addRelationByClass(entry.getKey(), relation);
	}
	
	@SuppressWarnings({ "rawtypes"})
	private void addRelationByClass(Class<Relation> relationClass, Relation r){
		List<Relation> relations = (List<Relation>) relationByClass.get(relationClass);
		if(relations==null){
			relations = new ArrayList<>();
			relationByClass.put(relationClass, relations);
		}
		relations.add(r);
	}
	
	@SuppressWarnings({ "rawtypes"})
	private void addEntityByClassAndTarget(Entry<Class<Relation>, Class<Entity>> entry, Entity entity){
		List<Entity> entities = entityByClassAndTarget.get(entry);
		if(entities==null){
			entities = new ArrayList<>();
			entityByClassAndTarget.put(entry, entities);
		}
		entities.add(entity);
		
		addEntityByClass(entry.getValue(), entity);
	}
	
	private void addEntityByClass(Class<Entity> entityClass, Entity entity){
		List<Entity> entities = entityByClass.get(entityClass);
		if(entities==null){
			entities = new ArrayList<>();
			entityByClass.put(entityClass, entities);
		}
		entities.add(entity);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Entry<Class<Relation>, Class<Entity>> getEntry(Class<Relation> realtionClass, Class<Entity> entityClass){
		Map<Class<Relation>, Class<Entity>> mapForEntry = new HashMap<>();
		mapForEntry.put(realtionClass, entityClass);
		return (Entry<Class<Relation>, Class<Entity>>) mapForEntry.entrySet().toArray()[0];
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addRelation(Relation relation){
		Entity entity = (Entity) relation.getTarget();
		Entry<Class<Relation>, Class<Entity>> entry = getEntry((Class<Relation>) relation.getClass(), (Class<Entity>) entity.getClass());
		addRelationByClassAndTarget(entry, relation);
		addEntityByClassAndTarget(entry, entity);
	}
	*/
	
	@Override
	public void addFacet(UUID uuid) {
		Facet facet = new DummyFacet(uuid);
		addFacet(facet);
	}
	
	@Override
	public <F extends Facet > void addFacet(F facet) {
		ConsistsOf<Resource, Facet> consistsOf = new ConsistsOfImpl<Resource, Facet>(this, facet, null); 
		addFacet(consistsOf);
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
		consistsOfList.add(relation);
		/*
		addRelation(relation);
		facets.add(relation.getTarget());
		*/
	}

	@Override
	public void attachResource(UUID uuid) {
		Resource resource = new DummyResource(uuid);
		attachResource(resource);
	}
	
	@Override
	public <R extends Resource> void attachResource(R resource) {
		IsRelatedTo<Resource, Resource> isRelatedTo = new IsRelatedToImpl<Resource, Resource>(this, resource, null);
		attachResource(isRelatedTo);
	}
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void attachResource(IsRelatedTo relation) {
		String message = String.format(
				"%s Source %s is not this. %s != %s", IsRelatedTo.NAME,
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
		
		if(relation.getSource()!=this){
			relation.setSource(this);
		}
		
		isRelatedToList.add(relation);
		/*
		addRelation(relation);
		*/
	}
	
	@Override
	public List<? extends Facet> getIdentificationFacets() {
		List<Facet> identificationFacets = new ArrayList<>();
		for(ConsistsOf<? extends Resource, ? extends Facet> consistsOfInstance : consistsOfList){
			if (IsIdentifiedBy.class.isAssignableFrom(consistsOfInstance.getClass())) {
				identificationFacets.add(consistsOfInstance.getTarget());
			}
		}
		return identificationFacets;
	}

	@Override
	public List<ConsistsOf<? extends Resource, ? extends Facet>> getConsistsOf() {
		return consistsOfList;
	}
	
	@Override
	public <C extends ConsistsOf<? extends Resource, ? extends Facet>> List<C> getConsistsOf(Class<C> clz) {
		List<C> list = new ArrayList<>();
		for(ConsistsOf<? extends Resource, ? extends Facet> consistsOf : consistsOfList){
			if(clz.isInstance(consistsOf)){
				@SuppressWarnings("unchecked")
				C c = (C) consistsOf;
				list.add(c);
			}
		}
		return list;
	}
	
	@Override
	public List<IsRelatedTo<? extends Resource, ? extends Resource>> getIsRelatedTo() {
		return isRelatedToList;
	}
	
	@Override
	public <I extends IsRelatedTo<? extends Resource, ? extends Resource>> List<I> getIsRelatedTo(Class<I> clz) {
		List<I> list = new ArrayList<>();
		for(IsRelatedTo<? extends Resource, ? extends Resource> isRelatedTo : isRelatedToList){
			if(clz.isInstance(isRelatedTo)){
				@SuppressWarnings("unchecked")
				I i = (I) isRelatedTo;
				list.add(i);
			}
		}
		return list;
	}
	
	@Override
	public List<? extends Facet> getFacets() {
		List<Facet> list = new ArrayList<>();
		for(ConsistsOf<? extends Resource, ? extends Facet> consistsOf : consistsOfList){
			list.add(consistsOf.getTarget());
		}
		return list;
	}
	
	@Override
	public <F extends Facet> List<F> getFacets(Class<F> clz) {
		List<F> list = new ArrayList<>();
		for(ConsistsOf<? extends Resource, ? extends Facet> consistsOf : consistsOfList){
			if(clz.isInstance(consistsOf.getTarget())){
				@SuppressWarnings("unchecked")
				F f = (F) consistsOf.getTarget();
				list.add(f);
			}
		}
		return list;
	}
	
	@Override
	public <F extends Facet, C extends ConsistsOf<? extends Resource, F>> List<C> getConsistsOf(Class<C> clz, Class<F> facetClz) {
		List<C> list = new ArrayList<>();
		for(ConsistsOf<? extends Resource, ? extends Facet> consistsOf : consistsOfList){
			if(clz.isInstance(consistsOf)){
				if(facetClz.isInstance(consistsOf.getTarget())){
					@SuppressWarnings("unchecked")
					C c = (C) consistsOf;
					list.add(c);
				}
			}
		}
		return list;
		
	}
	
	@Override
	public <F extends Facet, C extends ConsistsOf<? extends Resource, F>> List<F> getFacets(Class<C> clz, Class<F> facetClz) {
		List<F> list = new ArrayList<>();
		for(ConsistsOf<? extends Resource, ? extends Facet> consistsOf : consistsOfList){
			if(clz.isInstance(consistsOf)){
				if(facetClz.isInstance(consistsOf.getTarget())){
					@SuppressWarnings("unchecked")
					F f = (F) consistsOf.getTarget();
					list.add(f);
				}
			}
		}
		return list;
	}
	
}
