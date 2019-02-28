/**
 * 
 */
package org.gcube.resourcemanagement.model.reference.entity.resource;

import org.gcube.informationsystem.model.reference.annotations.Abstract;
import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.resourcemanagement.model.impl.entity.resource.ServiceImpl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Luca Frosini (ISTI - CNR)
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Service
 */
@Abstract
@JsonDeserialize(as=ServiceImpl.class)
public interface Service extends Resource {

	public static final String NAME = "Service"; // Service.class.getSimpleName();
	public static final String DESCRIPTION = "Collect Service information through the list of its facets";
	public static final String VERSION = "1.0.0";
	
}
