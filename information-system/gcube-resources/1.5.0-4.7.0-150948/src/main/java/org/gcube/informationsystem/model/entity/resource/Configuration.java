/**
 * 
 */
package org.gcube.informationsystem.model.entity.resource;

import org.gcube.informationsystem.impl.entity.resource.ConfigurationImpl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Configuration
 */
@JsonDeserialize(as=ConfigurationImpl.class)
public interface Configuration extends ConfigurationTemplate {
	
	public static final String NAME = "Configuration"; // Configuration.class.getSimpleName();
	public static final String DESCRIPTION = "Collect Configuration information through the list of its facets";
	public static final String VERSION = "1.0.0";
	
}