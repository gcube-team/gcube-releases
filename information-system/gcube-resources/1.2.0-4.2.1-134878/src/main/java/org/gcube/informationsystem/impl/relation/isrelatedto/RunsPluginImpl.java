/**
 * 
 */
package org.gcube.informationsystem.impl.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.resource.Plugin;
import org.gcube.informationsystem.model.entity.resource.RunningPlugin;
import org.gcube.informationsystem.model.relation.isrelatedto.RunsPlugin;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=RunsPlugin.NAME)
public class RunsPluginImpl<Out extends RunningPlugin, In extends Plugin>
		extends IsRelatedToImpl<Out, In> implements
		RunsPlugin<Out, In> {
	
	protected RunsPluginImpl(){
		super();
	}

	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public RunsPluginImpl(Out source, In target,
			RelationProperty relationProperty) {
		super(source, target, relationProperty);
	}

}
