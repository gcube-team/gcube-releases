package org.gcube.informationsystem.orientdb.hooks;

import org.gcube.informationsystem.impl.embedded.PropagationConstraintImpl;
import org.gcube.informationsystem.model.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.embedded.PropagationConstraint.AddConstraint;
import org.gcube.informationsystem.model.embedded.PropagationConstraint.RemoveConstraint;
import org.gcube.informationsystem.model.relation.IsRelatedTo;

import com.orientechnologies.orient.core.db.document.ODatabaseDocument;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class IsRelatedToHook extends RelationHook {

	public static PropagationConstraint propagationConstraint;

	static {
		propagationConstraint = new PropagationConstraintImpl();
		propagationConstraint.setRemoveConstraint(RemoveConstraint.keep);
		propagationConstraint.setAddConstraint(AddConstraint.unpropagate);
	}

	public IsRelatedToHook() {
		super(IsRelatedTo.NAME, propagationConstraint);
	}

	public IsRelatedToHook(ODatabaseDocument database) {
		super(database, IsRelatedTo.NAME, propagationConstraint);
	}

}