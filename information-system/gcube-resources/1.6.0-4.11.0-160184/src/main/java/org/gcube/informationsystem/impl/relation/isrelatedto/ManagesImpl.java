package org.gcube.informationsystem.impl.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.entity.resource.Dataset;
import org.gcube.informationsystem.model.entity.resource.Service;
import org.gcube.informationsystem.model.relation.isrelatedto.Manages;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = Manages.NAME)
public class ManagesImpl<Out extends Service, In extends Dataset> extends
		IsRelatedToImpl<Out, In> implements Manages<Out, In> {

	protected ManagesImpl() {
		super();
	}

	public ManagesImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
