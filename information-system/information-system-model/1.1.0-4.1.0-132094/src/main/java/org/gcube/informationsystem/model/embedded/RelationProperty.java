package org.gcube.informationsystem.model.embedded;

import org.gcube.informationsystem.impl.embedded.RelationPropertyImpl;
import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.entity.Entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Relation_Property
 */
@JsonDeserialize(as=RelationPropertyImpl.class)
public interface RelationProperty extends Embedded {
	
	public static final String NAME = "RelationProperty"; //RelationProperty.class.getSimpleName();

	/**
	 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
	 * The referential integrity value have their meaning taking in account the
	 * {@link Relation} direction. So that is the action to be made when an 
	 * action occurs on source {@link Entity}.
	 */
	public enum ReferentiaIntegrity {
		
		/**
		 * When the source {@link Entity} is deleted also the target 
		 * {@link Entity} is deleted if there are no other incoming 
		 * {@link Relation}.  
		 */
		onDeleteCascadeWhenOrphan,
		
		/**
		 * When the source {@link Entity} is deleted also the target 
		 * {@link Entity} is always deleted.
		 */
		onDeleteCascade, 
		
		/**
		 * When the source {@link Entity} is deleted the target {@link Entity} 
		 * is keep. This is the standard behavior also if no integrity is 
		 * declared. 
		 */
		onDeleteKeep
		
	}

	@ISProperty
	public ReferentiaIntegrity getReferentialIntegrity();
	
	public void setReferentialIntegrity(ReferentiaIntegrity referentialIntegrity);
	
	@ISProperty
	public AccessPolicy getPolicy();
	
	public void setPolicy(AccessPolicy accessPolicy);
	
}
