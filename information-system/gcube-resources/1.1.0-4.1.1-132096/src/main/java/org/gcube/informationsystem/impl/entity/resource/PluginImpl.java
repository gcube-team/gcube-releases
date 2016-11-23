/**
 * 
 */
package org.gcube.informationsystem.impl.entity.resource;

import org.gcube.informationsystem.model.entity.resource.Plugin;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@JsonTypeName(value=Plugin.NAME)
public class PluginImpl extends SoftwareImpl implements Plugin {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = 8531011342130252545L;

}
