/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.entity.resource;

import org.gcube.resourcemanagement.model.impl.entity.resource.VirtualServiceImpl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Virtual_Service
 */
@JsonDeserialize(as=VirtualServiceImpl.class)
public interface VirtualService extends Service {
	
	public static final String NAME = "VirtualService"; // VirtualService.class.getSimpleName();
	public static final String DESCRIPTION = "Collect Virtual Service information through the list of its facets";
	public static final String VERSION = "1.0.0";
}
