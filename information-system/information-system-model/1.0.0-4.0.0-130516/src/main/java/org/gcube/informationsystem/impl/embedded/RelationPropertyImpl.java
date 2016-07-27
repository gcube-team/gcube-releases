/**
 * 
 */
package org.gcube.informationsystem.impl.embedded;

import org.gcube.informationsystem.model.embedded.AccessPolicy;
import org.gcube.informationsystem.model.embedded.RelationProperty;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */

public class RelationPropertyImpl implements RelationProperty {

	protected ReferentiaIntegrity referentiaIntegrity;
	
	protected AccessPolicy accessPolicy;
	
	@Override
	public ReferentiaIntegrity getReferentialIntegrity() {
		return this.referentiaIntegrity;
	}

	@Override
	public void setReferentialIntegrity(ReferentiaIntegrity referentialIntegrity) {
		this.referentiaIntegrity = referentialIntegrity;
	}

	@Override
	public AccessPolicy getPolicy() {
		return this.accessPolicy;
	}

	@Override
	public void setPolicy(AccessPolicy accessPolicy) {
		this.accessPolicy = accessPolicy;
	}

}
