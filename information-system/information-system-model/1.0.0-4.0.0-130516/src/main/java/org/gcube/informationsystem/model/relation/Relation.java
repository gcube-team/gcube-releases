/**
 * 
 */
package org.gcube.informationsystem.model.relation;

import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.embedded.Header;
import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.Entity;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public interface Relation<Out extends Entity, In extends Entity> {
	
	public static final String NAME = Relation.class.getSimpleName();
	
	public static final String RELATION_PROPERTY = "relationProperty";
	
	public static final String HEADER_PROPERTY = Entity.HEADER_PROPERTY;
	
	@ISProperty(name=HEADER_PROPERTY, mandatory=true, nullable=false)
	public Header getHeader();
	
	public Out getSource();
	
	public In getTarget();
	
	@ISProperty(name=RELATION_PROPERTY)
	public RelationProperty getRelationProperty();
	
}
