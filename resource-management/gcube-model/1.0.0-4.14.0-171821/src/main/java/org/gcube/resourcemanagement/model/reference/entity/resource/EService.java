/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.entity.resource;

import org.gcube.resourcemanagement.model.impl.entity.resource.EServiceImpl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#E-Service
 */
@JsonDeserialize(as=EServiceImpl.class)
public interface EService extends Service {
	
	public static final String NAME = "EService"; // EService.class.getSimpleName();
	public static final String DESCRIPTION = "Collect Electronic Service (aka Running Service) information through the list of its facets";
	public static final String VERSION = "1.0.0";
	
	public static EService getInstance() {
		return new EServiceImpl();
	}
	
}
