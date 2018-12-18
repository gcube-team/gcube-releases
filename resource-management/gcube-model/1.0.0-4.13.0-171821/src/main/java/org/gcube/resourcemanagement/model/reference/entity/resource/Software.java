/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.entity.resource;

import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.resourcemanagement.model.impl.entity.resource.SoftwareImpl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Software
 */
@JsonDeserialize(as=SoftwareImpl.class)
public interface Software extends Resource {

	public static final String NAME = "Software"; // Software.class.getSimpleName();
	public static final String DESCRIPTION = "Collect Software information through the list of its facets";
	public static final String VERSION = "1.0.0";
	
}
