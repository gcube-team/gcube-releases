/**
 * 
 */
package org.gcube.informationsystem.impl.entity.resource;

import org.gcube.informationsystem.impl.entity.ResourceImpl;
import org.gcube.informationsystem.model.entity.resource.ConfigurationTemplate;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
@JsonTypeName(value=ConfigurationTemplate.NAME)
public class ConfigurationTemplateImpl extends ResourceImpl implements ConfigurationTemplate {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = 7118678229898232442L;
	
}
