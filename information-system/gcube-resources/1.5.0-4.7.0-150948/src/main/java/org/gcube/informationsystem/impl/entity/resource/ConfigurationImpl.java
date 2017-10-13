/**
 * 
 */
package org.gcube.informationsystem.impl.entity.resource;

import org.gcube.informationsystem.model.entity.resource.Configuration;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=Configuration.NAME)
public class ConfigurationImpl extends ConfigurationTemplateImpl implements Configuration {

	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = -5517329395666754079L;
	
}
