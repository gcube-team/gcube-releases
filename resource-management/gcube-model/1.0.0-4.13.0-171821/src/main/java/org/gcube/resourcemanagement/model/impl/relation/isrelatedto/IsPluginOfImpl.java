package org.gcube.resourcemanagement.model.impl.relation.isrelatedto;

import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.resourcemanagement.model.reference.entity.resource.Plugin;
import org.gcube.resourcemanagement.model.reference.entity.resource.Software;
import org.gcube.resourcemanagement.model.reference.relation.isrelatedto.IsPluginOf;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = IsPluginOf.NAME)
public class IsPluginOfImpl<Out extends Plugin, In extends Software> extends DependsOnImpl<Out,In>
		implements IsPluginOf<Out,In> {
	
	protected IsPluginOfImpl() {
		super();
	}
	
	public IsPluginOfImpl(Out source, In target, PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}
	
}
