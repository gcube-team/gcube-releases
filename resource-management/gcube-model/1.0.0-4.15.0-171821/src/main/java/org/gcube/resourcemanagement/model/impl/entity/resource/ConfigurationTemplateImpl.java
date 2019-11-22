/**
 * 
 */
package org.gcube.resourcemanagement.model.impl.entity.resource;

import org.gcube.informationsystem.model.impl.entity.ResourceImpl;
import org.gcube.resourcemanagement.model.reference.entity.resource.ConfigurationTemplate;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=ConfigurationTemplate.NAME)
public class ConfigurationTemplateImpl extends ResourceImpl implements ConfigurationTemplate {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = 7118678229898232442L;
	
}
