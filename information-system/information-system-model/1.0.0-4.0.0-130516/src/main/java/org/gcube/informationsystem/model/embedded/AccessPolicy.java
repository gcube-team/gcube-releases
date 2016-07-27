package org.gcube.informationsystem.model.embedded;

import org.gcube.informationsystem.model.annotations.ISProperty;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public interface AccessPolicy extends Embedded {
	
	public static final String NAME = AccessPolicy.class.getSimpleName();
	
	@ISProperty
	public ValueSchema getPolicy();

	public void setPolicy(ValueSchema policy);
	
	@ISProperty
	public String getNote();
	
	public void setNote(String note);
}
