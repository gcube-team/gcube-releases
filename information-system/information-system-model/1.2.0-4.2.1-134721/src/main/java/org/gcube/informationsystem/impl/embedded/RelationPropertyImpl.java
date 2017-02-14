/**
 * 
 */
package org.gcube.informationsystem.impl.embedded;

import org.gcube.informationsystem.model.embedded.AccessPolicy;
import org.gcube.informationsystem.model.embedded.RelationProperty;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=RelationProperty.NAME)
public class RelationPropertyImpl implements RelationProperty {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -369473095002699018L;

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
