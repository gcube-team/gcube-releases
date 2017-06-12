/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.er.relation;

import org.gcube.informationsystem.model.AccessType;
import org.gcube.informationsystem.model.relation.IsRelatedTo;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@SuppressWarnings("rawtypes")
public class IsRelatedToManagement extends RelationManagement<IsRelatedTo> {

	public IsRelatedToManagement() {
		super(AccessType.IS_RELATED_TO);
	}
	
	public IsRelatedToManagement(OrientGraph orientGraph) {
		super(AccessType.IS_RELATED_TO, orientGraph);
	}

}
