/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.entity.resource;

import org.gcube.resourcemanagement.model.impl.entity.resource.PluginImpl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Plugin
 */
@JsonDeserialize(as=PluginImpl.class)
public interface Plugin extends Software {
	
	public static final String NAME = "Plugin"; // Plugin.class.getSimpleName();
	public static final String DESCRIPTION = "Collect Plugin information through the list of its facets";
	public static final String VERSION = "1.0.0";
}
