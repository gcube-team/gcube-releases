/**
 * 
 */
package org.gcube.informationsystem.model;

import java.util.Arrays;

import org.gcube.informationsystem.impl.embedded.DummyEmbedded;
import org.gcube.informationsystem.impl.embedded.EmbeddedImpl;
import org.gcube.informationsystem.impl.entity.ContextImpl;
import org.gcube.informationsystem.impl.entity.DummyFacet;
import org.gcube.informationsystem.impl.entity.DummyResource;
import org.gcube.informationsystem.impl.entity.EntityImpl;
import org.gcube.informationsystem.impl.entity.FacetImpl;
import org.gcube.informationsystem.impl.entity.ResourceImpl;
import org.gcube.informationsystem.impl.relation.ConsistsOfImpl;
import org.gcube.informationsystem.impl.relation.DummyConsistsOf;
import org.gcube.informationsystem.impl.relation.DummyIsRelatedTo;
import org.gcube.informationsystem.impl.relation.IsParentOfImpl;
import org.gcube.informationsystem.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.impl.relation.RelationImpl;
import org.gcube.informationsystem.model.embedded.Embedded;
import org.gcube.informationsystem.model.entity.Context;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsParentOf;
import org.gcube.informationsystem.model.relation.IsRelatedTo;
import org.gcube.informationsystem.model.relation.Relation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 * Enumerates the basic type names.
 */
public enum AccessType {

	EMBEDDED(Embedded.class, Embedded.NAME, EmbeddedImpl.class, DummyEmbedded.class),
	
	CONTEXT(Context.class, Context.NAME, ContextImpl.class, null),
	IS_PARENT_OF(IsParentOf.class, IsParentOf.NAME, IsParentOfImpl.class, null),
	
	ENTITY(Entity.class, Entity.NAME, EntityImpl.class, null),
	RESOURCE(Resource.class, Resource.NAME, ResourceImpl.class, DummyResource.class),
	FACET(Facet.class, Facet.NAME, FacetImpl.class, DummyFacet.class),
		
	RELATION(Relation.class, Relation.NAME, RelationImpl.class, null),
	IS_RELATED_TO(IsRelatedTo.class, IsRelatedTo.NAME, IsRelatedToImpl.class, DummyIsRelatedTo.class),
	CONSISTS_OF(ConsistsOf.class, ConsistsOf.NAME, ConsistsOfImpl.class, DummyConsistsOf.class);
	
	private static Logger logger = LoggerFactory.getLogger(AccessType.class);
	
	private final Class<? extends ISManageable> clz;
	private final Class<? extends ISManageable> implementationClass;
	private final Class<? extends ISManageable> dummyImplementationClass;
	
	private final String name;
	private final String lowerCaseFirstCharacter;
	
	<ISM extends ISManageable, ISMC extends ISM, ISMD extends ISMC>
	AccessType(Class<ISM> clz, String name, Class<ISMC> implementationClass, Class<ISMD> dummyImplementationClass){
		this.clz = clz;
		this.implementationClass = implementationClass;
		this.dummyImplementationClass = dummyImplementationClass;
		this.name = name;
		this.lowerCaseFirstCharacter = name.substring(0, 1).toLowerCase() + name.substring(1);
	}
	
	@SuppressWarnings("unchecked")
	public <ISM extends ISManageable> Class<ISM> getTypeClass(){
		return (Class<ISM>) clz;
	}
	
	@SuppressWarnings("unchecked")
	public <ISM extends ISManageable, ISMC extends ISM> Class<ISMC> getImplementationClass() {
		return (Class<ISMC>) implementationClass;
	}
	
	@SuppressWarnings("unchecked")
	public <ISM extends ISManageable, ISMC extends ISM, ISMD extends ISMC> Class<ISMD> getDummyImplementationClass() {
		return (Class<ISMD>) dummyImplementationClass;
	}
	
	public String getName(){
		return name;
	}
	
	public String lowerCaseFirstCharacter() {
		return lowerCaseFirstCharacter;
	}
	
	@Override
	public String toString(){
		return name;
	}
	
	public static AccessType getAccessType(Class<?> clz) {
		AccessType ret  =null;
		
		AccessType[] accessTypes = AccessType.values();
		for (AccessType accessType : accessTypes) {
			Class<? extends ISManageable> typeClass = accessType.getTypeClass();
			if (typeClass.isAssignableFrom(clz)) {
				if(ret==null || ret.getTypeClass().isAssignableFrom(typeClass)){
					ret = accessType;
				}
			}
		}
		
		if(ret !=null){
			return ret;
		}else{
			String error = String
					.format("The provided class %s does not belong to any of defined AccessTypes %s",
							clz.getSimpleName(), Arrays.toString(accessTypes));
			logger.trace(error);
			throw new RuntimeException(error);
		}
	}
}
