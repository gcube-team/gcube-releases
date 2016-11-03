/**
 * 
 */
package org.gcube.informationsystem.model.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.isrelatedto.RunsPluginImpl;
import org.gcube.informationsystem.model.entity.resource.Plugin;
import org.gcube.informationsystem.model.entity.resource.RunningPlugin;
import org.gcube.informationsystem.model.relation.IsRelatedTo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#runs
 */
@JsonDeserialize(as=RunsPluginImpl.class)
public interface RunsPlugin<Out extends RunningPlugin, In extends Plugin> 
	extends IsRelatedTo<Out, In> {

	public static final String NAME = "RunsPlugin"; //RunsPlugin.class.getSimpleName();
	
}
