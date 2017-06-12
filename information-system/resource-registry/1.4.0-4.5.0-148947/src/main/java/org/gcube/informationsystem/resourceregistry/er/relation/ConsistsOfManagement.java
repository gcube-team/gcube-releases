/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.er.relation;

import org.gcube.informationsystem.model.AccessType;
import org.gcube.informationsystem.model.relation.ConsistsOf;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@SuppressWarnings("rawtypes")
public class ConsistsOfManagement extends RelationManagement<ConsistsOf> {

	public ConsistsOfManagement() {
		super(AccessType.CONSISTS_OF);
	}

	public ConsistsOfManagement(OrientGraph orientGraph) {
		super(AccessType.CONSISTS_OF, orientGraph);
	}
	
}
