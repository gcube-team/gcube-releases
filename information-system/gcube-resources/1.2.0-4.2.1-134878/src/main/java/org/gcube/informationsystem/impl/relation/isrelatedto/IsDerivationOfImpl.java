/**
 * 
 */
package org.gcube.informationsystem.impl.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.resource.Configuration;
import org.gcube.informationsystem.model.entity.resource.ConfigurationTemplate;
import org.gcube.informationsystem.model.relation.isrelatedto.IsDerivationOf;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=IsDerivationOf.NAME)
public class IsDerivationOfImpl<Out extends Configuration, In extends ConfigurationTemplate>
		extends IsRelatedToImpl<Out, In> implements
		IsDerivationOf<Out, In> {

	protected IsDerivationOfImpl(){
		super();
	}
	
	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public IsDerivationOfImpl(Out source, In target,
			RelationProperty relationProperty) {
		super(source, target, relationProperty);
	}

}
