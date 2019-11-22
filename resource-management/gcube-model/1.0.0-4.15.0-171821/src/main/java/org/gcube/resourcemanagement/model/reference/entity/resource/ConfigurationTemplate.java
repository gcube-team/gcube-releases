/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.entity.resource;

import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.resourcemanagement.model.impl.entity.resource.ConfigurationTemplateImpl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Configuration_Template
 */
@JsonDeserialize(as=ConfigurationTemplateImpl.class)
public interface ConfigurationTemplate extends Resource {
	
	public static final String NAME = "ConfigurationTemplate"; //ConfigurationTemplate.class.getSimpleName();
	public static final String DESCRIPTION = "It represents a template for a configuration. It describe how a configuration has to be realized. E.g. Used to define the accounting configuration parameters template.";
	public static final String VERSION = "1.0.0";
	
}