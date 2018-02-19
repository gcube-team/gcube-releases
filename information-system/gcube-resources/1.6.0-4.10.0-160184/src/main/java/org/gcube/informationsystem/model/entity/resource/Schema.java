/**
 * 
 */
package org.gcube.informationsystem.model.entity.resource;

import org.gcube.informationsystem.impl.entity.resource.SchemaImpl;
import org.gcube.informationsystem.model.entity.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Schema
 */
@JsonDeserialize(as=SchemaImpl.class)
public interface Schema extends Resource {
	
	public static final String NAME = "Schema"; // Schema.class.getSimpleName();
	public static final String DESCRIPTION = "Collect Schema information through the list of its facets";
	public static final String VERSION = "1.0.0";
}
