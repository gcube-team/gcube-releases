/**
 * 
 */
package org.gcube.informationsystem.impl.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.entity.resource.ConfigurationTemplate;
import org.gcube.informationsystem.model.entity.resource.Software;
import org.gcube.informationsystem.model.relation.isrelatedto.IsConfiguredBy;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = IsConfiguredBy.NAME)
public class IsConfiguredByImpl<Out extends Software, In extends ConfigurationTemplate>
		extends IsRelatedToImpl<Out, In> implements IsConfiguredBy<Out, In> {

	protected IsConfiguredByImpl() {
		super();
	}

	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public IsConfiguredByImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
