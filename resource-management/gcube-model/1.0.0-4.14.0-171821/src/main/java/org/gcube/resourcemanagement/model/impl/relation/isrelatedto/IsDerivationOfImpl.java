package org.gcube.resourcemanagement.model.impl.relation.isrelatedto;

import org.gcube.informationsystem.model.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.resourcemanagement.model.reference.entity.resource.Configuration;
import org.gcube.resourcemanagement.model.reference.entity.resource.ConfigurationTemplate;
import org.gcube.resourcemanagement.model.reference.relation.isrelatedto.IsDerivationOf;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = IsDerivationOf.NAME)
public class IsDerivationOfImpl<Out extends Configuration, In extends ConfigurationTemplate>
		extends IsRelatedToImpl<Out, In> implements IsDerivationOf<Out, In> {

	protected IsDerivationOfImpl() {
		super();
	}

	public IsDerivationOfImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
