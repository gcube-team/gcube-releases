package org.gcube.resourcemanagement.model.impl.relation.isrelatedto;

import org.gcube.informationsystem.model.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.resourcemanagement.model.reference.entity.resource.ConfigurationTemplate;
import org.gcube.resourcemanagement.model.reference.entity.resource.Service;
import org.gcube.resourcemanagement.model.reference.relation.isrelatedto.IsCustomizedBy;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = IsCustomizedBy.NAME)
public class IsCustomizedByImpl<Out extends Service, In extends ConfigurationTemplate>
		extends IsRelatedToImpl<Out, In> implements IsCustomizedBy<Out, In> {

	protected IsCustomizedByImpl() {
		super();
	}

	public IsCustomizedByImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
