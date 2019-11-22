/**
 * 
 */
package org.gcube.informationsystem.model.impl.embedded;

import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=PropagationConstraint.NAME)
public class PropagationConstraintImpl extends EmbeddedImpl implements PropagationConstraint {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -4708881022038107688L;

	@JsonFormat(shape=JsonFormat.Shape.STRING)
	@JsonProperty(value=REMOVE_PROPERTY)
	protected RemoveConstraint removeConstraint;
	
	@JsonFormat(shape=JsonFormat.Shape.STRING)
	@JsonProperty(value=ADD_PROPERTY)
	protected AddConstraint addConstraint;
	
	public PropagationConstraintImpl(){
		super();
	}

	@Override
	public RemoveConstraint getRemoveConstraint() {
		return this.removeConstraint;
	}

	@Override
	public void setRemoveConstraint(RemoveConstraint removeConstraint) {
		this.removeConstraint = removeConstraint;
	}

	@Override
	public AddConstraint getAddConstraint() {
		return this.addConstraint;
	}

	@Override
	public void setAddConstraint(AddConstraint addConstraint) {
		this.addConstraint = addConstraint;
	}
	
}
