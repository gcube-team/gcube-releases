package org.gcube.informationsystem.model.embedded;

import org.gcube.informationsystem.impl.embedded.AccessPolicyImpl;
import org.gcube.informationsystem.model.annotations.ISProperty;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonDeserialize(as=AccessPolicyImpl.class)
public interface AccessPolicy extends Embedded {
	
	public static final String NAME = "AccessPolicy"; //AccessPolicy.class.getSimpleName();
	
	@ISProperty
	public ValueSchema getPolicy();

	public void setPolicy(ValueSchema policy);
	
	@ISProperty
	public String getNote();
	
	public void setNote(String note);
}
