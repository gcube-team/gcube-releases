/**
 * 
 */
package org.gcube.informationsystem.impl.entity.resource;

import org.gcube.informationsystem.model.entity.resource.RunningPlugin;

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
