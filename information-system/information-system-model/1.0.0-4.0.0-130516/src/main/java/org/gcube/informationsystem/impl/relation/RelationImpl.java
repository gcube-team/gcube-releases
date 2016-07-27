/**
 * 
 */
package org.gcube.informationsystem.impl.relation;

import org.gcube.informationsystem.model.embedded.Header;
import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.relation.Relation;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class RelationImpl<Out extends Entity, In extends Entity> implements Relation<Out, In> {

	protected Header header;
	
	protected Out source;
	protected In target;
	
	protected RelationProperty relationProperty;
	
	protected RelationImpl(Out source, In target, RelationProperty relationProperty){
		this.source = source;
		this.target = target;
		this.relationProperty = relationProperty;
	}
	
	@Override
	public Header getHeader() {
		return header;
	}
	
	@Override
	public Out getSource() {
		return null;
	}

	@Override
	public In getTarget() {
		return null;
	}

	@Override
	public RelationProperty getRelationProperty() {
		return this.relationProperty;
	}


}
