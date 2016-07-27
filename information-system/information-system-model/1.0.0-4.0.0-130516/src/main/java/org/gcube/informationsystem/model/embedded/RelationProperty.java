package org.gcube.informationsystem.model.embedded;

import org.gcube.informationsystem.model.annotations.ISProperty;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public interface RelationProperty extends Embedded {
	
	public static final String NAME = RelationProperty.class.getSimpleName();

	/**
	 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
	 * The referential integrity value have their meaning taking in account the
	 * Edge direction. So that is the action to be made when an action occurs on
	 * source Vertex.
	 */
	public enum ReferentiaIntegrity {
		
		/**
		 * When the source Vertex is deleted also the target Vertex is deleted if
		 * there are no other incoming edge.  
		 */
		onDeleteCascadeWhenOrphan,
		
		/**
		 * When the source Vertex is deleted also the target Vertex is always 
		 * deleted
		 */
		onDeleteCascade, 
		
		/**
		 * When the source Vertex is deleted also the target Vertex is keep. 
		 * This is the standard behavior also if no integrity is declared. 
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
