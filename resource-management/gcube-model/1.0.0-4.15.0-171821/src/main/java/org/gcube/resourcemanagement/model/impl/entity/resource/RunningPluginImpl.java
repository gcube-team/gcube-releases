/**
 * 
 */
package org.gcube.resourcemanagement.model.impl.entity.resource;

import org.gcube.resourcemanagement.model.reference.entity.resource.RunningPlugin;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=RunningPlugin.NAME)
public class RunningPluginImpl extends EServiceImpl implements RunningPlugin {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = 7954507291742946502L;

}
