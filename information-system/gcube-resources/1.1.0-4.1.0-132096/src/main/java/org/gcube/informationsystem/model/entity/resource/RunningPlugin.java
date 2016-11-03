/**
 * 
 */
package org.gcube.informationsystem.model.entity.resource;

import org.gcube.informationsystem.impl.entity.resource.RunningPluginImpl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Running_Plugin
 */
@JsonDeserialize(as=RunningPluginImpl.class)
public interface RunningPlugin extends EService {
	
	public static final String NAME = "RunningPlugin"; // RunningPlugin.class.getSimpleName();
	public static final String DESCRIPTION = "Collect Running Plugin information through the list of its facets";
	public static final String VERSION = "1.0.0";
	
}
