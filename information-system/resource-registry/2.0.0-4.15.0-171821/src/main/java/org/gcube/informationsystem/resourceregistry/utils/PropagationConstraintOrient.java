package org.gcube.informationsystem.resourceregistry.utils;

import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class PropagationConstraintOrient extends ODocument implements org.gcube.informationsystem.model.reference.embedded.PropagationConstraint {
	
	public PropagationConstraintOrient() {
		super(PropagationConstraint.NAME);
	}
	
	protected PropagationConstraintOrient(String iClassName) {
		super(iClassName);
	}

	@Override
	public RemoveConstraint getRemoveConstraint() {
		return RemoveConstraint.valueOf((String) this.field(PropagationConstraint.REMOVE_PROPERTY));
	}

	@Override
	public void setRemoveConstraint(RemoveConstraint removeConstraint) {
		this.field(PropagationConstraint.REMOVE_PROPERTY, removeConstraint.name());
	}

	@Override
	public AddConstraint getAddConstraint() {
		return AddConstraint.valueOf((String) this.field(PropagationConstraint.ADD_PROPERTY));
	}

	@Override
	public void setAddConstraint(AddConstraint addConstraint) {
		this.field(PropagationConstraint.ADD_PROPERTY, addConstraint.name());
	}
	
}
