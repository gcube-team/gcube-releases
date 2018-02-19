/**
 * 
 */
package org.gcube.informationsystem.orientdb.hooks;

import org.gcube.informationsystem.impl.embedded.PropagationConstraintImpl;
import org.gcube.informationsystem.model.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.embedded.PropagationConstraint.AddConstraint;
import org.gcube.informationsystem.model.embedded.PropagationConstraint.RemoveConstraint;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsRelatedTo;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ConsistsOfHook extends RelationHook {

	public static PropagationConstraint propagationConstraint;

	static {
		propagationConstraint = new PropagationConstraintImpl();
		propagationConstraint.setRemoveConstraint(RemoveConstraint.cascadeWhenOrphan);
		propagationConstraint.setAddConstraint(AddConstraint.propagate);
	}

	public ConsistsOfHook() {
		super(ConsistsOf.NAME, propagationConstraint);
	}

	public ConsistsOfHook(ODatabaseDocument database) {
		super(database, IsRelatedTo.NAME, propagationConstraint);
	}

}